/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination.benchmark;

import com.stulsoft.pmongo.pagination.MongoVerticle1;
import com.stulsoft.pmongo.pagination.StopWatch;
import com.stulsoft.pmongo.pagination.Utils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Stul
 */
public class Paginator1WithVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Paginator1WithoutVerticle.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertrx();
        var verticleName = MongoVerticle1.class.getName();
        var sw = new StopWatch();
        var numberOfTests = 100;
        var query = new JsonObject()
                .put("f3", new JsonObject().put("$gt", 10000));

        var collection = "monitor";
        var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "Paginator1WithVerticle");
        long[] collectionSize = {0L};
        client.count(collection, query, ar -> {
            if (ar.succeeded()) {
                collectionSize[0] = ar.result();
                client.close();
            }
        });

        var pageSize = 20;
        var maxPageNumber = 30000 / pageSize;
        var random = new Random();
        var minDuration = Long.MAX_VALUE;
        var maxDuration = Long.MIN_VALUE;
        var totalDuration = 0L;
        var durations = new LinkedList<Long>();

        var counter = new CountDownLatch(numberOfTests);
        long[] periodicId = {0L};

        vertx.deployVerticle(verticleName, deployResult -> {
            if (deployResult.succeeded()) {
                periodicId[0] = vertx.setPeriodic(2000, l -> {
                    var pageNumber = random.nextInt(maxPageNumber) + 1;
                    var request = new JsonObject()
                            .put("pageSize", pageSize)
                            .put("pageNumber", pageNumber)
                            .put("query", query);
                    var start = System.currentTimeMillis();
                    vertx
                            .eventBus()
                            .send(MongoVerticle1.EB_ADDRESS, request, response -> {
                                durations.add(System.currentTimeMillis() - start);
                                counter.countDown();
                            });
                });
            } else {
                logger.error("Failed deploying verticle " + verticleName + ". Error: " + deployResult.cause().getMessage());
                vertx.close();
                logger.info("<==main");
            }
        });
        try {
            counter.await(10, TimeUnit.MINUTES);
            vertx.cancelTimer(periodicId[0]);
            vertx.close();

            for (var duration : durations) {
                minDuration = Math.min(minDuration, duration);
                maxDuration = Math.max(maxDuration, duration);
                totalDuration += duration;
            }

            var averageDuration = totalDuration / numberOfTests;

            // Standard deviation
            long s1 = 0L;
            for (var duration : durations) {
                s1 += Math.pow((duration - averageDuration), 2);
            }
            var standardDeviation = Math.sqrt(1.0 * s1 / (numberOfTests - 1));

            logger.info("Test details: number of tests={}, page size={}, collection size={}",
                    numberOfTests, pageSize, collectionSize[0]);
            logger.info("Statistics. duration: min={} ms, max={} ms, average={} ms, standard deviation={} ms",
                    minDuration, maxDuration, averageDuration, Math.round(standardDeviation));
            logger.info(sw.duration());


            logger.info("<==main");

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            vertx.close();
            logger.info("<==main");
        }
    }
}

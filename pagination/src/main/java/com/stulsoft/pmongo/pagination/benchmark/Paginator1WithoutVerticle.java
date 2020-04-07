/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination.benchmark;

import com.stulsoft.pmongo.pagination.Paginator1;
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
public class Paginator1WithoutVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Paginator1WithoutVerticle.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertrx();

        var query = new JsonObject()
                .put("f3", new JsonObject().put("$gt", 10000));

        try {
            var sw = new StopWatch();
            var collection = "monitor";
            var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "Paginator1WithoutVerticle");
            long[] collectionSize = {0L};
            client.count(collection, query, ar -> {
                if (ar.succeeded())
                    collectionSize[0] = ar.result();
            });
            var pg = new Paginator1(client, "monitor");

            var numberOfTests = 100;
            var pageSize = 20;
            var maxPageNumber = 30000 / pageSize;
            var random = new Random();
            var minDuration = Long.MAX_VALUE;
            var maxDuration = Long.MIN_VALUE;
            var totalDuration = 0L;
            var durations = new LinkedList<Long>();

            for (var testNumber = 1; testNumber <= numberOfTests; ++testNumber) {
                var pageNumber = random.nextInt(maxPageNumber) + 1;
                var duration = readPage(pg, pageSize, pageNumber, query);
                durations.add(duration);
                totalDuration += duration;
                minDuration = Long.min(minDuration, duration);
                maxDuration = Long.max(maxDuration, duration);
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

            client.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        vertx.close();
        logger.info("<==main");
    }

    private static long readPage(Paginator1 paginator, int pageSize, int pageNumber, JsonObject query) {
        var counter = new CountDownLatch(1);
        var start = System.currentTimeMillis();
        try {
            paginator
                    .readPage(pageSize, pageNumber, query)
                    .subscribe(
                            objects -> counter.countDown(),
                            err -> {
                                logger.error(err.getMessage());
                                counter.countDown();
                            }
                    );
            counter.await(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return System.currentTimeMillis() - start;
    }
}

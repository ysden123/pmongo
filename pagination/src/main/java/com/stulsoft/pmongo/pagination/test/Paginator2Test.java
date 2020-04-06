/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination.test;

import com.stulsoft.pmongo.pagination.Paginator1;
import com.stulsoft.pmongo.pagination.Paginator2;
import com.stulsoft.pmongo.pagination.StopWatch;
import com.stulsoft.pmongo.pagination.Utils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Stul
 */
public class Paginator2Test {
    private static final Logger logger = LoggerFactory.getLogger(Paginator2Test.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertrx();

        try {
            var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "Paginator1Test");
            var pg = new Paginator2(client, "monitor");
            for (var pageNumber = 1; pageNumber <= 5; ++pageNumber) {
                showPage(pg, 7, pageNumber);
            }

            client.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        vertx.close();

        logger.info("<==main");
    }

    private static void showPage(Paginator2 paginator, int pageSize, int pageNumber) {
        var counter = new CountDownLatch(1);
        var sw = new StopWatch();
        paginator
                .readPage(pageSize, pageNumber)
                .subscribe(
                        response -> {
                            logger.info("pageSize={}, pageNumber={}, lastPageNumber={}",
                                    response.getInteger("pageSize"),
                                    response.getInteger("pageNumber"),
                                    response.getInteger("lastPageNumber"));
                            logger.info("number of rows is {}", response.getJsonArray("data").size());
                            response.getJsonArray("data").forEach(row -> logger.info(((JsonObject) row).encode()));
                            counter.countDown();
                        },
                        err -> {
                            logger.error(err.getMessage());
                            counter.countDown();
                        });
        try {
            counter.await(1, TimeUnit.MINUTES);
            logger.info("Duration {}", sw.duration());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}

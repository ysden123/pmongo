/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination.test;

import com.stulsoft.pmongo.pagination.Paginator1;
import com.stulsoft.pmongo.pagination.StopWatch;
import com.stulsoft.pmongo.pagination.Utils;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Stul
 */
public class Paginator1Test {
    private static final Logger logger = LoggerFactory.getLogger(Paginator1Test.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertrx();

        try {
            var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "Paginator1Test");
            var pg1 = new Paginator1(client, "monitor");
            for(var pageNumber = 1; pageNumber <= 5; ++pageNumber){
                showPage(pg1, 6, pageNumber);
            }

            client.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        vertx.close();

        logger.info("<==main");
    }

    private static void showPage(Paginator1 paginator, int pageSize,int pageNumber){
        var counter = new CountDownLatch(1);
        var sw = new StopWatch();
        paginator
                .readPage(pageSize, pageNumber)
                .subscribe(
                        objects -> {
                            logger.info("page number {}, number of rows is {}", pageNumber, objects.size());
                            objects.forEach(object -> logger.info(object.encode()));
                            counter.countDown();
                        },
                        err -> {
                            logger.error(err.getMessage());
                            counter.countDown();
                        });
        try {
            counter.await(1, TimeUnit.MINUTES);
            logger.info("Duration {}", sw.duration());
        }catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }

}

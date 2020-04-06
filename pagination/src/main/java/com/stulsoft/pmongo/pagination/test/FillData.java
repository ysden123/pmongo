/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination.test;

import com.stulsoft.pmongo.pagination.Paginator1;
import com.stulsoft.pmongo.pagination.Utils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.BulkWriteOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Stul
 */
public class FillData {
    private static final Logger logger = LoggerFactory.getLogger(FillData.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertrx();
        try {
            var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "FillData");

            var batchSize = 1000;
            var numberOfBulks = 30;
            var counter = new CountDownLatch(numberOfBulks);
            for (var bulkNumber = 1; bulkNumber <= numberOfBulks; ++bulkNumber) {
                var operations = new ArrayList<BulkOperation>();
                for (var operationNumber = 1; operationNumber <= batchSize; ++operationNumber) {
                    var document = new JsonObject()
                            .put("f1", String.format("f1 %d - %d", bulkNumber, operationNumber))
                            .put("f2", String.format("f2 %d - %d", bulkNumber, operationNumber))
                            .put("f3", bulkNumber * batchSize + operationNumber);
                    operations.add(BulkOperation.createInsert(document));
                }
                client.bulkWrite("monitor", operations, ar -> {
                    if (ar.failed()){
                        logger.error("Failed bulkWrite: " + ar.cause().getMessage());
                    }
                    counter.countDown();
                });
            }

            counter.await(10, TimeUnit.MINUTES);
            client.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        vertx.close();
        logger.info("<==main");
    }
}

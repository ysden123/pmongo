/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Test1 {
    private static final Logger logger = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) {
        var vertx = Utils.createVertx();

        vertx.setTimer(10, l -> {
            var client1 = Utils.client(vertx);
            logger.info("Client 1 was created");
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client1.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
        });

        vertx.setTimer(1000, l -> {
            var client2 = Utils.client(vertx);
            logger.info("Client 2 was created");
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
            client2.find("test_01", new JsonObject(), ar ->logger.debug("{}", ar.result()));
        });

        vertx.setTimer(5000, l -> vertx.close());
    }
}

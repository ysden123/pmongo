/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.pmongo.vertx.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Yuriy Stul
 */
public class UpdateById2 {
    private static final Logger logger = LoggerFactory.getLogger(UpdateById2.class);

    public static void main(String[] args) {
        logger.info("==>main");

        var vertx = Vertx.vertx();

        vertx.setTimer(100, __ -> {
            logger.info("In process");
            var mongoClient = Utils.client(vertx);
            try {
                mongoClient.findOne("woindex",
                                new JsonObject()
                                        .put("a", 1),
                                new JsonObject())
                        .onSuccess(document -> {
                            try {
                                var objectMapper = new ObjectMapper();
                                var updateObject = objectMapper.readValue(document.encode(), UpdateObject.class);
                                logger.info(updateObject.toString());
                                var id = updateObject._id();
                                logger.info("id={}", id);
                                var query = new JsonObject()
                                        .put("_id", id);
                                var update = new JsonObject()
                                        .put("$set", new JsonObject()
                                                .put("b", "one " + new Date()));
                                mongoClient.updateCollection("woindex", query, update, ar -> {
                                    if (ar.succeeded()) {
                                        logger.info("Updated! Matched={}, modified={}",
                                                ar.result().getDocMatched(),
                                                ar.result().getDocModified());
                                    } else {
                                        logger.error(ar.cause().getMessage(), ar.cause());
                                    }
                                    vertx.close();
                                });
                            } catch (Exception exception) {
                                logger.error(exception.getMessage(), exception);
                            }
                        });
            } catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
                vertx.close();
            }
        });
    }
}

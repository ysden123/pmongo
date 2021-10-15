/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.pmongo.vertx.update;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Yuriy Stul
 */
public class UpdateById1 {
    private static final Logger logger = LoggerFactory.getLogger(UpdateById1.class);

    public static void main(String[] args) {
        logger.info("==>main");

        var vertx = Vertx.vertx();

        vertx.setTimer(100, __ -> {
            logger.info("In process");
            var mongoClient = Utils.client(vertx);
            try {
                var id = new JsonObject().put("$oid", "616944e83064482b8c900c7f");
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
                vertx.close();
            }
        });
    }
}

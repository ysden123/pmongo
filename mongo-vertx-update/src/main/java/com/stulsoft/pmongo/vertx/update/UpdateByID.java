/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.pmongo.vertx.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Yuriy Stul
 */
public class UpdateByID {
    private static final Logger logger = LoggerFactory.getLogger(UpdateByID.class);

    private final Vertx vertx;
    private final MongoClient mongoClient;

    private UpdateByID(final Vertx vertx) {
        this.vertx = vertx;
        mongoClient = Utils.client(vertx);
    }

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Vertx.vertx();
        var updateByID = new UpdateByID(vertx);

        vertx.setTimer(10, __ -> {
            updateByID
                    .test1()
                    .andThen(updateByID.test2())
                    .subscribe(
                            () -> {
                                vertx.close();
                                logger.info("<==main");
                            },
                            error -> {
                                logger.error(error.getMessage(), error);
                                vertx.close();
                                logger.info("<==main");
                            }
                    );
        });
    }

    private Completable test1() {
        logger.info("==>test1");
        return Completable.create(source -> {
            logger.info("Inside test1");
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
                    source.onComplete();
                });
            } catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
                source.onComplete();
            }
        });
    }

    private Completable test2() {
        logger.info("==>test2");
        return Completable.create(source -> {
            logger.info("Inside test2");
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
                                    source.onComplete();
                                });
                            } catch (Exception exception) {
                                logger.error(exception.getMessage(), exception);
                                source.onComplete();
                            }
                        });
            } catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
                source.onComplete();
            }

        });
    }
}

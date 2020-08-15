/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.multiconn;

import com.stulsoft.pmongo.vertx.Utils;
import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author Yuriy Stul
 */
public class ManyConnections {
    private static final Logger logger = LoggerFactory.getLogger(ManyConnections.class);

    private static final Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertx();

        vertx.setTimer(10, l -> {
            var client = Utils.client(vertx, 5, 10);
            for (int i = 0; i < 25; ++i) {
                test2(vertx, client).subscribe(
                        () -> {
                            logger.info("Completed");
                            vertx.close();
                        },
                        err -> {
                            logger.error(err.getMessage());
                            vertx.close();
                        }

                );
            }
        });

        vertx.setTimer(10000, l -> {
            vertx.close();
            logger.info("<==main");
        });
    }

    private static Completable test1(Vertx vertx, MongoClient client) {
        return Completable.create(source -> {
            vertx.setTimer(123 + random.nextInt(100), l ->
                    client.find(
                            "test_01",
                            new JsonObject()
                                    .put("age", random.nextInt(35)),
                            ar -> {
                                if (ar.succeeded()) {
                                    logger.debug("Succeeded: {}", ar.result());
                                    source.onComplete();
                                } else {
                                    logger.error("Failed {}", ar.cause().getMessage());
                                    source.onError(ar.cause());
                                }
                            })
            );
        });
    }

    private static Completable test2(Vertx vertx, MongoClient client) {
        return Completable.create(source ->
                client.find(
                        "test_01",
                        new JsonObject()
                                .put("age", random.nextInt(35)),
                        ar -> {
                            if (ar.succeeded()) {
                                logger.debug("Succeeded: {}", ar.result());
                                source.onComplete();
                            } else {
                                logger.error("Failed {}", ar.cause().getMessage());
                                source.onError(ar.cause());
                            }
                        }));
    }
}

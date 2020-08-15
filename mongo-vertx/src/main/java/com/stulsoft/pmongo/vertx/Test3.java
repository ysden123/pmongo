/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx;

import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Using execCommand with aggregate and close client
 *
 * @author Yuriy Stul
 */
public class Test3 {
    private static final Logger logger = LoggerFactory.getLogger(Test3.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertx();

        vertx.setTimer(10, l -> {
            test02(vertx, 10)
                    .andThen(test02(vertx, 2000))
                    .subscribe(
                            () -> {
                                logger.debug("Completed");
                                vertx.close();
                            },
                            err -> {
                                logger.error(err.getMessage(), err);
                                vertx.close();
                            });
        });
    }

    private static Completable test02(Vertx vertx, long delay) {
        return Completable.create(source -> {
            vertx.setTimer(delay, l -> {
                logger.info("==>test02");
                try {
                    var client = Utils.client(vertx);
                    logger.info("Client was created");
                    var command = new JsonObject()
                            .put("aggregate", "test_01")
                            .put("pipeline", new JsonArray()
                                    .add(new JsonObject()
                                            .put("$match", new JsonObject())))
                            .put("cursor", new JsonObject());
                    client.runCommand(
                            "aggregate"
                            , command,
                            res -> {
                                if (res.succeeded()) {
                                    try {
//                                    logger.info("{}", res.result().encode());
                                        res
                                                .result()
                                                .getJsonObject("cursor")
                                                .getJsonArray("firstBatch")
                                                .forEach(jo -> {
                                                    var doc = (JsonObject) jo;
                                                    logger.debug("{}", doc.encode());
                                                });
                                        source.onComplete();
                                    } catch (Exception ex) {
                                        logger.error(ex.getMessage(), ex);
                                        source.onError(ex);
                                    }
                                } else {
                                    logger.error(res.cause().getMessage(), res.cause());
                                    source.onError(res.cause());
                                }
                                client.close();
                            });
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    source.onError(ex);
                }
            });
        });
    }
}

/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.multiconn;

import com.stulsoft.pmongo.vertx.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author Yuriy Stul
 */
public class DBServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DBServiceVerticle.class);

    public static final String EB_ADDRESS = DBServiceVerticle.class.getName();

    private MongoClient client;
    private Random random;

    public DBServiceVerticle() {
    }

    @Override
    public void start() throws Exception {
        super.start();

        random = new Random(System.currentTimeMillis());
        var dataSource = "dataSource" + Thread.currentThread().getId();
        logger.info("Starting with dataSource {}", dataSource);
        client = Utils.client(vertx, 5, 10, dataSource);
        vertx.eventBus().consumer(EB_ADDRESS, this::handler);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("Stopping");
    }

    private void handler(Message<String> msg) {
        vertx.setTimer(10 + random.nextInt(1000),
                l -> client.find(
                        "test_01",
                        new JsonObject()
                                .put("age", random.nextInt(35)),
                        ar -> {
                            if (ar.succeeded()) {
                                logger.debug("Succeeded: {}", ar.result());
                                msg.reply("Done");
                            } else {
                                logger.error(ar.cause().getMessage(), ar.cause());
/*
                                if (ar.cause() instanceof MongoWaitQueueFullException){
                                    var ex = (MongoWaitQueueFullException)ar.cause();
                                    logger.error("MWQFE: {}", ex.getMessage());
                                }else {
                                    logger.error("Failed {}", ar.cause().getMessage());
                                }
*/
                                msg.fail(123, ar.cause().getMessage());
                            }
                        }));
    }
}

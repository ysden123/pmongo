/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class MongoVerticle1 extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MongoVerticle1.class);

    public static final String EB_ADDRESS = "MongoVerticle1";

    private Paginator1 paginator;

    @Override
    public void start(Future<Void> startFuture) {
        var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "MongoVerticle1");
        paginator = new Paginator1(client, "monitor");
        vertx.eventBus().consumer(EB_ADDRESS, this::handler);
        logger.info("{} was started", this.getClass().getSimpleName());
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        logger.info("{} was stopped", this.getClass().getSimpleName());
        stopFuture.complete();
    }

    private void handler(Message<JsonObject> msg) {
        var pageSize = msg.body().getInteger("pageSize");
        var pageNumber = msg.body().getInteger("pageNumber");
        paginator
                .readPage(pageSize, pageNumber)
                .subscribe(
                        response -> msg.reply(new JsonArray(response)),
                        err -> msg.fail(1, err.getMessage())
                );
    }
}

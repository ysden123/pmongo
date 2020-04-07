/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class MongoVerticle2 extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MongoVerticle2.class);

    public static final String EB_ADDRESS = "MongoVerticle2";

    private Paginator2 paginator;

    @Override
    public void start(Future<Void> startFuture) {
        var client = MongoClient.createShared(vertx, Utils.mongoConfig(), "MongoVerticle2");
        paginator = new Paginator2(client, "monitor");
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
        var query = msg.body().getJsonObject("query");
        paginator
                .readPage(pageSize, pageNumber, query)
                .subscribe(
                        msg::reply,
                        err -> msg.fail(1, err.getMessage())
                );
    }
}

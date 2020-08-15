/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx;

import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * @author Yuriy Stul
 */
public interface Utils {
    static Vertx createVertx() {
        return Vertx.vertx(new VertxOptions()
                .setEventBusOptions(new EventBusOptions()
                        .setClustered(false)
                        .setIdleTimeout(0))
                .setMaxEventLoopExecuteTime(2000000000000L)
                .setMaxWorkerExecuteTime(60000000000000L)
                .setBlockedThreadCheckInterval(1000000));
    }

    static Completable deployVerticles(final Vertx vertx, final String[] verticleNames) {
        return Completable.create(source -> {
            Completable[] deployers = new Completable[verticleNames.length];
            for (int i = 0; i < verticleNames.length; ++i) {
                int theI = i;
                deployers[i] = Completable.create(
                        drSource -> vertx.deployVerticle(verticleNames[theI],
                                dr -> {
                                    if (dr.succeeded())
                                        drSource.onComplete();
                                    else
                                        drSource.onError(dr.cause());
                                }));
            }

            Completable.concatArray(deployers)
                    .subscribe(
                            source::onComplete,
                            source::onError
                    );
        });
    }

    static MongoClient client(final Vertx vertx) {
        var conf = new JsonObject()
                .put("connection_string", "mongodb://root:admin@localhost:27017")
                .put("db_name", "pmongo");
        return MongoClient.createShared(vertx, conf);
    }
}

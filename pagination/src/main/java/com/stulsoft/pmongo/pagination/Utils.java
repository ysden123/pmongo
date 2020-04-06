/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
/**
 * @author Yuriy Stul
 */
public class Utils {
    private Utils() {
    }

    public static Vertx createVertrx() {
        return Vertx.vertx(new VertxOptions()
                .setEventBusOptions(new EventBusOptions()
                        .setClustered(false)
                        .setIdleTimeout(0))
                .setMaxEventLoopExecuteTime(2000000000000L)
                .setMaxWorkerExecuteTime(60000000000000L)
                .setBlockedThreadCheckInterval(1000000));
    }

    public static JsonObject mongoConfig() {
        return new JsonObject()
                .put("connection_string", "mongodb://root:admin@localhost:27017")
                .put("db_name", "pmongo");
    }

}

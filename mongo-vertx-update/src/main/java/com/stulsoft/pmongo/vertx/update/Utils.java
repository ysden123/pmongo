/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.pmongo.vertx.update;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * @author Yuriy Stul
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static MongoClient client(final Vertx vertx) {
        try {
            var config = new JsonObject()
                    .put("connection_string", "mongodb://root:admin@localhost:27017")
                    .put("db_name", "testDb");
            return MongoClient.create(vertx, config);
        } catch (Exception exception) {
            logger.error("Failed creating Mongo client: " + exception.getMessage(), exception);
            System.exit(123);
            return null;
        }
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }
}

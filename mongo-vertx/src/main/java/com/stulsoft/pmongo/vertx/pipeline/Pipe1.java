/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.pipeline;

import com.stulsoft.pmongo.vertx.Utils;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Pipe1 {
    private static final Logger logger = LoggerFactory.getLogger(Pipe1.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertx();
        var mongoClient = Utils.client(vertx, "testDb");

        vertx.setTimer(
                100,
                __ -> pipe1(mongoClient)
                        .flatMap(___ -> findById(mongoClient))
                        .subscribe(___ -> {
                            mongoClient.close();
                            vertx.close();
                        }));
    }

    private static Single<String> pipe1(MongoClient mongoClient) {
        logger.info("==>pipe1");
        return Single.create(source -> {
            var start = System.currentTimeMillis();
            var pipeline = new JsonArray();
            var sort = new JsonObject()
                    .put("$sort", new JsonObject().put("t", -1));

            var group = new JsonObject()
                    .put("$group", new JsonObject()
                            .put("_id", "$a")
                            .put("theId", new JsonObject()
                                    .put("$first", "$_id"))
                            .put("a", new JsonObject()
                                    .put("$first", "$a"))
                            .put("t", new JsonObject()
                                    .put("$first", "$t"))
                    );

            var match = new JsonObject()
                    .put("$match", new JsonObject()
                            .put("$and", new JsonArray()
                                    .add(new JsonObject()
                                            .put("a", new JsonObject()
                                                    .put("$gt", 1)))));
            pipeline.add(sort);
            pipeline.add(group);
            pipeline.add(match);

            var cursor = new JsonObject().put("batchSize", 10_000);
            var command = new JsonObject()
                    .put("aggregate", "testAggCollection")
                    .put("pipeline", pipeline)
                    .put("cursor", cursor)
                    .put("allowDiskUse", true);
            mongoClient.runCommand("aggregate", command)
                    .onComplete(res -> {
                        if (res.succeeded()) {
                            logger.debug("Succeeded");
                            logger.debug("result: {}", res.result().encode());

                            var resArr = res.result()
                                    .getJsonObject("cursor")
                                    .getJsonArray("firstBatch");
                            for (int i = 0; i < resArr.size(); ++i) {
                                var entry = resArr.getJsonObject(i);
                                logger.debug("entry: {}", entry.encode());
                                logger.debug("Entry ID: {}", entry.getJsonObject("theId").getString("$oid"));
                                logger.debug("a = {}, t = {}", entry.getInteger("a"),
                                        entry.getInteger("t"));
                            }

                        } else {
                            logger.error("Failure: {}", res.cause().getMessage());
                        }
                        logger.debug("Completed in {} ms", System.currentTimeMillis() - start);
                        source.onSuccess("");
                    });
        });
    }

    private static Single<String> findById(MongoClient mongoClient) {
        logger.info("==>findById");
        return Single.create(source -> {
            var start = System.currentTimeMillis();
            var query = new JsonObject()
                    .put("_id", new JsonObject().put("$oid", "604c877cc29ab63a043837fe"));
            mongoClient.findOne("testAggCollection", query, null)
                    .onComplete(res -> {
                        if (res.succeeded()) {
                            logger.debug("Succeeded");
                            var document = res.result();
                            if (document == null) {
                                logger.debug("No document found");
                            } else {
                                logger.debug("Document: {}", document.encode());
                            }
                        } else {
                            logger.error("Failure: {}", res.cause().getMessage());
                        }
                        source.onSuccess("");
                        logger.debug("Completed in {} ms", System.currentTimeMillis() - start);
                    });
        });
    }
}

/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.pipeline;

import com.stulsoft.pmongo.vertx.Utils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.AggregateOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Pipe2 {
    private static final Logger logger = LoggerFactory.getLogger(Pipe2.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertx();
        var mongoClient = Utils.client(vertx, "testDb");

        vertx.setTimer(
                100,
                __ -> pipe1(mongoClient)
                        .flatMap(___ -> pipe1(mongoClient))
                        .flatMap(___ -> pipe2(mongoClient))
                        .flatMap(___ -> pipe2(mongoClient))
                        .flatMap(___ -> findById(mongoClient))
                        .onComplete(___ -> {
                            mongoClient.close();
                            vertx.close();
                        }));
    }

    private static Future<String> pipe1(MongoClient mongoClient) {
        logger.info("==>pipe1");
        var promise = Promise.<String>promise();
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
                            var document = resArr.getJsonObject(i);
                            logger.debug("Document: {}", document.encode());
                            logger.debug("Document ID: {}", document.getJsonObject("theId").getString("$oid"));
                            logger.debug("a = {}, t = {}", document.getInteger("a"),
                                    document.getInteger("t"));
                        }

                    } else {
                        logger.error("Failure: {}", res.cause().getMessage());
                    }
                    logger.debug("Completed in {} ms", System.currentTimeMillis() - start);
                    promise.complete("");
                });
        return promise.future();
    }

    private static Future<String> findById(MongoClient mongoClient) {
        logger.info("==>findById");
        var promise = Promise.<String>promise();
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
                    logger.debug("Completed in {} ms", System.currentTimeMillis() - start);
                    promise.complete("");
                });
        return promise.future();
    }

    private static Future<String> pipe2(MongoClient mongoClient) {
        logger.info("==>pipe2");
        var start = System.currentTimeMillis();
        var promise = Promise.<String>promise();
        try {
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

            AggregateOptions aggregateOptions = new AggregateOptions()
                    .setAllowDiskUse(true)
                    .setBatchSize(10_000);
            mongoClient.aggregateWithOptions("testAggCollection", pipeline, aggregateOptions)
                    .exceptionHandler(error -> logger.error("Failure: {}", error.getMessage()))
                    .handler(document -> {
                        logger.debug("document: {}", document);
                        logger.debug("Document ID: {}", document.getJsonObject("theId").getString("$oid"));
                        logger.debug("a = {}, t = {}", document.getInteger("a"),
                                document.getInteger("t"));

                    })
                    .endHandler(__ -> {
                        logger.debug("Completed in {} ms", System.currentTimeMillis() - start);
                        promise.complete("");
                    });
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            promise.complete("");
        }

        return promise.future();
    }
}

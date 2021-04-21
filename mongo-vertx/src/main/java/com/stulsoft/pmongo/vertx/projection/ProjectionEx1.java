/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.projection;

import com.stulsoft.pmongo.vertx.Utils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class ProjectionEx1 {
    private static final Logger logger = LoggerFactory.getLogger(ProjectionEx1.class);

    public static void main(String[] args) {
        var vertx = Utils.createVertx();

        vertx.setTimer(10, l -> {
            var client = Utils.client(vertx);
            client.find("test1", new JsonObject(), ar -> {
                logger.debug("(1) {}", ar.result());
            });

            client.find("test1", new JsonObject().put("b", "bbb"), ar -> {
                logger.debug("(2) {}", ar.result());
            });
// todo YS
            var findOptions=new FindOptions()
                    .setSort(new JsonObject().put("aaa",-1))
                    .setBatchSize(1000);

            client.findBatchWithOptions("collection", new JsonObject(), findOptions)
                    .handler(document -> logger.debug("{}", document.encode()))
                    .exceptionHandler(error -> logger.error(error.getMessage()))
                    .endHandler(__ -> logger.debug("Completed"));
// todo YS
            client.findWithOptions("test1", new JsonObject(),
                    new FindOptions().setFields(new JsonObject().put("b", 0)), ar -> {
                        logger.debug("(3) {}", ar.result());
                    });
        });

        vertx.setTimer(5000, l -> vertx.close());
    }
}

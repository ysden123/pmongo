/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.multiconn;

import com.stulsoft.pmongo.vertx.Utils;
import io.vertx.core.DeploymentOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class ManyConnectionWithVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ManyConnectionWithVerticle.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var vertx = Utils.createVertx();

        int n = 900;
//        int n = 500;
        vertx.deployVerticle(new EndVerticle(n));
        vertx.deployVerticle(
                DBServiceVerticle.class.getName(),
                new DeploymentOptions()
                        .setInstances(2),
                dr -> {
                    if (dr.succeeded()) {
                        for (int i = 1; i <= n; ++i) {
                            vertx.setTimer(10, l ->
                                    vertx.eventBus().request(DBServiceVerticle.EB_ADDRESS,
                                            "test",
                                            ar -> {
                                                if (ar.succeeded()) {
//                                            logger.info("Succeeded {}", ar.result().body());
                                                } else {
                                                    logger.error("Failed {}", ar.cause().getMessage());
                                                }
                                                vertx.eventBus().publish(EndVerticle.EB_ADDRESS, "Done");
                                            }));
                        }
                    } else {
                        logger.error("Failed deployment. {}", dr.cause().getMessage());
                        vertx.close();
                    }
                });
    }
}

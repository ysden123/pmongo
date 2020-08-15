/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.vertx.multiconn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yuriy Stul
 */
public class EndVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(EndVerticle.class);

    public static final String EB_ADDRESS = EndVerticle.class.getName();
    private final AtomicInteger count;

    public EndVerticle(int count) {
        this.count = new AtomicInteger(count);
    }

    @Override
    public void start() throws Exception {
        super.start();
        vertx.eventBus().consumer(EB_ADDRESS, this::handler);
        logger.info("Started");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        logger.info("Stopped");
    }

    private void handler(Message<String> msg) {
        if (count.decrementAndGet() <= 0)
            vertx.close();
    }
}

/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.jmongo.watch;

import com.stulsoft.pmongo.jmongo.Client;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

/**
 * @author Yuriy Stul
 */
public class ConnectionWatcher {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionWatcher.class);

    private int lastConnectionCount = 0;
    private int lastCurrentCount = 0;
    private boolean toRun = true;

    private void run() {

        Executors
                .newSingleThreadExecutor()
                .submit(() -> {
                    var client = Client.client();
                    var db = client.getDatabase("pmongo");
                    while (toRun) {
                        var status = db.runCommand(new Document("serverStatus", 1));
                        var activeConnectionCount = status.get("connections", Document.class).getInteger("active");
                        var currentCount = status.get("connections", Document.class).getInteger("current");
                        if (activeConnectionCount != lastConnectionCount || currentCount != lastCurrentCount) {
                            lastConnectionCount = activeConnectionCount;
                            lastCurrentCount = currentCount;
                            System.out.printf("Current = %d, connections = %d%n", lastCurrentCount, lastConnectionCount);
                        }
                        try {
                            Thread.sleep(250);
                        } catch (Exception ignore) {
                        }
                    }
                });
    }

    public static void main(String[] args) {
        var cw = new ConnectionWatcher();
        cw.run();
    }
}

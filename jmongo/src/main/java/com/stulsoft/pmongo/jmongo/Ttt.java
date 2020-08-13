/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.jmongo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Ttt {
    private static final Logger logger = LoggerFactory.getLogger(Ttt.class);

    public static void main(String[] args) {
        var client = Client.client();
        var db = client.getDatabase("pmongo");
        db.listCollectionNames().forEach(n -> logger.info("Collection {}", n));
        var status = db.runCommand(new Document("serverStatus", 1));
        logger.debug("Server status, connections: {}", status.get("connections", Document.class). toJson());
        client.close();
    }
}

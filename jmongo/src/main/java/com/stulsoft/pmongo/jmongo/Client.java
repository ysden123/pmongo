/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.jmongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * @author Yuriy Stul
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static MongoClient client() {
        var location = System.getenv("LOCATION");
        var pass=("office".equals(location))? "123456789": "admin";
        var credentials = MongoCredential
                .createCredential("root", "admin", pass.toCharArray());
        var client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder
                                        .hosts(Collections
                                                .singletonList(new ServerAddress("localhost",
                                                        27017))))
                        .credential(credentials)
                        .build());
        logger.debug("Connected to cluster: {}", client.getClusterDescription());
        return client;
    }
}

/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.spring;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;

/**
 * @author Yuriy Stul
 */
@Configuration
public class AppConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    @Bean(name = "AppMongoClient")
    public MongoClient mongoClient(@Autowired Environment env) {
        logger.info("==>mongoClient");
        var credentials = MongoCredential
                .createCredential(
                        env.getProperty("mongo.user"),
                        env.getProperty("mongo.authDB"),
                        env.getProperty("mongo.pass").toCharArray()
                );

        var client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder
                                        .hosts(Collections
                                                .singletonList(
                                                        new ServerAddress(env.getProperty("mongo.host"),
                                                                env.getProperty("mongo.port", Integer.class)))))
                        .credential(credentials)
                        .build());
        logger.debug("Connected to cluster: {}", client.getClusterDescription());
        return client;
    }
}

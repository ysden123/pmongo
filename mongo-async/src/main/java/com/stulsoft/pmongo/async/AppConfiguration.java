/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.async;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Objects;

/**
 * @author Yuriy Stul
 */
@Configuration
public class AppConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    @Bean(name = "AppMongoClient")
    public MongoClient mongoClient(@Autowired Environment env) {
        logger.info("==>mongoClient");
/*
        return MongoClients
                .create(Objects.requireNonNull(env.getProperty("mongo.connection_string")));
*/

        try {
            var credentials = MongoCredential
                    .createCredential(
                            Objects.requireNonNull(env.getProperty("mongo.user")),
                            Objects.requireNonNull(env.getProperty("mongo.authDB")),
                            Objects.requireNonNull(env.getProperty("mongo.pass")).toCharArray()
                    );

            var client = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder
                                            .hosts(Collections
                                                    .singletonList(
                                                            new ServerAddress(Objects.requireNonNull(env.getProperty("mongo.host")),
                                                                    Objects.requireNonNull(env.getProperty("mongo.port", Integer.class))))))
                            .credential(credentials)
                            .build());
            logger.debug("Connected to cluster: {}", client.getClusterDescription());
            return client;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}

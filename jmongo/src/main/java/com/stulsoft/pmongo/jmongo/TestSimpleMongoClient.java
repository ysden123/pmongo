/*
 * Copyright (c) 2021. Webpals
 */

package com.stulsoft.pmongo.jmongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

/**
 * @author Yuriy Stul
 */
public class TestSimpleMongoClient {
    private static final Logger logger = LogManager.getLogger(TestSimpleMongoClient.class);

    public static void main(String[] args) {
        logger.info("==>main");
        MongoClient client;
        try{
//            client = new MongoClient("mongodb://root:123456789@mongo:27017");
//            client = new MongoClient("mongodb://root:123456789@localhost");
            client = MongoClients.create("mongodb://root:123456789@mongo:27017");
            logger.info("Created");

            var db = client.getDatabase("ETLDB");
            logger.info("db received");
            var collection = db.getCollection("Users");
            logger.info("collection received");
            var users = collection.find(Filters.eq("name", "yuriy.s"));
            logger.info("users received: {}", users);
            Document user = users.first();
            logger.info("user received: {}", user);
            if (user != null){
                logger.info("user: {}", user);
            }
            client.close();
        }catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}

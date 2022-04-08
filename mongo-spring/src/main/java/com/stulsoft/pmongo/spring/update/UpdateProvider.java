/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.stulsoft.pmongo.spring.data.Test01Record;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Yuriy Stul
 */
@Service
public class UpdateProvider {
    private static final Logger logger = LoggerFactory.getLogger(UpdateProvider.class);

    private final MongoClient mongoClient;

    @Autowired
    public UpdateProvider(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    private void ttt() {
        logger.info("==>ttt");
//        update1();
//        update2();
    }

    private void update1() {
        logger.info("==>update1");
        try {
            var mongoOperations = new MongoTemplate(mongoClient, "pmongo");

            var query = new Query()
                    .addCriteria(Criteria.where("name").is("ys 06"));
            var document = mongoOperations.findOne(query, Document.class, "test_01");
            if (document != null) {
                document.put("name", "NAME UPDATED");
                var replaceQuery = new Document()
                        .append("_id", document.get("_id"));
                logger.info("replaceQuery={}", replaceQuery.toJson());
                logger.info("document={}", document.toJson());
                var updateResult = mongoClient
                        .getDatabase("pmongo")
                        .getCollection("test_01")
                        .replaceOne(replaceQuery, document);

                logger.info("updateResult={}, {} was modified", updateResult, updateResult.getModifiedCount());
            } else {
                logger.info("Did not find");
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

    private void update2() {
        logger.info("==>update2");
        try {
            var mongoOperations = new MongoTemplate(mongoClient, "pmongo");

            var query = new Query()
                    .addCriteria(Criteria.where("name").is("ys 06"));
            var document = mongoOperations.findOne(query, Test01Record.class, "test_01");
            if (document != null) {
                var updateDocument = new Test01Record(document._id(), "NAME UPDATED", document.sum(), document.age());
                ObjectMapper objectMapper = new ObjectMapper();
                var json = objectMapper.writeValueAsString(updateDocument);
                var documentToStore = Document.parse(json);
                documentToStore.remove("_id");
                var replaceQuery = new Document()
                        .append("_id", new ObjectId(document._id()));
                logger.info("replaceQuery={}", replaceQuery.toJson());
                logger.info("documentToStore={}", documentToStore.toJson());
                var updateResult = mongoClient
                        .getDatabase("pmongo")
                        .getCollection("test_01")
                        .replaceOne(replaceQuery, documentToStore);

                logger.info("updateResult={}, {} was modified", updateResult, updateResult.getModifiedCount());
            } else {
                logger.info("Did not find");
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}

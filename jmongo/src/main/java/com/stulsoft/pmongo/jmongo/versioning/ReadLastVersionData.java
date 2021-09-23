/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.jmongo.versioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.stulsoft.pmongo.jmongo.Client;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

/**
 * @author Yuriy Stul
 */
public class ReadLastVersionData {
    private static final Logger logger = LoggerFactory.getLogger(ReadLastVersionData.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        logger.info("==>main");

        try {
            test1();
            test2();
            test3();
            test4();
        }catch(Exception exception){
            logger.error(exception.getMessage(), exception);
        }
    }

    private static void test1() {
        logger.info("==>test1");
        var client = Client.client();
        var db = client.getDatabase("testDb");
        var collection = db.getCollection("testAggCollection");
        var start = System.currentTimeMillis();

        collection.aggregate(
                        Arrays.asList(
                                sort(Sorts.ascending("a")),
                                sort(Sorts.descending("t")),
                                group("$a",
                                        Accumulators.first("a", "$a"),
                                        Accumulators.first("t", "$t")),
                                project(Projections.fields(
                                        Projections.excludeId()
                                ))
                        )
                )
                .forEach(document -> logger.info("{}", document));
        logger.info("<==test1 in {}", System.currentTimeMillis() - start);
        client.close();
    }

    private static void test2() {
        logger.info("==>test2");
        var client = Client.client();
        var db = client.getDatabase("testDb");
        var collection = db.getCollection("testAggCollection");
        var objectMapper = new ObjectMapper();
        var start = System.currentTimeMillis();
        var map = new HashMap<Double, TestAggCollection>();
        collection
                .find(Document.class)
                .sort(Sorts.descending("a", "t"))
                .allowDiskUse(true)
                .map(document -> {
                    try {
                        return objectMapper.readValue(document.toJson(), TestAggCollection.class);
                    } catch (JsonProcessingException exception) {
                        logger.error(exception.getMessage());
                        return null;
                    }
                })
                .forEach(testAggCollection -> map.put(testAggCollection.a(), testAggCollection));
        map.values().stream().sorted(Comparator.comparing(TestAggCollection::a))
                .collect(Collectors.toList())
                .forEach(testAggCollection -> logger.info("{}", testAggCollection));
        logger.info("<==test2 in {}", System.currentTimeMillis() - start);
        client.close();
    }

    private static void test3() {
        logger.info("==>test3");
        var client = Client.client();
        var db = client.getDatabase("testDb");
        var collection = db.getCollection("testAggCollection");
        var start = System.currentTimeMillis();
        var map = new HashMap<Double, Document>();
        collection
                .find(Document.class)
                .allowDiskUse(true)
                .sort(ascending("a", "t"))
                .forEach(document -> map.put(document.getDouble("a"), document));
        map.values().stream().sorted(Comparator.comparing(o -> o.getDouble("a")))
                .map(document -> {
                    try{
                        return objectMapper.readValue(document.toJson(), TestAggCollection.class);
                    }catch(Exception exception){
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .forEach(testAggCollection -> logger.info("{}", testAggCollection));
        logger.info("<==test3 in {}", System.currentTimeMillis() - start);
        client.close();
    }

    /**
     * Find a last item
     */
    private static void test4() throws JsonProcessingException {
        logger.info("==>test4");
        var client = Client.client();
        var db = client.getDatabase("testDb");
        var collection = db.getCollection("testAggCollection");

        var document = collection
                .find(eq("a", 2.0))
                .allowDiskUse(true)
                .sort(descending("a", "t"))
                .first();
        if (document != null){
            logger.info("{}", objectMapper.readValue(document.toJson(), TestAggCollection.class));
        }else{
            logger.info("Did not found");
        }
        logger.info("<==test4");
    }
}

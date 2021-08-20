/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.spring;

import com.mongodb.client.MongoClient;
import com.stulsoft.pmongo.spring.data.Test01;
import com.stulsoft.pmongo.spring.data.Test012;
import com.stulsoft.pmongo.spring.data.Test013;
import com.stulsoft.pmongo.spring.data.Test02;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author Yuriy Stul
 */
@Service
public class Service1 {
    private static final Logger logger = LoggerFactory.getLogger(Service1.class);
    private final MongoClient client;

    public Service1(@Autowired @Qualifier("AppMongoClient") MongoClient client) {
        this.client = client;
    }

    public void showDocuments() {
        MongoOperations mongoOperations = new MongoTemplate(client, "pmongo");
        var query = new Query();

        mongoOperations.find(query, Document.class, "test_01")
                .stream().map(Test01::new)
                .forEach(test01 -> logger.info("{}", test01));

        mongoOperations.find(query, Document.class, "test_01")
                .forEach(test01 -> logger.info("{}", test01));

        mongoOperations.find(query, Test012.class, "test_01")
                .forEach(test01 -> logger.info("{}", test01));

        mongoOperations.find(query, Test013.class, "test_01")
                .forEach(test01 -> logger.info("{}", test01));

        query.addCriteria(Criteria.where("name").is("combined 1"));
        mongoOperations.find(query, Test013.class, "test_01")
                .forEach(test01 -> logger.info("{}", test01));

        var q2 = new Document().append("name", "combined 1");
        client
                .getDatabase("pmongo")
                .getCollection("test_01")
                .find(q2)
                .forEach(doc -> logger.info("{}", Test01.fromDocument(doc)));

        mongoOperations.find(query, Document.class, "test_01")
                .stream().map(Test02::new)
                .forEach(test02 -> {
                    logger.info("test02: {}", test02);
                    test02.id().ifPresent(id -> logger.info(" id={}", id));
                    test02.name().ifPresent(name -> logger.info(" name={}", name));
                    test02.getLong("age").ifPresent(age -> logger.info(" age (long)={}", age));
                    test02.getDouble("age").ifPresent(age -> logger.info(" age (double)={}", age));
                    test02.age().ifPresent(age -> logger.info(" age={}", age));

                    test02.withName("ys test 123 changed");
                    test02.name().ifPresent(name -> logger.info(" name={}", name));

                    test02.withAge(321L).withSum(756.098);
                    logger.info("{}", test02);

                    test02.withAge(null).withSum(null).withName(null);
                    logger.info("{}", test02);
                    logger.info("age={}", test02.age());

                });

    }
}

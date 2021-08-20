/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.manipulation;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Manipulation1 {
    private static final Logger logger = LoggerFactory.getLogger(Manipulation1.class);

    public static void main(String[] args) {
        logger.info("==>main");
        var manipulation = new Manipulation1();
        manipulation.test1();
    }

    private void test1() {
        logger.info("==>test1");
        var bsonDocument = new BsonDocument();
        var age = new BsonInt32(123);
        bsonDocument.put("age", age);
        logger.info("{}", bsonDocument.toJson());

        // throws exception
//        var ageDocument = bsonDocument.getDocument("age");
//        logger.info("{}", ageDocument);
        var ageValue32 = bsonDocument.getInt32("age");
        logger.info("age={}", ageValue32);
        logger.info("age={}", ageValue32.getValue());

        // throws exception
//        var ageValue64 = bsonDocument.getInt64("age");
//        logger.info("age={}", ageValue64);
//        logger.info("age={}", ageValue64.getValue());
        var ageValueNumber = bsonDocument.getNumber("age");
        logger.info("age={}", ageValueNumber);
        logger.info("age={}", ageValueNumber.longValue());

        bsonDocument.put("long1", new BsonInt64(1234L));
        logger.info("{}", bsonDocument.toJson());
    }
}

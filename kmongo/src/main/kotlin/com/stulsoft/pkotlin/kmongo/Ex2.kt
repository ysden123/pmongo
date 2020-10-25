/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pkotlin.kmongo

import org.bson.Document
import org.slf4j.LoggerFactory
import org.litote.kmongo.*

/**
 * @author Yuriy Stul
 */
object Ex2 {
    data class test_01(val name:String?, val age:Double?, val sum:Double?)
    private val logger = LoggerFactory.getLogger("")

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("==>start")
        val client = KMongo.createClient("mongodb://root:admin@localhost:27017/?authSource=admin")
        val db = client.getDatabase("pmongo")
        val collection = db.getCollection<test_01>()

        logger.info("1")
        collection.find()
                .sort(Document("name",1))
                .forEach { logger.info("$it") }

        logger.info("2")
        collection.find()
                .filter(Document("age", Document("${MongoOperator.exists}", "true")))
                .sort(Document("name",1))
                .forEach { logger.info("$it") }

        client.close()
    }

}
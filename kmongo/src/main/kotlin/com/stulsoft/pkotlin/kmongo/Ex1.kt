/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pkotlin.kmongo

import org.slf4j.LoggerFactory
import org.litote.kmongo.*

/**
 * @author Yuriy Stul
 */
object Ex1 {
    private val logger = LoggerFactory.getLogger("")

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("==>start")
        val client = KMongo.createClient("mongodb://root:admin@localhost:27017/?authSource=admin")
        val db = client.getDatabase("pmongo")
        val collection = db.getCollection("test_01")

        collection.find()
                .forEach { logger.info("$it") }

        client.close()
    }

}
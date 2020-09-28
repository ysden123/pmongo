/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.kotlin

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.mongodb.javadsl.MongoSource
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document


/**
 * @author Yuriy Stul
 */
object Test01Manager {
    private val client: MongoClient = MongoClients.create(Config.mongoConnectionString())
    private val db: MongoDatabase = client.getDatabase("pmongo")
    private val test01Collection = db.getCollection("test_01")

    fun showAllDocuments(system: ActorSystem) {
        val source: Source<Document, NotUsed> =
                MongoSource.create(test01Collection.find())
        source.runWith(Sink.foreach { d -> println(d) }, system)
    }
}

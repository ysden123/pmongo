/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.data

import akka.{Done, NotUsed}
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.scaladsl.{Sink, Source}
import com.mongodb.reactivestreams.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import com.stulsoft.pmongo.alpakka.Config
import com.typesafe.scalalogging.StrictLogging
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
 * @author Yuriy Stul
 */
object Test01Manager extends StrictLogging {
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Test01]), DEFAULT_CODEC_REGISTRY)
  val client: MongoClient = MongoClients.create(Config.mongoConnectionString())
  val db: MongoDatabase = client.getDatabase("pmongo")

  val test01Collection: MongoCollection[Test01] = db
    .getCollection("test_01", classOf[Test01])
    .withCodecRegistry(codecRegistry)

  def showAllDocuments()(implicit mat: Materializer): Unit = {
    logger.info("==>showAllDocuments")
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(classOf[Test01]))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t))

  }

  def listAllDocuments()(implicit mat: Materializer): Seq[Test01] = {
    logger.info("==>listAllDocuments")
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(classOf[Test01]))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds)
  }

  def showAllDocuments2()(implicit mat: Materializer): Future[Done] ={
    logger.info("==>showAllDocuments2")

    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(classOf[Test01]))

    source.runWith(Sink.foreach(t => logger.info("{}", t)))
  }
}

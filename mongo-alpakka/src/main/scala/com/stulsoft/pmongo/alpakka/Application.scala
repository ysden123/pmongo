/*
 * Copyright (c) 2020. Yuriy Stul 
 */

package com.stulsoft.pmongo.alpakka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.scaladsl.{Sink, Source}
import com.mongodb.reactivestreams.client.MongoClients
import com.stulsoft.pmongo.alpakka.data.Test01
import com.typesafe.scalalogging.StrictLogging
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
 * @author Yuriy Stul
 */
object Application extends App with StrictLogging {
  logger.info("==>main")
  val codecRegistry = fromRegistries(fromProviders(classOf[Test01]), DEFAULT_CODEC_REGISTRY)
  val client = MongoClients.create(Config.mongoConnectionString())
  val db = client.getDatabase("pmongo")
  val test01Collection = db
    .getCollection("test_01", classOf[Test01])
    .withCodecRegistry(codecRegistry)

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = Materializer.createMaterializer(system)

  test1()
  test2()
  test3()
  test4()
  test5()
  test6()

  client.close()
  system.terminate()

  /**
   * Find all, usage of case class
   */
  def test1(): Unit = {
    logger.info("==>test1")
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(classOf[Test01]))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t))
  }

  /**
   * Find with filter by name, usage of case class
   */
  def test2(): Unit = {
    logger.info("==>test2")
    val filter = new Document()
      .append("name", "ys 01")
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(filter, classOf[Test01]))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t))
  }

  /**
   * Find with filter age > 12, usage of case class
   */
  def test3(): Unit = {
    logger.info("==>test3")
    val filter = new Document()
      .append("age", new Document("$gt", 12))
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection.find(filter, classOf[Test01]))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t))
  }

  /**
   * Find with filter age > 12 and sorting by name, usage of case class
   */
  def test4(): Unit = {
    logger.info("==>test4")
    val filter = new Document()
      .append("age", new Document("$gt", 12))
    val source: Source[Test01, NotUsed] =
      MongoSource(test01Collection
        .find(filter, classOf[Test01])
        .sort(new Document("name", -1)))

    val rows: Future[Seq[Test01]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t))
  }

  /**
   * Find all, usage of Document
   */
  def test5(): Unit = {
    logger.info("==>test5")
    val source: Source[Document, NotUsed] =
      MongoSource(test01Collection.find(classOf[Document]))

    val rows: Future[Seq[Document]] = source.runWith(Sink.seq)
    Await.result(rows, 5.seconds).foreach(t => logger.info("{}", t.toJson))
  }

  /**
   * Find all, usage of Document
   */
  def test6(): Unit = {
    logger.info("==>test6")
    val source: Source[Document, NotUsed] =
      MongoSource(test01Collection.find(classOf[Document]))

    Await.result(source.runWith(Sink.foreach(t => logger.info("{}", t.toJson))), 5.seconds)
  }

}

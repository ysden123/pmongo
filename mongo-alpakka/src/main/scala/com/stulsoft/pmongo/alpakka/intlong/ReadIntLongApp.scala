/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.intlong

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.scaladsl.Source
import com.mongodb.reactivestreams.client.MongoClients
import com.stulsoft.pmongo.alpakka.Config
import com.stulsoft.pmongo.alpakka.data.IntLong
import com.typesafe.scalalogging.StrictLogging
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

/**
 * @author Yuriy Stul
 */
object ReadIntLongApp extends App with StrictLogging {
  val codecRegistry = fromRegistries(fromProviders(classOf[IntLong]), DEFAULT_CODEC_REGISTRY)
  val client = MongoClients.create(Config.mongoConnectionString())
  val db = client.getDatabase("testDb")
  val intLongCollection = db
    .getCollection("IntLong", classOf[IntLong])
    .withCodecRegistry(codecRegistry)

  implicit val system: ActorSystem = ActorSystem("ReadIntLongApp")
  implicit val mat: Materializer = Materializer.createMaterializer(system)

  test1()
    .andThen(_ => test2())
    .onComplete(_ => system.terminate())

  def test1(): Future[Unit] = {
    logger.info("==>test1")
    val promise = Promise[Unit]()
    val source: Source[IntLong, NotUsed] =
      MongoSource(intLongCollection.find(classOf[IntLong]))

    source.runForeach(il => println(il)).onComplete(_ => promise.success(()))

    promise.future
  }

  def test2(): Future[Unit] = {
    logger.info("==>test2")
    val promise = Promise[Unit]()
    val source: Source[IntLong, NotUsed] =
      MongoSource(intLongCollection.find(classOf[IntLong]))

    source
      .runForeach(il => il.n.foreach(theN => println(s"n=$theN")))
      .onComplete(_ => promise.success(()))

    promise.future
  }
}

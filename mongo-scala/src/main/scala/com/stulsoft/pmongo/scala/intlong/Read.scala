/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.pmongo.scala.intlong

import com.stulsoft.pmongo.scala.connectionString
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}

/**
 * @author Yuriy Stul
 */
object Read extends App with StrictLogging {
  logger.info("==>main")
  try {
    val dbClient: MongoClient = MongoClient(connectionString)
    val database = dbClient.getDatabase("testDb")
    val collection = database.getCollection("IntLong")

    Await.ready(read1(collection), 1.minutes)
    Await.ready(read2(collection), 1.minutes)

    dbClient.close()
  } catch {
    case ex: Exception =>
      logger.error(ex.getMessage, ex)
  }

  def read1(collection: MongoCollection[Document]): Future[Unit] = {
    val promise = Promise[Unit]()
    collection.find().subscribe(doc => {
      logger.info(s"doc.toJson(): ${doc.toJson()}")
    }, err => {
      logger.error(err.getMessage)
    },
      () => promise.success(())
    )
    promise.future
  }

  def read2(collection: MongoCollection[Document]): Future[Unit] = {
    val promise = Promise[Unit]()
    collection.find().subscribe(
      doc => {
        val intLongObg = new IntLongObg(doc)
        intLongObg.n.map(l => logger.info(s"intLongObg.n: $l"))
      }, err => {
        logger.error(err.getMessage)
      },
      () => promise.success(())
    )
    promise.future
  }

}

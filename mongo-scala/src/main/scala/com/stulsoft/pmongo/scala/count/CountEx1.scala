/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.pmongo.scala.count

import com.stulsoft.pmongo.scala.connectionString
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}

/**
 * @author Yuriy Stul
 */
object CountEx1 extends App with StrictLogging {
  logger.info("==>main")
  try {
    val dbClient: MongoClient = MongoClient(connectionString)
    val database = dbClient.getDatabase("testDb")
    val collection = database.getCollection("IntLong")

    Await.ready(count1(collection), 1.minutes)
  } catch {
    case ex: Exception =>
      logger.error(ex.getMessage, ex)
  }

  def count1(collection: MongoCollection[Document]): Future[Unit] = {
    val promise = Promise[Unit]()

    collection.countDocuments().subscribe(
      count => {
        logger.info(s"Total document count is $count")
      },
      err => {
        logger.error(err.getMessage)
      },
      () => {
        promise.success(())
      }
    )

    promise.future
  }

}

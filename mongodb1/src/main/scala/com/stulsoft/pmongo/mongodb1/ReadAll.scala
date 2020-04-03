/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.mongodb1

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.bson.collection.immutable.Document

import scala.util.{Failure, Success}

/**
 * @author Yuriy Stul
 */
object ReadAll extends App with StrictLogging {
  DB.client() match {
    case Failure(exception) =>
      logger.error(s"Cannot connect to DB: ${exception.getMessage}")
      System.exit(1)
    case Success(client) =>
      logger.info("Received client")
      try {
        val db = client.getDatabase("pmongo")
        val count = new CountDownLatch(1)
        db.getCollection("test-01")
          .find()
          .subscribe(
            (doc: Document) => logger.info(s"doc: $doc"), // onNext
            (err: Throwable) => {
              logger.error(s"Failed getting item: ${err.getMessage}")
              count.countDown()
            }, // onError
            () => { // onComplete
              logger.info("Completed!")
              client.close()
              count.countDown()
            }
          )
        count.await(1, TimeUnit.MINUTES)
      } catch {
        case ex: Exception =>
          logger.error(s"Failure ${ex.getMessage}", ex)
      }
  }

}

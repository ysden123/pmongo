/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.mongodb1

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Observer, Subscription}

import scala.util.{Failure, Success}

/**
 * @author Yuriy Stul
 */
object ReadAll2 extends App with StrictLogging {
  DB.client() match {
    case Failure(exception) =>
      logger.error(s"Cannot connect to DB: ${exception.getMessage}")
      System.exit(1)
    case Success(client) =>
      logger.info("Received client")
      try {
        val db = client.getDatabase("pmongo")
        val count = new CountDownLatch(1)
        db.getCollection("test_01")
          .find()
          .subscribe(new Observer[Document]() {
            var subscription: Option[Subscription] = None

            override def onSubscribe(subscription: Subscription): Unit = {
              this.subscription = Some(subscription)
//              subscription.request(100)
              subscription.request(Long.MaxValue)
            }

            override def onNext(doc: Document): Unit = {
              logger.info(s"${doc.toJson()}")
            }

            override def onError(e: Throwable): Unit = {
              logger.error(s"Error: ${e.getMessage}",e)
            }

            override def onComplete(): Unit = {
              logger.info("Completed!")
              client.close()
              count.countDown()
            }
          })

        count.await(1, TimeUnit.MINUTES)
      } catch {
        case ex: Exception =>
          logger.error(s"Failure ${ex.getMessage}", ex)
      }
  }

}

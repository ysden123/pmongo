/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.mongodb1

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.bson.{BsonInt32, BsonString}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import scala.util.{Failure, Success}

/**
 * @author Yuriy Stul
 */
object FindFirst extends App with StrictLogging {
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
          .find(equal("age",2))
          .first()
          .subscribe(
            (doc: Document) => {
              logger.info(doc.toJson())
              logger.info(s"name: ${doc.get("name").toString}, sum=${doc.get("sum")}")
              val name = doc.get("name") match {
                case Some(s:BsonString) => s.getValue
                case _ => "null"
              }
              val sum = doc.get("sum")match{
                case Some(v:BsonInt32)=> v.intValue()
                case _ => 0
              }
              logger.info(s"name: $name, sum=$sum")
            },
            (err: Throwable) => {
              logger.error(s"Failed getting item: ${err.getMessage}")
              count.countDown()
            },
            () => {
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

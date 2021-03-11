/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

import com.stulsoft.wp.tools.Constants._
import com.stulsoft.wp.tools.db.DocumentUtils._
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}

/**
 * @author Yuriy Stul
 */
object TestDataManipulations extends App with StrictLogging {
  logger.info("==>TestDataManipulations")

  val reportType = ReportType.MerchantData
  val connectionString = Config.mongoConnectionString(Environment.dev)
  var dbClient: MongoClient = _
  try {
    dbClient = MongoClient(connectionString)
    val db = dbClient.getDatabase(DB_NAME)

    Await.ready(extractArray(db), 3.minutes)
    Await.ready(extractObject(db), 3.minutes)
  }
  catch {
    case e: Exception => logger.error(e.getMessage, e)
  }
  finally {
    try {
      dbClient.close()
    } catch {
      case _: Exception =>
    }
  }

  def extractArray(db: MongoDatabase): Future[Unit] = {
    logger.info("==>extractArray")

    val promise = Promise[Unit]()
    val collection = db.getCollection("Accounts")
    collection
      .find(Document(ACC_ID -> 18885, "reportType" -> ReportType.MerchantData.toString))
      .sort(Document("timeStamp" -> -1))
      .first()
      .subscribe(
        account => {
          logger.debug("account received")
          try {
            account.getArray("configHistory") match {
              case Some(configHistory) =>
                configHistory.foreach(configItem => {
                  val config = configItem.getDocument("config")
                  logger.debug("config: {}", config)
                })
              case None =>
                logger.error("Not found 1")
            }

            account.getArray("configHistoryERROR") match {
              case Some(configHistory) =>
                configHistory.foreach(configItem => {
                  val config = configItem.getDocument("config")
                  logger.debug("config: {}", config)
                })
              case None =>
                logger.error("Not found 2")
            }

          } catch {
            case error: Exception => logger.error(error.getMessage)
          }
          promise.success(None)
        },
        error => {
          logger.error(error.getMessage)
          promise.failure(error)
        },
        () => {
          if (!promise.isCompleted) {
            logger.debug("Not found")
            promise.success(None)
          }
        }
      )
    promise.future
  }

  def extractObject(db: MongoDatabase): Future[Unit] = {
    logger.info("==>extractObject")

    val promise = Promise[Unit]()
    val collection = db.getCollection("Accounts")
    collection
      .find(Document(ACC_ID -> 18885, "reportType" -> ReportType.MerchantData.toString))
      .sort(Document("timeStamp" -> -1))
      .first()
      .subscribe(
        account => {
          logger.debug("account received")
          try {
            account.get(CONFIG) match {
              case Some(config) =>
                logger.debug("{}", config)
              case None =>
                logger.debug("config has not found")
            }
          } catch {
            case error: Exception => logger.error(error.getMessage)
          }
          promise.success(None)
        },
        error => {
          logger.error(error.getMessage)
          promise.failure(error)
        },
        () => {
          if (!promise.isCompleted) {
            logger.debug("Not found")
            promise.success(None)
          }
        }
      )
    promise.future
  }
}

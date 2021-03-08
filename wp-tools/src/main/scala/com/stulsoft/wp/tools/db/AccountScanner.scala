/*
 * Copyright (c) 2021. Webpals
 */

package com.stulsoft.wp.tools.db

import com.stulsoft.wp.tools.ReportType.ReportType
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.{Document, MongoDatabase}

import scala.concurrent.{Future, Promise}

/**
 * @author Yuriy Stul
 */
case class AccountScanner(val db: MongoDatabase) extends StrictLogging {
  private val collectionName = "Accounts"

  def scanAccounts(reportType: ReportType, handler: Document => Unit): Future[Unit] = {
    val promise = Promise[Unit]()
    val collection = db.getCollection(collectionName)
    collection.aggregate(List(
      Document("$match" -> Document("reportType" -> reportType.toString)),
      Document("$sort" -> Document("timeStamp" -> -1)),
      Document("$group" -> Document("_id" -> "$accID", "entry" -> Document("$first" -> "$$ROOT"))),
      Document("$replaceRoot" -> Document("newRoot" -> "$entry")),
      Document("$match" -> Document("status" -> Document("$ne" -> "removed"))),
      Document("$sort" -> Document("accID" -> 1))
    )).allowDiskUse(true)
      .subscribe(
        account => handler(account),
        err => {
          logger.error(err.getMessage)
          promise.failure(err)
        },
        () => promise.success(())
      )
    return promise.future
  }
}

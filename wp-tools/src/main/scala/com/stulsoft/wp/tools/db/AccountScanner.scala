/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools.db

import com.stulsoft.wp.tools.ReportType.ReportType
import com.typesafe.scalalogging.StrictLogging
import org.bson.Document
import org.mongodb.scala.MongoDatabase

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
      new Document("$match", new Document("reportType", reportType.toString)),
      new Document("$sort", new Document("timeStamp", -1)),
      new Document("$group", new Document("_id", "$accID").append("entry", new Document("$first", "$$ROOT"))),
      new Document("$replaceRoot", new Document("newRoot", "$entry")),
      new Document("$match", new Document("status", new Document("$ne", "removed"))),
      new Document("$sort", new Document("accID", 1))
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

/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools.validation

import com.stulsoft.wp.tools.Constants.{ACC_ID, CONFIG, CONFIG_HISTORY, ETLMethod}
import com.stulsoft.wp.tools.ReportType.ReportType
import com.stulsoft.wp.tools.db.AccountScanner
import com.stulsoft.wp.tools.db.DocumentUtils._
import com.typesafe.scalalogging.StrictLogging
import org.bson.Document
import org.mongodb.scala.MongoDatabase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * @author Yuriy Stul
 */
class ConfigValidation extends StrictLogging {
  var totalCount = 0
  var errorCount = 0

  def validate(db: MongoDatabase, reportType: ReportType): Future[Unit] = {
    logger.info("Validation for {} has started", reportType)
    val promise = Promise[Unit]()
    val scanner = AccountScanner(db)
    scanner.scanAccounts(reportType, configValidator).onComplete {
      case Success(_) =>
        logger.info("{} accounts have {} errors", totalCount, errorCount)
        promise.success(None)
      case Failure(exception) => promise.failure(exception)
    }
    promise.future
  }

  private def configValidator(account: Document): Unit = {
    try {
      totalCount += 1
      val accID = account.getInteger(ACC_ID)
      val etlMethod = account.getString(ETLMethod)
      if (etlMethod != null) {
        val config = account.getDocument(CONFIG)
        if (config.isEmpty) {
          logger.error("Account {}: root config is missing", accID)
          errorCount += 1
        } else {
          val configByEtlMethod = config.head
          if (configByEtlMethod.entrySet().isEmpty) {
            logger.error("Account {}: root config has empty config for {} ETLMethod", accID, etlMethod)
            errorCount += 1
          }
        }
      }

      account.getArray(CONFIG_HISTORY) match {
        case Some(configHistoryList) =>
          if (configHistoryList.nonEmpty) {
            val configHistoryItem = configHistoryList.last
            val configHistoryEtlMethod = configHistoryItem.getString(ETLMethod)
            if (configHistoryEtlMethod == null) {
              logger.error("Account {}: ETLMethod is missing in config history", accID)
              errorCount += 1
            } else {
              configHistoryItem.getDocument(CONFIG) match {
                case Some(configInConfigHistoryItem) =>
                  val configByEtlMethod = configInConfigHistoryItem.get(configHistoryEtlMethod, classOf[AnyRef])
                  if (configByEtlMethod == null) {
                    logger.error("Account {}: config is missing in config history for {} ETLMethod", accID, configHistoryEtlMethod)
                    errorCount += 1
                  } else {
                    configByEtlMethod match {
                      case s: String =>
                        if (s.isEmpty || "{}".equals(s)) {
                          logger.error("Account {}: config is empty string in config history for {} ETLMethod", accID, configHistoryEtlMethod)
                          errorCount += 1
                        }
                      case c: org.bson.Document =>
                        if (c.isEmpty) {
                          logger.error("Account {}: config is empty object in config history for {} ETLMethod", accID, configHistoryEtlMethod)
                          errorCount += 1
                        }
                      case x =>
                        logger.error("Account {}: Unexpected object {} in config", accID, x.getClass.getName)
                        errorCount += 1
                    }
                  }
                case None =>
                  logger.error("Account {}: configInConfigHistoryItem is missing in config history", accID)
                  errorCount += 1
              }
            }
          }
        case None =>
      }
    }
    catch {
      case exception: Exception =>
        logger.error(exception.getMessage, exception)
        System.exit(1)
    }
  }

  def isConfigEmptyObject(config: Document): Boolean = {
    config.isEmpty
  }

}

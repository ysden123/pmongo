/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools.validation

import com.stulsoft.wp.tools.Constants.DB_NAME
import com.stulsoft.wp.tools.{ChooseEnv, ChooseReportType, Config}
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.MongoClient

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * @author Yuriy Stul
 */
object ConfigValidationRunner extends App with StrictLogging {
  var dbClient: MongoClient = _
  ChooseEnv.env().foreach(env => {
    ChooseReportType.reportType().foreach(reportType => {
      logger.info("Running config validation for {} on {}", reportType, env)
      try {
        dbClient = MongoClient(Config.mongoConnectionString(env))
        val configValidation = new ConfigValidation
        Await.ready(configValidation.validate(dbClient.getDatabase(DB_NAME), reportType), 3.minutes)
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
    })
  })
}

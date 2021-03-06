/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

import com.stulsoft.wp.tools.Environment.Environment

import java.io.File
import com.typesafe.config.ConfigFactory

/**
 * @author Yuriy Stul
 */
object Config {
  private lazy val config = ConfigFactory
    .parseFile(new File(s"${System.getenv("wp-config")}/application.conf"))
    .withFallback(ConfigFactory.load()).getConfig("config")

  def mongoConnectionString(environment: Environment): String = {
    environment match {
      case Environment.dev => config.getConfig("mongoDev").getString("connectionString")
      case Environment.qa => config.getConfig("mongoQa").getString("connectionString")
      case Environment.stg => config.getConfig("mongoStg").getString("connectionString")
      case Environment.prod => config.getConfig("mongoProd").getString("connectionString")
      case _ => s"Unsupported environment $environment"
    }
  }
}

/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka

import java.io.File

import com.typesafe.config.ConfigFactory

/**
 * @author Yuriy Stul
 */
object Config {
  private lazy val config = ConfigFactory
    .parseFile(new File("application.conf"))
    .withFallback(ConfigFactory.load()).getConfig("config")
  def mongoConnectionString():String={
    config.getConfig("mongo").getString("connectionString")
  }
}

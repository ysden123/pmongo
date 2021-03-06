/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

/**
 * @author Yuriy Stul
 */
object Test {

  def main(args: Array[String]): Unit = {
    println("==>main")
    val environment = ChooseEnv.env()
    if (environment.isEmpty)
      return

    val connectionString = Config.mongoConnectionString(environment.get)
    println(connectionString)
  }

}

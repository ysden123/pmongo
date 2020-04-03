/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.mongodb1

import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.util.Try

/**
 * @author Yuriy Stul
 */
object DB {
  def client(): Try[MongoClient] = {
    Try {
      val uri = "mongodb://root:admin@127.0.0.1:27017/?authSource=admin&readPreference=primary&appname=MongoDB1%20Scala&ssl=false"
      System.setProperty("org.mongodb.async.type", "netty")
      MongoClient(uri)
    }
  }
}

/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.pmongo.scala.intlong

import com.stulsoft.pmongo.scala.Helpers.GenericObservable
import com.stulsoft.pmongo.scala.connectionString
import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.{Document, MongoClient}

/**
 * @author Yuriy Stul
 */
object FillCollection extends App with StrictLogging {
  logger.info("==>main")
  try {
    val dbClient: MongoClient = MongoClient(connectionString)
    val database = dbClient.getDatabase("testDb")
    val collection = database.getCollection("IntLong")

    var doc = Document("n" -> 123456, "not-used" -> "nothing 1")
    collection.insertOne(doc).results()

    doc = Document("n" -> 123454546L, "not-used" -> "nothing 2")
    collection.insertOne(doc).results()

    dbClient.close()
  } catch {
    case ex: Exception =>
      logger.error(ex.getMessage, ex)
  }
}

/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools.db

import com.typesafe.scalalogging.StrictLogging
import org.mongodb.scala.bson.collection.immutable.Document

import scala.jdk.CollectionConverters._

/**
 * @author Yuriy Stul
 */
object DocumentUtils {
  type bsonDocument = org.bson.Document

  implicit class ImplicitDocumentUtils(document: Document) extends StrictLogging {

    def getArray(name: String): Option[Seq[bsonDocument]] = {
      try {
        val originList = document.getList(name, classOf[bsonDocument])
        if (originList == null)
          Option.empty
        else
          Option(originList.asScala.toList)
      } catch {
        case exception: Exception =>
          logger.error(exception.getMessage)
          Option.empty
      }
    }

/*
    def getDocument(name: String): Option[bsonDocument] = {
      try {
        Option(document.get(name, classOf[bsonDocument]))
      } catch {
        case exception: Exception =>
          logger.error(exception.getMessage)
          Option.empty
      }
    }
*/


  }

  implicit class ImplicitBsonDocumentUtils(document: bsonDocument) extends StrictLogging {
    def getArray(name: String): Option[Seq[bsonDocument]] = {
      try {
        val originList = document.getList(name, classOf[bsonDocument])
        if (originList == null)
          Option.empty
        else
          Option(originList.asScala.toList)
      } catch {
        case exception: Exception =>
          logger.error(exception.getMessage)
          Option.empty
      }
    }
    def getDocument(name: String): Option[bsonDocument] = {
      try {
        Option(document.get(name, classOf[bsonDocument]))
      } catch {
        case exception: Exception =>
          logger.error(exception.getMessage)
          Option.empty
      }
    }
  }

}

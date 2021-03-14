/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools.db

import com.typesafe.scalalogging.StrictLogging
import org.bson.Document

import scala.jdk.CollectionConverters._

/**
 * @author Yuriy Stul
 */
object DocumentUtils {

  implicit class ImplicitDocumentUtils(document: Document) extends StrictLogging {

    def getArray(name: String): Option[Seq[Document]] = {
      try {
        val originList = document.getList(name, classOf[Document])
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

    def getDocument(name: String): Option[Document] = {
      try {
        Option(document.get(name, classOf[Document]))
      } catch {
        case exception: Exception =>
          logger.error(exception.getMessage)
          Option.empty
      }
    }
  }

}

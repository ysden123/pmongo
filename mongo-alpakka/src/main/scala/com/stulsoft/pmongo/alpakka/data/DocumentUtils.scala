/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.data

import com.typesafe.scalalogging.StrictLogging
import org.bson.Document
import scala.jdk.CollectionConverters._

/**
 * @author Yuriy Stul
 */
object DocumentUtils {

  implicit class ImplicitDocumentUtils(document: Document) extends StrictLogging {
    def getArray(name: String): Option[Iterable[Document]] = {
      val list = document.getList(name, classOf[Document])
      if (list == null)
        Option.empty
      else
        Option(list.asScala)
    }

    def getDocument(name: String): Option[Document] = {
      Option(document.get(name, classOf[Document]))
    }
  }

}

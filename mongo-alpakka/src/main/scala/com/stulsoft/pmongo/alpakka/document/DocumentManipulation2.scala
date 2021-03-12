/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.document

import com.typesafe.scalalogging.StrictLogging
import org.bson.Document
import scala.jdk.CollectionConverters._
import com.stulsoft.pmongo.alpakka.data.DocumentUtils._

/**
 * @author Yuriy Stul
 */
object DocumentManipulation2 extends App with StrictLogging {
  val doc1 = new Document()
    .append("array", List(new Document("aaa", "AAAA"), new Document("bbb", "BBBB")).asJava)
    .append("key1", new Document("SomeKey", "SomeValue"))
    .append("key2", "SomeValue for key 2")

  doc1.getArray("array").foreach(items => {
    items.foreach(document => logger.debug("doc1, array, document: {}", document.toJson))
  })

  doc1.getDocument("key1").foreach(value => logger.debug("doc1, key1: {}", value.toJson))
  logger.debug("doc1, key2: {}", doc1.getString("key2"))
}

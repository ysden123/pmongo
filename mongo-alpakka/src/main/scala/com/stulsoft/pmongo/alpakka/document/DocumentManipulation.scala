/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.document

import java.util

import org.bson.Document
import scala.jdk.CollectionConverters._

/**
 * @author Yuriy Stul
 */
object DocumentManipulation extends App {
  val doc1 = new Document()
    .append("attr1", "value 1")

  println(s"doc1: ${doc1.toJson}")

  val doc2 = new Document("root", "boom")
    .append("array1", util.Arrays.asList("v3.2", "v3.0", "v2.6"))
    .append("array2", util.Arrays.asList(new Document("atr1", "val1"), new Document("atr1", "val2")))

  println(s"doc2: ${doc2.toJson}")

  doc2.getList("array1", classOf[String]).asScala.foreach(item => println(s"array1, item: $item"))

  val doc3 = new Document("root", "boom")
    .append("array1", List("v3.2", "v3.0", "v2.6").asJava)
    .append("array2", List(new Document("atr1", "val1"), new Document("atr1", "val2")).asJava)

  println(s"doc3: ${doc3.toJson}")

  val doc4 = new Document()
    .append("array", List(new Document("aaa", "AAAA"), new Document("bbb", "BBBB")).asJava)
  doc4.getList("array", classOf[Document]).asScala.foreach(item => println(s"array, item: ${item.toJson}"))
}

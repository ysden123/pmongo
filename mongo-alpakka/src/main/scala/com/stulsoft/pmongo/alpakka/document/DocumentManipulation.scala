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
  .append("array1", util.Arrays.asList("v3.2", "v3.0", "v2.6") )
  .append("array2", util.Arrays.asList(new Document("atr1","val1"),new Document("atr1","val2")) )

  println(s"doc2: ${doc2.toJson}")

  val doc3 = new Document("root", "boom")
  .append("array1", List("v3.2", "v3.0", "v2.6").asJava )
  .append("array2", List(new Document("atr1","val1"),new Document("atr1","val2")).asJava )

  println(s"doc3: ${doc3.toJson}")
}

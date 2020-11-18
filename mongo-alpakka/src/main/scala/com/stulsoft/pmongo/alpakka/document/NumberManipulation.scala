package com.stulsoft.pmongo.alpakka.document

import org.bson.Document

/**
 * @author Yuriy Stul
 */
object NumberManipulation extends App {
  val v= 123
  val d=new Document("i",v)
  show(d)

  val v1=1239898098080L
  val d1=new Document("i",v1)
  show(d1)

  val d2=new Document("StartRun", new Document("$gt", v1))
  show(d2)

  val v2:Long=1605639600000L
  val d3=new Document("StartRun", new Document("$gt", v2))
  show(d3)

  def show(doc:Document): Unit ={
    println(s"doc: $doc")
    println(s"doc (toJson): ${doc.toJson}")
  }
}

/*
 * Copyright (c) 2020. StulSoft
 */

package com.stulsoft.pmongo.scala.intlong

import org.mongodb.scala.Document

/**
 * @author Yuriy Stul
 */
class IntLongObg(doc: Document) {
  val n: Option[Long] = {
    val v = doc.get("n")
    if (v.isDefined) Some(v.get.asNumber().longValue()) else None
  }
}

/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

/**
 * @author Yuriy Stul
 */
object Environment extends Enumeration {
  type Environment = Value
  val dev, qa, stg, prod = Value
}

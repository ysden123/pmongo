/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

import com.stulsoft.wp.tools.Environment._

import scala.io.StdIn.readLine

/**
 * @author Yuriy Stul
 */
object ChooseEnv {
  def env(): Option[Environment] = {
    println("Choose environment (enter number):")
    println("0 - exit")
    println("1 - dev")
    println("2 - qa")
    println("3 - stg")
    println("4 - prod")
    readLine() match {
      case "0" => None
      case "1" => Some(dev)
      case "2" => Some(qa)
      case "3" => Some(stg)
      case "4" => Some(prod)
      case _ =>
        println("Wrong answer")
        None
    }
  }
}

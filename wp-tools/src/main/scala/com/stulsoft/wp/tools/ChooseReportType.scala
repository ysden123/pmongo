/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.wp.tools

import com.stulsoft.wp.tools.ReportType._

import scala.io.StdIn.readLine

/**
 * @author Yuriy Stul
 */
object ChooseReportType {
  def reportType(): Option[ReportType] = {
    println("Choose report type (enter number):")
    println("0 - exit")
    println("1 - MerchantData")
    println("2 - Subid")
    println("3 - PlayerData")
    readLine() match {
      case "0" => None
      case "1" => Some(MerchantData)
      case "2" => Some(Subid)
      case "3" => Some(PlayerData)
      case _ =>
        println("Wrong answer")
        None
    }
  }
}

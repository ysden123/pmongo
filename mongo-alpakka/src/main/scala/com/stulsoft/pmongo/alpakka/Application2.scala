/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.stulsoft.pmongo.alpakka.data.Test01Manager
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * @author Yuriy Stul
 */
object Application2 extends App with StrictLogging {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = Materializer.createMaterializer(system)

  Test01Manager.showAllDocuments()
  Test01Manager.listAllDocuments()
    .foreach(t => logger.info("{}", t))

  Await.ready(Test01Manager.showAllDocuments2(), 5.seconds)
  system.terminate()
}

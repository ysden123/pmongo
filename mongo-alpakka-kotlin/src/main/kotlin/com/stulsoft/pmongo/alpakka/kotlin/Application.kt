/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.kotlin

import akka.actor.ActorSystem
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Yuriy Stul
 */
object Application {
    private val logger: Logger = LoggerFactory.getLogger("")

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("==>main")
        val system: ActorSystem = ActorSystem.create()

        Test01Manager.showAllDocuments(system)
    }
}

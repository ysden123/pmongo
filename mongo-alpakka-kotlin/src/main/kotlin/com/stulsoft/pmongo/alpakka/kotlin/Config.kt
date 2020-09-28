/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.alpakka.kotlin

import com.typesafe.config.ConfigFactory
import java.io.File

/**
 * @author Yuriy Stul
 */
object Config {
    private val config = ConfigFactory
            .parseFile(File("application.conf"))
            .withFallback(ConfigFactory.load()).getConfig("config")

    fun mongoConnectionString(): String {
        return config.getConfig("mongo").getString("connectionString")
    }
}

package com.colorextract

import com.typesafe.config.ConfigFactory

object ColorExtractConfig {
  private val config = ConfigFactory.load()
  private val root = config.getConfig("colorextract")
}

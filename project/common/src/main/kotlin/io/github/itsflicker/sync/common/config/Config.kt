package io.github.itsflicker.sync.common.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object Config {

    @Config
    lateinit var conf: Configuration
        private set

}
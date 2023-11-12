package io.github.itsflicker.sync.bukkit.util

import io.github.itsflicker.sync.bukkit.ItsSyncBukkit
import io.github.itsflicker.sync.common.util.string
import io.github.itsflicker.sync.common.util.uuid
import taboolib.common.platform.function.info
import taboolib.common.util.unsafeLazy
import taboolib.module.configuration.ConfigNode
import java.util.*

@ConfigNode(value = "options.debug", bind = "config_bukkit.yml")
var debugMode = false
    private set

val serverUUID: UUID by unsafeLazy {
    ItsSyncBukkit.serverConfig.getString("uuid")!!.uuid()
}

val serverName: String get() = ItsSyncBukkit.serverConfig.getString("name", serverUUID.string())!!

fun debug(message: String) {
    if (debugMode) info(message)
}
package io.github.itsflicker.sync.bukkit.util

import io.github.itsflicker.sync.bukkit.ItsSyncBukkit
import io.github.itsflicker.sync.common.util.string
import io.github.itsflicker.sync.common.util.uuid
import taboolib.common.util.unsafeLazy
import java.util.*

val serverUUID: UUID by unsafeLazy {
    ItsSyncBukkit.serverConfig.getString("uuid")!!.uuid()
}

val serverName: String get() = ItsSyncBukkit.serverConfig.getString("name", serverUUID.string())!!

fun debug(message: String) {

}
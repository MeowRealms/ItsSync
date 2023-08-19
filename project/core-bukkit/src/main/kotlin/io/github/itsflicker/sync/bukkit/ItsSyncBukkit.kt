package io.github.itsflicker.sync.bukkit

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common5.util.parseUUID
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.withComment
import java.util.*

@PlatformSide([Platform.BUKKIT])
object ItsSyncBukkit : Plugin() {

    @Config("config_bukkit.yml")
    lateinit var bukkitConfig: Configuration
        private set

    @Config("server.yml")
    lateinit var serverConfig: Configuration
        private set

    override fun onEnable() {
        if (serverConfig.getString("uuid")?.parseUUID() == null) {
            val uuid = UUID.nameUUIDFromBytes(getDataFolder().absolutePath.toByteArray())
            serverConfig["uuid"] = uuid withComment "Do not change or delete!"
            serverConfig.saveToFile()
        }
    }

}
package io.github.itsflicker.sync.bukkit

import io.github.itsflicker.sync.bukkit.core.PlayerDataHandler
import io.github.itsflicker.sync.bukkit.watchdog.WatchdogThread
import io.github.itsflicker.sync.common.redis.RedisManager
import io.github.itsflicker.sync.common.util.string
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submitAsync
import taboolib.common5.util.parseUUID
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.withComment
import taboolib.platform.util.onlinePlayers
import java.util.*

@PlatformSide([Platform.BUKKIT])
object ItsSyncBukkit : Plugin() {

    @Config("config_bukkit.yml")
    lateinit var bukkitConfig: Configuration
        private set

    @Config("server.yml")
    lateinit var serverConfig: Configuration
        private set

    override fun onLoad() {
        if (serverConfig.getString("uuid")?.parseUUID() == null) {
            val uuid = UUID.nameUUIDFromBytes(getDataFolder().absolutePath.toByteArray()).string()
            serverConfig["uuid"] = uuid withComment "Do not change or delete! Do not copy this file to other servers!"
            serverConfig.saveToFile()
        }
    }

    override fun onEnable() {
        submitAsync(period = 20L) {
            WatchdogThread.tick()
        }
    }

    override fun onDisable() {
        onlinePlayers.forEach {
            val data = PlayerDataHandler.generatePlayerData(it)
            data.toRedis()
        }
        RedisManager.close()
    }

}
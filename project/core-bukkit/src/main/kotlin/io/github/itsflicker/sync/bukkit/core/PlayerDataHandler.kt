package io.github.itsflicker.sync.bukkit.core

import io.github.itsflicker.sync.common.data.PlayerData
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.configuration.ConfigNode
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@PlatformSide([Platform.BUKKIT])
object PlayerDataHandler {

    @ConfigNode("sync", "config_bukkit.yml")
    var syncConf = emptyMap<String, Boolean>()
        private set

    val currentPlayerData = ConcurrentHashMap<UUID, PlayerData>()

    fun generatePlayerData(player: Player): PlayerData {
        val data = PlayerData(player.uniqueId, player.name)
        return data
    }

    fun applyPlayerData(player: Player, data: PlayerData) {

    }

}
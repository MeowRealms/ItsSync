package io.github.itsflicker.sync.bukkit.listener

import io.github.itsflicker.sync.bukkit.core.PlayerDataHandler
import io.github.itsflicker.sync.bukkit.util.serverUUID
import io.github.itsflicker.sync.common.data.PlayerState
import io.github.itsflicker.sync.common.database.DatabaseManager
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

@PlatformSide([Platform.BUKKIT])
object ListenerQuit {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        val state = PlayerState.get(player.uniqueId) ?: return
        if (state !is PlayerState.Locked || state.server != serverUUID) {
            return
        }
        if (PlayerDataHandler.releaseLock.putIfAbsent(player.uniqueId, true) == null) {
            val data = PlayerDataHandler.generatePlayerData(player)
            data.toRedis()
            PlayerState.Free(player.uniqueId).set()
            PlayerDataHandler.releaseLock.remove(player.uniqueId)
            DatabaseManager.store(player.uniqueId, data, "Quit")
        }
    }

}
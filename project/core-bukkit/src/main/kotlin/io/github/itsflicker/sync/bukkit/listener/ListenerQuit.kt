package io.github.itsflicker.sync.bukkit.listener

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
    }

}
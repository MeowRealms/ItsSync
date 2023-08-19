package io.github.itsflicker.sync.bukkit.listener

import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.world.WorldSaveEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.SubscribeEvent

@PlatformSide([Platform.BUKKIT])
object ListenerSave {

    @SubscribeEvent
    fun onWorldSave(e: WorldSaveEvent) {

    }

    @SubscribeEvent
    fun onDeath(e: PlayerDeathEvent) {

    }

    @SubscribeEvent
    fun onChangedWorld(e: PlayerChangedWorldEvent) {

    }

}
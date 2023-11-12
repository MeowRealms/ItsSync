package io.github.itsflicker.sync.bukkit.listener

import io.github.itsflicker.sync.bukkit.ItsSyncBukkit
import io.github.itsflicker.sync.bukkit.core.PlayerDataHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.world.WorldSaveEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

@PlatformSide([Platform.BUKKIT])
object ListenerSave {

    @SubscribeEvent
    fun onWorldSave(e: WorldSaveEvent) {
        if (!ItsSyncBukkit.bukkitConfig.getBoolean("save.worldSave")) return
        submitAsync {
            e.world.players.forEach {
                val data = PlayerDataHandler.generatePlayerData(it)
                data.toRedis()
            }
        }
    }

    @SubscribeEvent
    fun onDeath(e: PlayerDeathEvent) {
        if (!ItsSyncBukkit.bukkitConfig.getBoolean("save.death")) return
    }

    @SubscribeEvent
    fun onChangedWorld(e: PlayerChangedWorldEvent) {
        if (!ItsSyncBukkit.bukkitConfig.getBoolean("save.changeWorld")) return
    }

}
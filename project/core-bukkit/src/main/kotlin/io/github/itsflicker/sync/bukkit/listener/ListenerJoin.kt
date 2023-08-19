package io.github.itsflicker.sync.bukkit.listener

import io.github.itsflicker.sync.bukkit.core.PlayerDataHandler
import io.github.itsflicker.sync.bukkit.util.debug
import io.github.itsflicker.sync.bukkit.util.serverUUID
import io.github.itsflicker.sync.common.data.PlayerData
import io.github.itsflicker.sync.common.data.PlayerState
import io.github.itsflicker.sync.common.redis.RedisMessage
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.util.parseMillis
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.ConfigNodeTransfer

@PlatformSide([Platform.BUKKIT])
object ListenerJoin {

    @ConfigNode("options.max-wait-time", "config_bukkit.yml")
    val maxWaitTime = ConfigNodeTransfer<String, Long> { parseMillis() }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPreLogin(e: AsyncPlayerPreLoginEvent) {
        if (e.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return
        }
        var state = PlayerState.get(e.uniqueId) ?: return // 新玩家
        if (state is PlayerState.Locked) {
            // 数据锁定在本服
            if (state.server == serverUUID) {
                return
            }
            RedisMessage
            val timeout = System.currentTimeMillis() + maxWaitTime.get()
            while (state is PlayerState.Locked && System.currentTimeMillis() < timeout) {
                try {
                    Thread.sleep(20L)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                state = PlayerState.get(e.uniqueId)!!
            }
            if (state is PlayerState.Locked) {
                e.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
                e.kickMessage = "§c等待数据解锁超时! 请联系管理员"
                return
            }
        }
        val data = PlayerData.fromRedis(e.uniqueId) ?: return debug("Player ${e.name} (${e.uniqueId}): State is not null but data is null")
        PlayerDataHandler.currentPlayerData[e.uniqueId] = data
    }

    @SubscribeEvent
    fun onSpawn(e: PlayerSpawnLocationEvent) {
        val data = PlayerDataHandler.currentPlayerData[e.player.uniqueId] ?: return
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun onJoin(e: PlayerJoinEvent) {
        PlayerState.Locked(e.player.uniqueId, serverUUID).set()
    }

}
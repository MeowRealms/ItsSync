package io.github.itsflicker.sync.bukkit.core

import io.netty.channel.Channel
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning
import taboolib.module.nms.ChannelExecutor
import taboolib.module.nms.ConnectionGetter
import taboolib.module.nms.nmsProxy
import taboolib.platform.util.onlinePlayers

@PlatformSide([Platform.BUKKIT])
object ChannelHandler {

    // 不注入pipeline
    init {
        ChannelExecutor.disable()
    }

    fun getPlayerChannel(player: Player, callback: (Channel) -> Unit) {
        val address = player.address?.address ?: return warning("Cannot get player address: ${player.name} (${player.address})")
        val channel = ChannelExecutor.getPlayerChannel(address, isFirst = false)
        callback(channel)
    }

    fun setChannelReadOnly(player: Player) {
        getPlayerChannel(player) {
            it.config().setAutoRead(false)
        }
    }

    fun closeChannel(player: Player) {
        getPlayerChannel(player) {
            it.close()
        }
    }

    @SubscribeEvent(EventPriority.MONITOR)
    private fun onJoin(e: PlayerLoginEvent) {
        if (e.result == PlayerLoginEvent.Result.ALLOWED) {
            ChannelExecutor.getPlayerChannel(e.address, isFirst = true)
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        if (TabooLibCommon.isStopped()) {
            return
        }
        nmsProxy<ConnectionGetter>().release(e.player.address ?: return)
    }

    @Awake(LifeCycle.ACTIVE)
    private fun onEnable() {
        if (TabooLibCommon.isStopped()) {
            return
        }
        onlinePlayers.forEach {
            val address = it.address?.address
            if (address == null) {
                warning("Cannot get player address: ${it.name} (${it.address})")
                return@forEach
            }
            ChannelExecutor.getPlayerChannel(it.address!!.address, isFirst = true)
        }
    }
}
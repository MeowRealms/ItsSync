package io.github.itsflicker.sync.bukkit.core

import io.github.itsflicker.sync.bukkit.api.nms.NMS
import io.github.itsflicker.sync.bukkit.util.debug
import io.github.itsflicker.sync.bukkit.util.serverUUID
import io.github.itsflicker.sync.common.data.PlayerState
import io.github.itsflicker.sync.common.database.DatabaseManager
import io.github.itsflicker.sync.common.util.uuid
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.nms.nmsProxy
import java.util.*

@PlatformSide([Platform.BUKKIT])
object RedisListener {

    fun execute(message: String) {
        val name = message.substringBefore(':')
        val data = message.substringAfter(':')
        when (name) {
            "release" -> {
                release(data.substring(0, 36).uuid(), data.substring(36).uuid())
            }
            "disconnect" -> {
                disconnect(data.substring(0, 36).uuid(), data.substring(36).uuid())
            }
        }
    }

    fun release(server: UUID, uuid: UUID) {
        if (server != serverUUID) {
            return
        }
        if (PlayerDataHandler.releaseLock.putIfAbsent(uuid, true) == null) {
            val player = Bukkit.getPlayer(uuid)
            if (player == null || !player.isOnline) {
                PlayerState.Free(uuid).set()
                PlayerDataHandler.releaseLock.remove(uuid)
                return debug("Player ${uuid}: Release requested from other server but is not online")
            }
            player.closeInventory()
            player.eject()
            nmsProxy<NMS>().freeze(player)
            val data = PlayerDataHandler.generatePlayerData(player)
            data.toRedis()
            PlayerState.Free(player.uniqueId).set()
            PlayerDataHandler.releaseLock.remove(player.uniqueId)
            DatabaseManager.store(player.uniqueId, data, "Quit")
        }
    }

    fun disconnect(server: UUID, uuid: UUID) {
        if (server == serverUUID) {
            return
        }
        val player = Bukkit.getPlayer(uuid) ?: return
        ChannelHandler.closeChannel(player)
    }

}
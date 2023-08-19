package io.github.itsflicker.sync.common.data

import io.github.itsflicker.sync.common.redis.RedisManager
import io.github.itsflicker.sync.common.util.string
import io.github.itsflicker.sync.common.util.uuid
import taboolib.module.configuration.ConfigNode
import java.util.*

sealed interface PlayerState {

    val player: UUID

    fun toContent(): String

    fun set() {
        RedisManager[prefix + player.string()] = toContent()
    }

    data class Free(override val player: UUID) : PlayerState {
        override fun toContent(): String {
            return "free"
        }
    }

    data class Locked(override val player: UUID, val server: UUID) : PlayerState {
        override fun toContent(): String {
            return "locked:${server.string()}"
        }
    }

    companion object {

        @ConfigNode("redis.state-prefix")
        var prefix = "itssync-state:"
            private set

        fun get(player: UUID): PlayerState? {
            val state = RedisManager[prefix + player.string()] ?: return null
            return if (state.startsWith("locked")) {
                Locked(player, state.substringAfter(':').uuid())
            } else if (state == "free") {
                Free(player)
            } else {
                error("Unknown player state $state")
            }
        }

    }
}
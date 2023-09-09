package io.github.itsflicker.sync.common.redis

import io.github.itsflicker.sync.common.util.string
import java.util.*

sealed interface RedisMessage {

    val name: String

    fun toContent(): String

    fun send() {
        RedisManager.publish("${name}:${toContent()}")
    }

    data class Release(val server: UUID, val player: UUID) : RedisMessage {
        override val name = "release"
        override fun toContent(): String {
            return server.string() + player.string()
        }
    }

    data class Disconnect(val server: UUID, val player: UUID) : RedisMessage {
        override val name = "disconnect"
        override fun toContent(): String {
            return server.string() + player.string()
        }
    }

}
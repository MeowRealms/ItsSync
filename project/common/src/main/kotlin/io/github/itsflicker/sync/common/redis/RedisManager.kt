package io.github.itsflicker.sync.common.redis

import io.github.itsflicker.sync.common.config.Config
import io.github.itsflicker.sync.taboolib.expansion.AlkaidRedis
import io.github.itsflicker.sync.taboolib.expansion.SingleRedisConnection
import io.github.itsflicker.sync.taboolib.expansion.fromConfig
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.configuration.ConfigNode

object RedisManager {

    @ConfigNode("redis.message-channel")
    var channel = "itssync-message"
        private set

    private var connection: SingleRedisConnection? = null

    @Awake(LifeCycle.LOAD)
    fun init() {
        connection = AlkaidRedis.createDefault {
            it.fromConfig(Config.conf.getConfigurationSection("redis")!!)
        }
    }

    fun close() {
        connection?.close()
    }

    operator fun get(key: String): String? {
        return connection?.get(key)
    }

    operator fun get(key: ByteArray): ByteArray? {
        return connection?.exec { it.get(key) }
    }

    operator fun set(key: String, value: String?) {
        connection?.set(key, value)
    }

    operator fun set(key: ByteArray, value: ByteArray?) {
        connection?.exec { if (value == null) it.del(key) else it.set(key, value) }
    }

    fun publish(message: String) {
        connection?.publish(channel, message)
    }

    fun subscribe() {

    }

}
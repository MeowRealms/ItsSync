package io.github.itsflicker.sync.common.database

import io.github.itsflicker.sync.common.data.PlayerData
import io.github.itsflicker.sync.common.data.PlayerDataStored
import taboolib.module.configuration.ConfigNode
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object DatabaseManager {

    @ConfigNode("database.enabled")
    var enabled = true
        private set

    private val database by lazy {
        Database()
    }

    private val pool = Executors.newFixedThreadPool(4)

    fun getAll(player: UUID): CompletableFuture<List<PlayerDataStored>> {
        val future = CompletableFuture<List<PlayerDataStored>>()
        pool.submit {
            future.complete(database.getAll(player))
        }
        return future
    }

    fun store(player: UUID, data: PlayerData, reason: String) {
        pool.submit {
            val stored = PlayerDataStored(player, reason = reason, data = data.toByteArray())
        }
    }

    fun switchLocked() {

    }

}
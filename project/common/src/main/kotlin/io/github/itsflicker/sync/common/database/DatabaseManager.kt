package io.github.itsflicker.sync.common.database

import io.github.itsflicker.sync.common.data.PlayerDataStored
import taboolib.expansion.dbSection
import taboolib.expansion.persistentContainer
import taboolib.module.configuration.ConfigNode
import java.util.concurrent.Executors

object DatabaseManager {

    @ConfigNode("database.enabled")
    var enabled = true
        private set

    @ConfigNode("database.table")
    var table = "itssync_data"
        private set

    private val container by lazy {
        persistentContainer(dbSection("config.yml", "database")) { new<PlayerDataStored>(table) }
    }

    private val pool = Executors.newFixedThreadPool(4)

    fun close() {
        if (enabled) {
            container.close()
        }
    }

}
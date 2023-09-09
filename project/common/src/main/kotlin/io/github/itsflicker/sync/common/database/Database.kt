package io.github.itsflicker.sync.common.database

import io.github.itsflicker.sync.common.config.Config
import io.github.itsflicker.sync.common.data.PlayerDataStored
import io.github.itsflicker.sync.common.util.string
import taboolib.common.io.unzip
import taboolib.module.database.*
import java.util.*

class Database {

    abstract class Type {

        abstract fun host(): Host<*>

        abstract fun tableVar(): Table<*, *>
    }

    class TypeSQL : Type() {

        val host = Config.conf.getHost("database")

        val tableVar = Table(Config.conf.getString("database.table")!!, host) {
            add { id() }
            add("player") {
                type(ColumnTypeSQL.VARCHAR, 36) {
                    options(ColumnOptionSQL.KEY, ColumnOptionSQL.NOTNULL)
                }
            }
            add("version") {
                type(ColumnTypeSQL.BIGINT) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("locked") {
                type(ColumnTypeSQL.BOOLEAN) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("reason") {
                type(ColumnTypeSQL.VARCHAR, 36) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("data") {
                type(ColumnTypeSQL.LONGBLOB) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
        }

        override fun host(): Host<*> {
            return host
        }

        override fun tableVar(): Table<*, *> {
            return tableVar
        }
    }

    val type = TypeSQL()

    val dataSource = type.host().createDataSource()

    init {
        type.tableVar().workspace(dataSource) { createTable() }.run()
    }

    fun getAll(player: UUID): List<PlayerDataStored> {
        return type.tableVar().workspace(dataSource) {
            select {
                rows("version", "locked", "reason", "data")
                where { "player" eq player.string() }
            }
        }.map {
            PlayerDataStored(
                player,
                Date(getLong("version")),
                getBoolean("locked"),
                getString("reason"),
                getBytes("data").unzip()
            )
        }
    }

}
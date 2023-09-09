package io.github.itsflicker.sync.common.data

import taboolib.common.util.unsafeLazy
import java.util.*

class PlayerDataStored(
    val player: UUID,
    val version: Date = Date(),
    var locked: Boolean = false,
    val reason: String,
    val data: ByteArray
) {

    val playerData by unsafeLazy {
        PlayerData.fromByteArray(data)
    }
}
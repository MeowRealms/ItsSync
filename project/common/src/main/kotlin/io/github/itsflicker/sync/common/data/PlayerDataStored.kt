package io.github.itsflicker.sync.common.data

import taboolib.expansion.Id
import taboolib.expansion.Key
import java.util.*

class PlayerDataStored(
    @Id
    val uuid: UUID,
    @Key
    val version: Date,
    val data: ByteArray
)
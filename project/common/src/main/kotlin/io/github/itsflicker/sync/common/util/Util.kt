package io.github.itsflicker.sync.common.util

import com.eatthepath.uuid.FastUUID
import java.util.*

fun UUID.string() = FastUUID.toString(this)

fun String.uuid() = FastUUID.parseUUID(this)
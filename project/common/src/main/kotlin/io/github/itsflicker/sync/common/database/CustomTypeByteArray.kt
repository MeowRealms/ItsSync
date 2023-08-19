package io.github.itsflicker.sync.common.database

import taboolib.common.io.unzip
import taboolib.common.io.zip
import taboolib.expansion.CustomType
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.ColumnTypeSQLite
import java.io.ByteArrayInputStream

object CustomTypeByteArray : CustomType {

    override val type: Class<*> = ByteArray::class.java

    override val typeSQL: ColumnTypeSQL = ColumnTypeSQL.LONGBLOB

    override val typeSQLite: ColumnTypeSQLite = ColumnTypeSQLite.NULL

    override val length: Int = 0

    override fun serialize(value: Any): Any {
        return ByteArrayInputStream((value as ByteArray).zip())
    }

    override fun deserialize(value: Any): Any {
        return (value as ByteArray).unzip()
    }

}
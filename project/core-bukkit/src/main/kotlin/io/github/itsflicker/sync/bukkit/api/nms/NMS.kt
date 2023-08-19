package io.github.itsflicker.sync.bukkit.api.nms

import org.bukkit.entity.Player

abstract class NMS {

    abstract fun getActiveEffects(player: Player): ByteArray

    abstract fun getAttributes(player: Player): ByteArray

    abstract fun getEnderChest(player: Player): ByteArray

    abstract fun getInventory(player: Player): ByteArray

    abstract fun getRecipeBook(player: Player): ByteArray

    abstract fun setActiveEffects(player: Player, data: ByteArray)

    abstract fun setAttributes(player: Player, data: ByteArray)

    abstract fun setEnderChest(player: Player, data: ByteArray)

    abstract fun setInventory(player: Player, data: ByteArray)

    abstract fun setRecipeBook(player: Player, data: ByteArray)

    abstract fun freeze(player: Player)

}
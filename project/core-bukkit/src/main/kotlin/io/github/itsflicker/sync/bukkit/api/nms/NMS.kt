package io.github.itsflicker.sync.bukkit.api.nms

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory

abstract class NMS {

    abstract fun getSelectedItemSlot(player: Player): Int

    abstract fun setSelectedItemSlot(player: Player, slot: Int)

    abstract fun serializeActiveEffects(player: Player): ByteArray

    abstract fun serializeAttributes(player: Player): ByteArray

    abstract fun serializeEnderChest(player: Player): ByteArray

    abstract fun serializeInventory(player: Player): ByteArray

    abstract fun serializeRecipeBook(player: Player): ByteArray

    abstract fun deserializeActiveEffects(data: ByteArray, player: Player)

    abstract fun deserializeAttributes(data: ByteArray, player: Player)

    abstract fun deserializeEnderChest(data: ByteArray, player: Player?): Inventory

    abstract fun deserializeInventory(data: ByteArray, player: Player?): PlayerInventory

    abstract fun deserializeRecipeBook(data: ByteArray, player: Player)

    abstract fun freeze(player: Player)

}
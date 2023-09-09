package io.github.itsflicker.sync.bukkit.api.nms

import io.github.itsflicker.sync.bukkit.core.ChannelHandler
import net.minecraft.nbt.NBTReadLimiter
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.PlayerInventory
import net.minecraft.world.inventory.InventoryEnderChest
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryPlayer
import org.bukkit.entity.Player
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

@Suppress("unused")
class NMSImpl : NMS() {

    private val players = (Bukkit.getServer() as CraftServer).handle
        .getProperty<ArrayList<EntityPlayer>>("players")

    private val playersByUUID = (Bukkit.getServer() as CraftServer).handle
        .getProperty<HashMap<UUID, EntityPlayer>>("playersByUUID")

    override fun getSelectedItemSlot(player: Player): Int {
        val entityPlayer = getNMSPlayer(player)
        return entityPlayer.inventory.selected
    }

    override fun setSelectedItemSlot(player: Player, slot: Int) {
        val entityPlayer = getNMSPlayer(player)
        entityPlayer.inventory.selected = slot
    }

    override fun serializeActiveEffects(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        val list = NBTTagList()
        if (entityPlayer.activeEffects.isNotEmpty()) {
            entityPlayer.activeEffects.values.forEach {
                list.add(it.save(NBTTagCompound()))
            }
        }
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun serializeAttributes(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.attributes.save()
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun serializeEnderChest(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.enderChestInventory.createTag()
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun serializeInventory(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.inventory.save(NBTTagList())
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun serializeRecipeBook(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val compound = entityPlayer.recipeBook.toNbt()
                compound.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun deserializeActiveEffects(data: ByteArray, player: Player) {
        val entityPlayer = getNMSPlayer(player)
        data.dataInputStream {
            val list = NBTTagList.TYPE.load(it, 0, NBTReadLimiter.UNLIMITED)
            (0 until list.size).forEach { i ->
                val effect = MobEffect.load(list.getCompound(i))
                if (effect != null) {
                    entityPlayer.activeEffects[effect.effect] = effect
                }
            }
        }
    }

    override fun deserializeAttributes(data: ByteArray, player: Player) {
        val entityPlayer = getNMSPlayer(player)
        data.dataInputStream {
            val list = NBTTagList.TYPE.load(it, 0, NBTReadLimiter.UNLIMITED)
            entityPlayer.attributes.load(list)
        }
    }

    override fun deserializeEnderChest(data: ByteArray, player: Player?): org.bukkit.inventory.Inventory {
        val inv = player?.let { getNMSPlayer(it).enderChestInventory } ?: InventoryEnderChest()
        data.dataInputStream {
            val list = NBTTagList.TYPE.load(it, 0, NBTReadLimiter.UNLIMITED)
            inv.fromTag(list)
        }
        return CraftInventory(inv)
    }

    override fun deserializeInventory(data: ByteArray, player: Player?): org.bukkit.inventory.PlayerInventory {
        val inv = player?.let { getNMSPlayer(it).inventory } ?: PlayerInventory::class.java.unsafeInstance() as PlayerInventory
        data.dataInputStream {
            val list = NBTTagList.TYPE.load(it, 0, NBTReadLimiter.UNLIMITED)
            inv.load(list)
        }
        return CraftInventoryPlayer(inv)
    }

    override fun deserializeRecipeBook(data: ByteArray, player: Player) {
        val entityPlayer = getNMSPlayer(player)
        data.dataInputStream {
            val compound = NBTTagCompound.TYPE.load(it, 0, NBTReadLimiter.UNLIMITED)
            entityPlayer.recipeBook.fromNbt(compound, entityPlayer.server.recipeManager)
        }
    }

    override fun freeze(player: Player) {
        val entityPlayer = getNMSPlayer(player)
        ChannelHandler.setChannelReadOnly(player)
        entityPlayer.serverLevel().removePlayerImmediately(entityPlayer, Entity.RemovalReason.UNLOADED_WITH_PLAYER)
        players?.remove(entityPlayer)
        playersByUUID?.remove(entityPlayer.uuid)
    }

    private fun getNMSPlayer(player: Player): EntityPlayer {
        return (player as CraftPlayer).handle
    }

    private fun ByteArray.dataInputStream(block: (DataInputStream) -> Unit) {
        ByteArrayInputStream(this).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                block(dataInputStream)
            }
        }
    }
}
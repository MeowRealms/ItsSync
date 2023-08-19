package io.github.itsflicker.sync.bukkit.api.nms

import io.github.itsflicker.sync.bukkit.core.ChannelHandler
import net.minecraft.nbt.NBTReadLimiter
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import taboolib.library.reflex.Reflex.Companion.getProperty
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class NMSImpl : NMS() {

    private val players = (Bukkit.getServer() as CraftServer).handle
        .getProperty<ArrayList<EntityPlayer>>("players")

    private val playersByUUID = (Bukkit.getServer() as CraftServer).handle
        .getProperty<HashMap<UUID, EntityPlayer>>("playersByUUID")

    override fun getActiveEffects(player: Player): ByteArray {
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

    override fun getAttributes(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.attributes.save()
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun getEnderChest(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.enderChestInventory.createTag()
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun getInventory(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val list = entityPlayer.inventory.save(NBTTagList())
                list.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun getRecipeBook(player: Player): ByteArray {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            DataOutputStream(byteArrayOutputStream).use { dataOutputStream ->
                val compound = entityPlayer.recipeBook.toNbt()
                compound.write(dataOutputStream)
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    override fun setActiveEffects(player: Player, data: ByteArray) {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                val list = NBTTagList.TYPE.load(dataInputStream, 0, NBTReadLimiter.UNLIMITED)
                (0 until list.size).forEach {
                    val effect = MobEffect.load(list.getCompound(it))
                    if (effect != null) {
                        entityPlayer.activeEffects[effect.effect] = effect
                    }
                }
            }
        }
    }

    override fun setAttributes(player: Player, data: ByteArray) {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                val list = NBTTagList.TYPE.load(dataInputStream, 0, NBTReadLimiter.UNLIMITED)
                entityPlayer.attributes.load(list)
            }
        }
    }

    override fun setEnderChest(player: Player, data: ByteArray) {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                val list = NBTTagList.TYPE.load(dataInputStream, 0, NBTReadLimiter.UNLIMITED)
                entityPlayer.enderChestInventory.fromTag(list)
            }
        }
    }

    override fun setInventory(player: Player, data: ByteArray) {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                val list = NBTTagList.TYPE.load(dataInputStream, 0, NBTReadLimiter.UNLIMITED)
                entityPlayer.inventory.load(list)
            }
        }
    }

    override fun setRecipeBook(player: Player, data: ByteArray) {
        val entityPlayer = getNMSPlayer(player)
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            DataInputStream(byteArrayInputStream).use { dataInputStream ->
                val compound = NBTTagCompound.TYPE.load(dataInputStream, 0, NBTReadLimiter.UNLIMITED)
                entityPlayer.recipeBook.fromNbt(compound, entityPlayer.server.recipeManager)
            }
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
}
package io.github.itsflicker.sync.bukkit.core

import io.github.itsflicker.sync.bukkit.ItsSyncBukkit
import io.github.itsflicker.sync.bukkit.api.nms.NMS
import io.github.itsflicker.sync.common.data.PlayerData
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.resettableLazy
import taboolib.module.configuration.util.getMap
import taboolib.module.nms.nmsProxy
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@PlatformSide([Platform.BUKKIT])
object PlayerDataHandler {

    val enables by resettableLazy {
        ItsSyncBukkit.bukkitConfig.getMap<String, Boolean>("sync")
    }

    val nms = nmsProxy<NMS>()

    val releaseLock = ConcurrentHashMap<UUID, Boolean>()

    fun generatePlayerData(player: Player): PlayerData {
        val data = PlayerData(player.uniqueId, player.name)
        val proxyPlayer = adaptPlayer(player)
        if (enables.getOrDefault("advancement", true)) data.advancements = ExtraDataHandler.serializeAdvancements(player)
        if (enables.getOrDefault("statistics", true)) data.statistics = ExtraDataHandler.serializeStatistics(player)
        if (enables.getOrDefault("inventory", true)) {
            data.inventory = nms.serializeInventory(player)
            data.selectedItemSlot = nms.getSelectedItemSlot(player)
        }
        if (enables.getOrDefault("enderChest", true)) data.enderchest = nms.serializeEnderChest(player)
        if (enables.getOrDefault("effect", false)) data.activeEffects = nms.serializeActiveEffects(player)
        if (enables.getOrDefault("attribute", false)) data.attributes = nms.serializeAttributes(player)
        if (enables.getOrDefault("recipeBook", false)) data.recipeBook = nms.serializeRecipeBook(player)
        if (enables.getOrDefault("gamemode", false)) data.gamemode = proxyPlayer.gameMode
        if (enables.getOrDefault("health", true)) data.health = player.health
        if (enables.getOrDefault("maxHealth", false)) data.maxHealth = proxyPlayer.maxHealth
        if (enables.getOrDefault("food", true)) {
            data.saturation = player.saturation
            data.exhaustion = player.exhaustion
            data.foodLevel = player.foodLevel
        }
        if (enables.getOrDefault("exp", true)) {
            data.level = player.level
            data.exp = player.exp
            data.totalExp = player.totalExperience
        }
        if (enables.getOrDefault("fly", false)) {
            data.allowFlight = player.allowFlight
            data.isFlying = player.isFlying
            data.flySpeed = player.flySpeed
        }
        if (enables.getOrDefault("walkSpeed", false)) data.walkSpeed = player.walkSpeed
        if (enables.getOrDefault("air", false)) data.remainingAir = proxyPlayer.remainingAir
        if (enables.getOrDefault("location", false)) {
            val location = player.location
            data.world = location.world?.name
            data.x = location.x
            data.y = location.y
            data.z = location.z
            if (enables.getOrDefault("rotation", true)) {
                data.yaw = location.yaw
                data.pitch = location.pitch
            }
        }
        return data
    }

    fun applyPlayerData(player: Player, data: PlayerData) {
        val proxyPlayer = adaptPlayer(player)
        if (enables.getOrDefault("advancement", true)) data.advancements?.let { ExtraDataHandler.deserializeAdvancements(player, it) }
        if (enables.getOrDefault("statistics", true)) data.statistics?.let { ExtraDataHandler.deserializeStatistics(player, it) }
        if (enables.getOrDefault("inventory", true)) {
            data.inventory?.let { nms.deserializeInventory(it, player) }
            data.selectedItemSlot?.let { nms.setSelectedItemSlot(player, it) }
        }
        if (enables.getOrDefault("enderChest", true)) data.enderchest?.let { nms.deserializeEnderChest(it, player) }
        if (enables.getOrDefault("effect", false)) data.activeEffects?.let { nms.deserializeActiveEffects(it, player) }
        if (enables.getOrDefault("attribute", false)) data.attributes?.let { nms.deserializeAttributes(it, player) }
        if (enables.getOrDefault("recipeBook", false)) data.recipeBook?.let { nms.deserializeRecipeBook(it, player) }
        if (enables.getOrDefault("gamemode", false)) data.gamemode?.let { proxyPlayer.gameMode = it }
        if (enables.getOrDefault("health", true)) data.health?.let { player.health = it }
        if (enables.getOrDefault("maxHealth", false)) data.maxHealth?.let { proxyPlayer.maxHealth = it }
        if (enables.getOrDefault("food", true)) {
            data.saturation?.let { player.saturation = it }
            data.exhaustion?.let { player.exhaustion = it }
            data.foodLevel?.let { player.foodLevel = it }
        }
        if (enables.getOrDefault("exp", true)) {
            data.level?.let { player.level = it }
            data.exp?.let { player.exp = it }
            data.totalExp?.let { player.totalExperience = it }
        }
        if (enables.getOrDefault("fly", false)) {
            data.allowFlight?.let { player.allowFlight = it }
            data.isFlying?.let { player.isFlying = it }
            data.flySpeed?.let { player.flySpeed = it }
        }
        if (enables.getOrDefault("walkSpeed", false)) data.walkSpeed?.let { player.walkSpeed = it }
        if (enables.getOrDefault("air", false)) data.remainingAir?.let { player.remainingAir = it }
    }

}
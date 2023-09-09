package io.github.itsflicker.sync.bukkit.core

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object ExtraDataHandler {

    fun serializeAdvancements(player: Player): JSONObject {
        val obj = JSONObject.of()
        Bukkit.advancementIterator().forEachRemaining { adv ->
            val progress = player.getAdvancementProgress(adv)
            if (progress.awardedCriteria.isNotEmpty()) {
                obj[adv.key.toString()] = JSONArray.of().apply {
                    progress.awardedCriteria.forEach { criteria ->
                        add(criteria)
                    }
                }
            }
        }
        return obj
    }

    fun deserializeAdvancements(player: Player, obj: JSONObject) {
        val level = player.level
        val exp = player.exp
        val totalExp = player.totalExperience
        for ((key, value) in obj) {
            val adv = Bukkit.getAdvancement(NamespacedKey.fromString(key) ?: continue) ?: continue
            val progress = player.getAdvancementProgress(adv)
            val completed = (value as JSONArray)
            progress.awardedCriteria.forEach {
                if (it !in completed) {
                    progress.revokeCriteria(it)
                }
            }
            progress.remainingCriteria.forEach {
                if (it in completed) {
                    progress.awardCriteria(it)
                }
            }
        }
        player.level = level
        player.exp = exp
        player.totalExperience = totalExp
    }

    fun serializeStatistics(player: Player): JSONObject {
        val obj = JSONObject.of()
        Statistic.entries.forEach { stat ->
            when (stat.type) {
                Statistic.Type.ITEM -> {
                    val value = JSONObject.of()
                    Material.entries.filter { it.isItem }.forEach { mat ->
                        player.getStatistic(stat, mat).takeIf { it != 0 }?.let {
                            value[mat.name] = it
                        }
                    }
                    if (value.isNotEmpty()) {
                        obj[stat.name] = value
                    }
                }
                Statistic.Type.BLOCK -> {
                    val value = JSONObject.of()
                    Material.entries.filter { it.isBlock }.forEach { mat ->
                        player.getStatistic(stat, mat).takeIf { it != 0 }?.let {
                            value[mat.name] = it
                        }
                    }
                    if (value.isNotEmpty()) {
                        obj[stat.name] = value
                    }
                }
                Statistic.Type.ENTITY -> {
                    val value = JSONObject.of()
                    EntityType.entries.filter { it.isAlive }.forEach { entity ->
                        player.getStatistic(stat, entity).takeIf { it != 0 }?.let {
                            value[entity.name] = it
                        }
                    }
                    if (value.isNotEmpty()) {
                        obj[stat.name] = value
                    }
                }
                else -> {
                    player.getStatistic(stat).takeIf { it != 0 }?.let {
                        obj[stat.name] = it
                    }
                }
            }
        }
        return obj
    }

    fun deserializeStatistics(player: Player, data: JSONObject) {

    }

}
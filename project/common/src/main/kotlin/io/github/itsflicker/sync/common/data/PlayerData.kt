package io.github.itsflicker.sync.common.data

import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONByteArray
import io.github.itsflicker.sync.common.redis.RedisManager
import taboolib.module.configuration.ConfigNode
import java.nio.ByteBuffer
import java.util.*

data class PlayerData(
    var uuid: UUID,
    var name: String,
    var gamemode: String? = null,
    var health: Double? = null,
    var maxHealth: Double? = null,
    var saturation: Float? = null,
    var exhaustion: Float? = null,
    var foodLevel: Int? = null,
    var level: Int? = null,
    var exp: Float? = null,
    var totalExp: Int? = null,
    var allowFlight: Boolean? = null,
    var isFlying: Boolean? = null,
    var flySpeed: Float? = null,
    var walkSpeed: Float? = null,
    var remainingAir: Int? = null,
    var world: String? = null,
    var x: Double? = null,
    var y: Double? = null,
    var z: Double? = null,
    var yaw: Float? = null,
    var pitch: Float? = null,
    var selectedItemSlot: Int? = null,
    var activeEffects: ByteArray? = null,
    var attributes: ByteArray? = null,
    var enderchest: ByteArray? = null,
    var inventory: ByteArray? = null,
    var recipeBook: ByteArray? = null,
    var advancements: JSONObject? = null,
    var statistics: JSONObject? = null,
    var extraProperties: JSONObject? = null
) {

    constructor() : this(UUID(0, 0), "")

    fun toByteArray(): ByteArray {
        return toJSONByteArray()
    }

    fun toRedis() {
        RedisManager[getRedisKey(uuid)] = toByteArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PlayerData
        if (uuid != other.uuid) return false
        if (name != other.name) return false
        if (gamemode != other.gamemode) return false
        if (health != other.health) return false
        if (maxHealth != other.maxHealth) return false
        if (saturation != other.saturation) return false
        if (exhaustion != other.exhaustion) return false
        if (foodLevel != other.foodLevel) return false
        if (level != other.level) return false
        if (exp != other.exp) return false
        if (totalExp != other.totalExp) return false
        if (allowFlight != other.allowFlight) return false
        if (isFlying != other.isFlying) return false
        if (flySpeed != other.flySpeed) return false
        if (walkSpeed != other.walkSpeed) return false
        if (remainingAir != other.remainingAir) return false
        if (world != other.world) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false
        if (selectedItemSlot != other.selectedItemSlot) return false
        if (activeEffects != null) {
            if (other.activeEffects == null) return false
            if (!activeEffects.contentEquals(other.activeEffects)) return false
        } else if (other.activeEffects != null) return false
        if (attributes != null) {
            if (other.attributes == null) return false
            if (!attributes.contentEquals(other.attributes)) return false
        } else if (other.attributes != null) return false
        if (enderchest != null) {
            if (other.enderchest == null) return false
            if (!enderchest.contentEquals(other.enderchest)) return false
        } else if (other.enderchest != null) return false
        if (inventory != null) {
            if (other.inventory == null) return false
            if (!inventory.contentEquals(other.inventory)) return false
        } else if (other.inventory != null) return false
        if (recipeBook != null) {
            if (other.recipeBook == null) return false
            if (!recipeBook.contentEquals(other.recipeBook)) return false
        } else if (other.recipeBook != null) return false
        if (advancements != other.advancements) return false
        if (statistics != other.statistics) return false
        if (extraProperties != other.extraProperties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (gamemode?.hashCode() ?: 0)
        result = 31 * result + (health?.hashCode() ?: 0)
        result = 31 * result + (maxHealth?.hashCode() ?: 0)
        result = 31 * result + (saturation?.hashCode() ?: 0)
        result = 31 * result + (exhaustion?.hashCode() ?: 0)
        result = 31 * result + (foodLevel ?: 0)
        result = 31 * result + (level ?: 0)
        result = 31 * result + (exp?.hashCode() ?: 0)
        result = 31 * result + (totalExp ?: 0)
        result = 31 * result + (allowFlight?.hashCode() ?: 0)
        result = 31 * result + (isFlying?.hashCode() ?: 0)
        result = 31 * result + (flySpeed?.hashCode() ?: 0)
        result = 31 * result + (walkSpeed?.hashCode() ?: 0)
        result = 31 * result + (remainingAir ?: 0)
        result = 31 * result + (world?.hashCode() ?: 0)
        result = 31 * result + (x?.hashCode() ?: 0)
        result = 31 * result + (y?.hashCode() ?: 0)
        result = 31 * result + (z?.hashCode() ?: 0)
        result = 31 * result + (yaw?.hashCode() ?: 0)
        result = 31 * result + (pitch?.hashCode() ?: 0)
        result = 31 * result + (selectedItemSlot ?: 0)
        result = 31 * result + (activeEffects?.contentHashCode() ?: 0)
        result = 31 * result + (attributes?.contentHashCode() ?: 0)
        result = 31 * result + (enderchest?.contentHashCode() ?: 0)
        result = 31 * result + (inventory?.contentHashCode() ?: 0)
        result = 31 * result + (recipeBook?.contentHashCode() ?: 0)
        result = 31 * result + (advancements?.hashCode() ?: 0)
        result = 31 * result + (statistics?.hashCode() ?: 0)
        result = 31 * result + (extraProperties?.hashCode() ?: 0)
        return result
    }

    companion object {

        @ConfigNode("redis.data-prefix")
        var prefix = "itssync-data:"
            private set

        fun fromByteArray(data: ByteArray): PlayerData {
            return data.to<PlayerData>()
        }

        fun getRedisKey(uuid: UUID): ByteArray {
            val prefix = prefix.toByteArray()
            val buffer = ByteBuffer.allocate(prefix.size + 16)
            buffer.put(prefix)
            buffer.putLong(uuid.mostSignificantBits)
            buffer.putLong(uuid.leastSignificantBits)
            return buffer.array()
        }

        fun fromRedis(uuid: UUID): PlayerData? {
            val data = RedisManager[getRedisKey(uuid)] ?: return null
            return fromByteArray(data)
        }
    }
}
package io.github.itsflicker.sync.bukkit.watchdog

import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.Awake
import taboolib.common.platform.function.severe
import taboolib.common5.util.parseMillis
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.ConfigNodeTransfer

object WatchdogThread : Thread("ItsSync Watchdog Thread") {

    @ConfigNode("options.crash-timeout", "config_bukkit.yml")
    val timeoutMillis = ConfigNodeTransfer<String, Long> { parseMillis() }

    @Volatile
    private var lastTick = monotonicMillis()

    @JvmStatic
    private fun monotonicMillis() = System.nanoTime() / 1000000L

    @JvmStatic
    fun tick() {
        lastTick = monotonicMillis()
    }

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        start()
    }

    override fun run() {
        while (!TabooLibCommon.isStopped()) {
            if (lastTick != 0L && timeoutMillis.get() > 0L && monotonicMillis() - timeoutMillis.get() > lastTick) {
                severe(
                    "------------------------------",
                    "The server has likely stopped responding!",
                    "ItsSync is trying to save online players' data",
                    "------------------------------"
                )
                break
            }
            try {
                sleep(10000L)
            } catch (_: InterruptedException) {
                interrupt()
            }
        }
    }

}
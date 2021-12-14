package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


data class KnockedPlayer(val player: Player, val knockedBossbar: KnockedBossbar, val dedTime: Short) {
    lateinit var timeRunnable: BukkitRunnable
    var state: KnockedState = KnockedState.GROUND
    var time = dedTime
    var destroyed = false
    var isTreated = false
    var internalTimer = false
}

enum class KnockedState { GROUND, PLAYER_HEAD }
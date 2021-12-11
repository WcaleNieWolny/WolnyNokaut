package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.utils.LimboUtils

class KnockedFactory(private val plugin: JavaPlugin, private val limboUtils: LimboUtils, val nokautConfig: NokautConfig, val cache: KnockedCache) {
    fun createKnockedPlayer(player: Player) : KnockedPlayer =
        KnockedPlayer(player = player,
            limboUtils = limboUtils,
            plugin = plugin,
            cache = cache,
            knockedBossbar = KnockedBossbar(player, time = nokautConfig.dedTime.toFloat(), nokautConfig.treatmentTime.toFloat(), plugin),
            dedTime = nokautConfig.dedTime,
            healXP = nokautConfig.healXP,
            resuscitationForHeal1 = nokautConfig.resuscitationForHeal1,
            resuscitationForHeal2 = nokautConfig.resuscitationForHeal2,
            treatmentTime = nokautConfig.treatmentTime
    )
}
package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.limbo.LimboControler

class KnockedFactory(private val plugin: JavaPlugin, private val limboControler: LimboControler, val nokautConfig: NokautConfig, val cache: KnockedCache) {
    fun createKnockedPlayer(player: Player) : KnockedPlayer =
        KnockedPlayer(player = player,
            knockedBossbar = KnockedBossbar(player, time = nokautConfig.dedTime.toFloat(), nokautConfig.treatmentTime.toFloat(), plugin),
            dedTime = nokautConfig.dedTime,
    )
    fun createControler(): KnockedControler =
        KnockedControler(
            plugin = plugin,
            limboControler = limboControler,
            cache = cache,
            healXP = nokautConfig.healXP,
            resuscitationForHeal2 = nokautConfig.resuscitationForHeal2,
            resuscitationForHeal1 = nokautConfig.resuscitationForHeal1,
            treatmentTime = nokautConfig.treatmentTime
        )
}
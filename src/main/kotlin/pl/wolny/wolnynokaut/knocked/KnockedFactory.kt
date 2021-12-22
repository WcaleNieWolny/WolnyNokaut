package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.limbo.LimboController

class KnockedFactory(
    private val plugin: JavaPlugin,
    private val limboController: LimboController,
    private val nokautConfig: NokautConfig,
    private val cache: KnockedCache
) {
    fun createKnockedPlayer(player: Player): KnockedPlayer =
        KnockedPlayer(
            player = player,
            knockedBossbar = KnockedBossbar(
                player,
                time = nokautConfig.dedTime.toFloat(),
                nokautConfig.treatmentTime.toFloat(),
                plugin
            ),
            dedTime = nokautConfig.dedTime,
        )

    fun createControler(): KnockedController =
        KnockedController(
            plugin = plugin,
            limboController = limboController,
            cache = cache,
        )
}
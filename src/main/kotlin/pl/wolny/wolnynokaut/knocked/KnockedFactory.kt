package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.utils.LimboUtils

class KnockedFactory(private val plugin: JavaPlugin, private val limboUtils: LimboUtils, val nokautConfig: NokautConfig, val cache: KnockedCache) {
    fun createKnockedPlayer(player: Player) : KnockedPlayer = KnockedPlayer(player, limboUtils, plugin, nokautConfig, cache)
}
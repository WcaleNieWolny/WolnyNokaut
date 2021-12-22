package pl.wolny.wolnynokaut.knocked

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class KnockedCache: Listener {
    lateinit var factory: KnockedFactory
    operator fun get(uniqueId: UUID): KnockedPlayer? {
        return knockedPlayers[uniqueId]
    }
    operator fun get(player: Player): KnockedPlayer? {
        return knockedPlayers[player.uniqueId]
    }

    val knockedPlayers = mutableMapOf<UUID, KnockedPlayer>()

    fun getPlayersWithDriver(player: Player): List<KnockedPlayer>{
        return knockedPlayers.filterValues { knockedPlayer -> knockedPlayer.driver == player }.toList().map { pair -> pair.second }
    }
}
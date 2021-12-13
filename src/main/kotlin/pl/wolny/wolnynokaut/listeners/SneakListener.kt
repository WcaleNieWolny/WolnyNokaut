package pl.wolny.wolnynokaut.listeners

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedControler
import java.awt.Component

class SneakListener(val knockedCache: KnockedCache, val knockedControler: KnockedControler) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun playerToggleSneakEvent(event: PlayerToggleSneakEvent){
        val player = event.player
        val nearbyPlayers = player.getNearbyEntities(1.0, 3.0, 1.0)
            .filterIsInstance<Player>()
            .filter { player1 -> player1 != player}
            .filter { player1 -> knockedCache.knockedPlayers[player1.uniqueId] != null}
        if(nearbyPlayers.size > 1){
            player.sendMessage(net.kyori.adventure.text.Component.text()
                .content("Wykryto więcej niż jednego gracza w pobliżu! Reanimuję pojedyńczo!")
                .color(NamedTextColor.RED))
        }
        if(nearbyPlayers.isEmpty()){
            return
        }
        knockedControler.startRecovery(player, knockedCache[nearbyPlayers[0].uniqueId]!!)
    }
}
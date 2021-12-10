package pl.wolny.wolnynokaut.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import pl.wolny.wolnynokaut.knocked.KnockedCache

class DamageListener(val cache: KnockedCache): Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if(event.entity !is Player){
            return
        }
        val player = event.entity
        if(cache[player.uniqueId] != null){
            event.isCancelled = true
        }
    }
}
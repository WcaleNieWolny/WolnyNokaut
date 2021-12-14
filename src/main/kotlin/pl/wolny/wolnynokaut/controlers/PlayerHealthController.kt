package pl.wolny.wolnynokaut.controlers

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController

class PlayerHealthController(
    private val cache: KnockedCache,
    private val plugin: JavaPlugin,
    private val knockedController: KnockedController): Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player
        if (player.lastDamageCause!!.cause == EntityDamageEvent.DamageCause.VOID) {
            return
        }
        val dataContainer = player.persistentDataContainer
        val namespacedKey = NamespacedKey(plugin, "die_on_event")
        val data = dataContainer.get(namespacedKey, PersistentDataType.BYTE)
        data.apply {
            if (data == (1).toByte()) {
                dataContainer.set(namespacedKey, PersistentDataType.BYTE, 0)
                return
            }
        }
        if (player.vehicle != null) {
            player.vehicle!!.removePassenger(player)
        }
        event.isCancelled = true
        forcePlayerKnockout(player)
    }

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

    fun forcePlayerKnockout(player: Player){
        val knockedPlayer = cache.factory.createKnockedPlayer(player)
        cache.knockedPlayers[player.uniqueId] = knockedPlayer
        knockedController.putOnGround(knockedPlayer)
    }
}
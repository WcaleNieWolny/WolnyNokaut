package pl.wolny.wolnynokaut.controlers

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController
import pl.wolny.wolnynokaut.knocked.KnockedPlayer
import pl.wolny.wolnynokaut.utils.ComponentUtils
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt

class RescueController(private val knockedController: KnockedController,
                       private val healXP: Short,
                       private val resuscitationForHeal1: List<String>,
                       private val resuscitationForHeal2: List<String>,
                       private val treatmentTime: Short,
                       private val plugin: JavaPlugin,
                       private val knockedCache: KnockedCache
) : Listener{

    fun startRecovery(medic: Player, knockedPlayer: KnockedPlayer) {
        if (knockedPlayer.isTreated) {
            return
        }
        if (medic.totalExperience < healXP) {
            return
        }
        knockedController.stopInternalTimers(false, knockedPlayer)
        var time: Double = treatmentTime.toDouble()
        var i1 = -1
        var i2 = 0
        knockedPlayer.isTreated = true
        knockedPlayer.knockedBossbar.switchForRescue()
        val runnable = object : BukkitRunnable() {
            override fun run() {
                if (knockedPlayer.destroyed) {
                    this.cancel()
                }
                //
                if (!medic.isSneaking || !medic.isOnline || medic.totalExperience < healXP || knockedPlayer.player.location.distance(medic.location) >= 1.25
                ) {
                    this.cancel()
                    knockedController.startInternalTimers(knockedPlayer)
                    knockedPlayer.knockedBossbar.switchForDed()
                    knockedPlayer.isTreated = false
                    medic.clearTitle()
                    return
                }
                i1 += 1
                i2 += 1
                medic.showTitle(
                    Title.title(
                        ComponentUtils.format(resuscitationForHeal1[i1]),
                        ComponentUtils.format(resuscitationForHeal2[i2]),
                        Title.Times.of(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(6),
                            Duration.ofSeconds(0)
                        )
                    )
                )
                time -= 0.25
                medic.giveExp(-healXP);
                if (i1 == resuscitationForHeal1.size - 1) {
                    i1 = -1;
                }
                if (i2 == resuscitationForHeal1.size - 1) {
                    i2 = -1;
                }
                if (time == 0.00) {
                    this.cancel()
                    knockedController.forceRecovery(knockedPlayer)
                    medic.resetTitle()
                }
            }
        }
        runnable.runTaskTimer(plugin, 0, 5)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerToggleSneakEvent(event: PlayerToggleSneakEvent) {
        val player = event.player
        val nearbyPlayers = player.getNearbyEntities(1.0, 3.0, 1.0)
            .filterIsInstance<Player>()
            .filter { player1 -> player1 != player }
            .filter { player1 -> knockedCache.knockedPlayers[player1.uniqueId] != null }
        if (nearbyPlayers.size > 1) {
            player.sendMessage(
                net.kyori.adventure.text.Component.text()
                    .content("Wykryto więcej niż jednego gracza w pobliżu! Reanimuję pojedyńczo!")
                    .color(NamedTextColor.RED)
            )
        }
        if (nearbyPlayers.isEmpty()) {
            return
        }
        startRecovery(player, knockedCache[nearbyPlayers[0].uniqueId]!!)
    }
}
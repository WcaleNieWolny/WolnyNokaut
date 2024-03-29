package pl.wolny.wolnynokaut.knocked

import net.kyori.adventure.title.Title
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.spigotmc.event.entity.EntityDismountEvent
import pl.wolny.wolnynokaut.hook.WorldGuardHook
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.utils.ComponentUtils
import pl.wolny.wolnynokaut.utils.removeFakeEffect
import pl.wolny.wolnynokaut.utils.resetFakeGamemode
import pl.wolny.wolnynokaut.utils.sendFakeEffect
import java.time.Duration

class KnockedController(
    private val plugin: JavaPlugin,
    private val limboController: LimboController,
    private val cache: KnockedCache,
    private val worldGuardHook: WorldGuardHook,
    private val titleString: String,
    private val playerKillSubTitleEnabled: Boolean
): Listener {
    private fun getTimeRunnable(knockedPlayer: KnockedPlayer): BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            knockedPlayer.time = (knockedPlayer.time - 1).toShort()
            if (knockedPlayer.time <= 0) {
                killAndStop(knockedPlayer)
            }
        }
    }

    fun putOnGround(knockedPlayer: KnockedPlayer) {
        knockedPlayer.player.sendFakeEffect(15, true)
        knockedPlayer.player.sendFakeEffect(2,true)
        knockedPlayer.player.sendFakeEffect(8,true)
        limboController.setInLimbo(knockedPlayer.player)
        limboController.forceGround(knockedPlayer.player)
        startInternalTimers(knockedPlayer)
    }


    fun killAndStop(knockedPlayer: KnockedPlayer) {
        val player = knockedPlayer.player
        cache.knockedPlayers.remove(player.uniqueId)
        limboController.removeFromLimbo(player)
        limboController.removePlayerSlotLimitation(player)
        stopInternalTimers(true, knockedPlayer)
        knockedPlayer.knockedBossbar.removeRender()
        knockedPlayer.player.resetFakeGamemode()
        player.updateInventory()
        player.persistentDataContainer.set(NamespacedKey(plugin, "die_on_event"), PersistentDataType.BYTE, 1)
        player.health = 0.0
        knockedPlayer.destroyed = true
    }

    fun forceRecovery(knockedPlayer: KnockedPlayer) {
        val player = knockedPlayer.player
        cache.knockedPlayers.remove(player.uniqueId)
        cache.lastPlayerKillers.remove(player.uniqueId)
        limboController.removeFromLimbo(player)
        limboController.removePlayerSlotLimitation(player)
        knockedPlayer.knockedBossbar.removeRender()
        knockedPlayer.player.removeFakeEffect(PotionEffectType.SLOW)
        knockedPlayer.player.removeFakeEffect(PotionEffectType.BLINDNESS)
        knockedPlayer.player.removeFakeEffect(PotionEffectType.JUMP)
        stopInternalTimers(true, knockedPlayer)
        player.resetFakeGamemode()
        limboController.standUp(player)
    }

    fun startInternalTimers(knockedPlayer: KnockedPlayer) {
        if (knockedPlayer.internalTimer) {
            return
        }
        knockedPlayer.timeRunnable = getTimeRunnable(knockedPlayer)
        knockedPlayer.timeRunnable.runTaskTimer(plugin, 20, 20)
        knockedPlayer.internalTimer = true
        knockedPlayer.knockedBossbar.start()

    }

    fun stopInternalTimers(stopBossbar: Boolean, knockedPlayer: KnockedPlayer) {
        knockedPlayer.internalTimer = false
        knockedPlayer.timeRunnable.cancel()
        if (stopBossbar) {
            knockedPlayer.knockedBossbar.stop()
        }
    }
    fun forcePlayerKnockout(player: Player){
        val knockedPlayer = cache.factory.createKnockedPlayer(player)
        cache.knockedPlayers[player.uniqueId] = knockedPlayer
        putOnGround(knockedPlayer)
    }

    @EventHandler(priority = EventPriority.HIGH)
    private fun onPlayerQuit(event: PlayerQuitEvent)
    {
        if(cache[event.player.uniqueId] != null){
            killAndStop(knockedPlayer = cache[event.player.uniqueId]!!)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private fun onPlayerDismount(event: EntityDismountEvent){
        if(event.entity !is Player){
            return
        }
        val knockedPlayer = cache[event.entity.uniqueId] ?: return
        if(knockedPlayer.state == KnockedState.PLAYER_HEAD){
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private fun onPlayerSneek(event: PlayerToggleSneakEvent){
        val knockedPlayer = cache[event.player.uniqueId] ?: return
        if(knockedPlayer.state == KnockedState.GROUND){
            event.isCancelled = true
        }
    }

    fun checkSubTitle(player: Player){
        if(playerKillSubTitleEnabled){
            return
        }
        var lastDamageCause = player.lastDamageCause
        if (lastDamageCause != null) {
            val cause = lastDamageCause.cause
            if(cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                lastDamageCause = lastDamageCause as EntityDamageByEntityEvent
                val entity = lastDamageCause.damager
                if(entity is Player){
                    val title = Title.title(ComponentUtils.format(""), ComponentUtils.format(titleString.replace("{USER}", player.name)), Title.Times.of(Duration.ofSeconds(0), Duration.ofMillis(1500), Duration.ofSeconds(0)))
                    entity.showTitle(title)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        if(cache.lastPlayerKillers[event.player.uniqueId]  != null){
            player.killer = cache.lastPlayerKillers[event.player.uniqueId]
            cache.lastPlayerKillers[event.player.uniqueId] = null
        }

        if (player.lastDamageCause!!.cause == EntityDamageEvent.DamageCause.VOID) {
            return
        }

        if(!worldGuardHook.allowKnockout(player)){
            return
        }

        checkSubTitle(player)

        cache.lastPlayerKillers[event.player.uniqueId] = player.killer

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

}
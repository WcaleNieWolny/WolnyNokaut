package pl.wolny.wolnynokaut.knocked

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.utils.*
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt

class KnockedController(
    private val plugin: JavaPlugin,
    private val limboController: LimboController,
    private val cache: KnockedCache,
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
        limboController.setInLimbo(knockedPlayer.player)
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, knockedPlayer.player.entityId)
        val pose = EnumWrappers.EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcher.WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != knockedPlayer.player }
            .forEach { player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket) }
        knockedPlayer.player.sendFakeGameMode(GameMode.ADVENTURE)
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
        limboController.removeFromLimbo(player)
        limboController.removePlayerSlotLimitation(player)
        knockedPlayer.knockedBossbar.removeRender()
        knockedPlayer.player.removeFakeEffect(12)
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

}
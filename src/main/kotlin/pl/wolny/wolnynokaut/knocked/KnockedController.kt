package pl.wolny.wolnynokaut.knocked

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
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
) {
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
}
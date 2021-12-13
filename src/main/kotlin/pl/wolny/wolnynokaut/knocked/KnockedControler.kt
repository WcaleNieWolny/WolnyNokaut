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
import pl.wolny.wolnynokaut.limbo.LimboControler
import pl.wolny.wolnynokaut.utils.ComponentUtils
import pl.wolny.wolnynokaut.utils.EffectUtils
import pl.wolny.wolnynokaut.utils.FakeGameModeUtils
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt

class KnockedControler(
    val plugin: JavaPlugin,
    val limboControler: LimboControler,
    val cache: KnockedCache,
    private val healXP: Short,
    private val resuscitationForHeal1: List<String>,
    private val resuscitationForHeal2: List<String>,
    private val treatmentTime: Short
) {
    private fun getTimeRunnable(knockedPlayer: KnockedPlayer): BukkitRunnable = object : BukkitRunnable(){
        override fun run() {
            knockedPlayer.time = (knockedPlayer.time - 1).toShort()
            if(knockedPlayer.time <= 0){
                killAndStop(knockedPlayer)
            }
        }
    }

    fun putOnGround(knockedPlayer: KnockedPlayer){
        EffectUtils.sendFakeEffect(knockedPlayer.player, 15, true)
        limboControler.setInLimbo(knockedPlayer.player)
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, knockedPlayer.player.entityId)
        val pose = EnumWrappers.EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcher.WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != knockedPlayer.player }.forEach{ player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket)}
        FakeGameModeUtils.sendFakeGameMode(knockedPlayer.player, GameMode.ADVENTURE)
        startInternalTimers(knockedPlayer)
    }
    fun killAndStop(knockedPlayer: KnockedPlayer){
        val player = knockedPlayer.player
        cache.knockedPlayers.remove(player.uniqueId)
        limboControler.removeFromLimbo(player)
        limboControler.removePlayerSlotLimitation(player)
        stopInternalTimers(true, knockedPlayer)
        knockedPlayer.knockedBossbar.removeRender()
        FakeGameModeUtils.resetGamemode(player)
        player.updateInventory()
        player.persistentDataContainer.set(NamespacedKey(plugin, "die_on_event"), PersistentDataType.BYTE, 1)
        player.health = 0.0
        knockedPlayer.destroyed = true
    }
    fun forceRecovery(knockedPlayer: KnockedPlayer){
        val player = knockedPlayer.player
        cache.knockedPlayers.remove(player.uniqueId)
        limboControler.removeFromLimbo(player)
        limboControler.removePlayerSlotLimitation(player)
        knockedPlayer.knockedBossbar.removeRender()
        EffectUtils.removeEffect(player, 12)
        stopInternalTimers(true, knockedPlayer)
        FakeGameModeUtils.resetGamemode(player)
        limboControler.standUp(player)
    }
    fun startInternalTimers(knockedPlayer: KnockedPlayer){
        if(knockedPlayer.internalTimer){
            return
        }
        knockedPlayer.timeRunnable = getTimeRunnable(knockedPlayer)
        knockedPlayer.timeRunnable.runTaskTimer(plugin, 20, 20)
        knockedPlayer.internalTimer = true
        knockedPlayer.knockedBossbar.start()

    }
    fun stopInternalTimers(stopBossbar: Boolean, knockedPlayer: KnockedPlayer){
        knockedPlayer.internalTimer = false
        knockedPlayer.timeRunnable.cancel()
        if(stopBossbar){
            knockedPlayer.knockedBossbar.stop()
        }

    }
    fun distance(location1: Location, location2: Location) : Double = sqrt((location1.x - location2.x).pow(2) + (location1.y - location2.y).pow(2) + (location1.z - location2.z).pow(2))

    fun startRecovery(medic: Player, knockedPlayer: KnockedPlayer) {
        if(knockedPlayer.isTreated){return}
        if(medic.totalExperience < healXP){return}
        stopInternalTimers(false, knockedPlayer)
        var time: Double = treatmentTime.toDouble()
        var i1 = -1
        var i2 = 0
        knockedPlayer.isTreated = true
        knockedPlayer.knockedBossbar.switchForRescue()
        val runnable = object : BukkitRunnable() {
            override fun run() {
                if(knockedPlayer.destroyed){
                    this.cancel()
                }
                //
                if(!medic.isSneaking || !medic.isOnline || medic.totalExperience < healXP || distance(knockedPlayer.player.location, medic.location) >= 1.25){
                    this.cancel()
                    startInternalTimers(knockedPlayer)
                    knockedPlayer.knockedBossbar.switchForDed()
                    knockedPlayer.isTreated = false
                    medic.clearTitle()
                    return
                }
                i1 += 1
                i2 += 1
                medic.showTitle(
                    Title.title(
                        ComponentUtils.format(resuscitationForHeal1[i1]), ComponentUtils.format(resuscitationForHeal2[i2]), Title.Times.of(
                    Duration.ofSeconds(0),
                    Duration.ofSeconds(6),
                    Duration.ofSeconds(0))))
                time -= 0.25
                medic.giveExp(-healXP);
                if(i1 == resuscitationForHeal1.size-1){
                    i1 = -1;
                }
                if(i2 == resuscitationForHeal1.size-1){
                    i2 = -1;
                }
                if(time == 0.00){
                    this.cancel()
                    forceRecovery(knockedPlayer)
                    medic.resetTitle()
                }
            }
        }
        runnable.runTaskTimer(plugin, 0, 5)
    }
}
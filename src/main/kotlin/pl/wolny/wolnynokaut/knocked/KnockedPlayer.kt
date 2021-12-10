package pl.wolny.wolnynokaut.knocked

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.utils.ComponentUtils
import pl.wolny.wolnynokaut.utils.EffectUtils
import pl.wolny.wolnynokaut.utils.FakeGameModeUtils
import pl.wolny.wolnynokaut.utils.LimboUtils
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt


class KnockedPlayer(val player: Player, private val limboUtils: LimboUtils, private val plugin: JavaPlugin, private val config: NokautConfig, private val cache: KnockedCache) {
    var state: KnockedState = KnockedState.GROUND
    var time = 30
    var destroyed = false
    var isTreated = false
    var internalTimer = false
    private val knockedBossbar = KnockedBossbar(player, time = time.toFloat(), config.treatmentTime.toFloat(), plugin)

    private fun getTimeRunnable(): BukkitRunnable = object : BukkitRunnable(){
        override fun run() {
            time -= 1
            if(time <= 0){
                killAndStop()
            }
        }
    }

    private var timeRunnable = getTimeRunnable()

    fun putOnGround(){
        EffectUtils.sendFakeEffect(player, 15, true)
        limboUtils.setInLimbo(player)
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, player.entityId)
        val pose = EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != player }.forEach{player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket)}
        FakeGameModeUtils.sendFakeGameMode(player, GameMode.ADVENTURE)
        startInternalTimers()
    }
    fun killAndStop(){
        limboUtils.removeFromLimbo(player)
        limboUtils.removePlayerSlotLimitation(player)
        stopInternalTimers(true)
        knockedBossbar.removeRender()
        FakeGameModeUtils.resetGamemode(player)
        player.updateInventory()
        player.persistentDataContainer.set(NamespacedKey(plugin, "die_on_event"), PersistentDataType.BYTE, 1)
        player.health = 0.0
        destroyed = true
    }
    fun forceRecovery(){
        cache.knockedPlayers.remove(player.uniqueId)
        limboUtils.removeFromLimbo(player)
        limboUtils.removePlayerSlotLimitation(player)
        knockedBossbar.removeRender()
        EffectUtils.removeEffect(player, 12)
        stopInternalTimers(true)
        FakeGameModeUtils.resetGamemode(player)
        limboUtils.standUp(player)
    }
    fun startInternalTimers(){
        if(internalTimer){
            return
        }
        timeRunnable = getTimeRunnable()
        timeRunnable.runTaskTimer(plugin, 0, 20)
        internalTimer = true
        knockedBossbar.start()

    }
    fun stopInternalTimers(stopBossbar: Boolean){
        internalTimer = false
        timeRunnable.cancel()
        if(stopBossbar){
            knockedBossbar.stop()
        }

    }
    fun distance(location1: Location, location2: Location) : Double = sqrt((location1.x - location2.x).pow(2) + (location1.y - location2.y).pow(2) + (location1.z - location2.z).pow(2))

    fun startRecovery(medic: Player) {
        if(isTreated){return}
        stopInternalTimers(false)
        val xp = config.healXP
        val msg1 = config.resuscitationForHeal1
        val msg2 = config.resuscitationForHeal2
        var time: Double = config.treatmentTime.toDouble()
        var i1 = -1
        var i2 = 0
        isTreated = true
        knockedBossbar.switchForRescue()
        val runnable = object : BukkitRunnable() {
            override fun run() {
                if(destroyed){
                    this.cancel()
                }
                //medic.totalExperience < xp
                if(!medic.isSneaking || !medic.isOnline || distance(player.location, medic.location) >= 1.25){
                    this.cancel()
                    startInternalTimers()
                    knockedBossbar.switchFoDed()
                    isTreated = false
                    return
                }
                i1 += 1
                i2 += 1
                medic.showTitle(Title.title(ComponentUtils.format(msg1[i1]), ComponentUtils.format(msg2[i2]), Title.Times.of(
                    Duration.ofSeconds(0),
                    Duration.ofSeconds(6),
                    Duration.ofSeconds(0))))
                time -= 0.25
                medic.giveExp(-xp / 4);
                if(i1 == msg1.size-1){
                    i1 = -1;
                }
                if(i2 == msg2.size-1){
                    i2 = -1;
                }
                if(time == 0.00){
                    this.cancel()
                    forceRecovery()
                    medic.resetTitle()
                }
                }
            }
        runnable.runTaskTimer(plugin, 0, 5)
        }
}
enum class KnockedState{GROUND, PLAYER_HEAD}
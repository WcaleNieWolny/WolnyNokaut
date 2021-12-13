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
import java.time.Duration
import kotlin.math.pow
import kotlin.math.sqrt


class KnockedPlayer(val player: Player, val knockedBossbar: KnockedBossbar, dedTime: Short) {
    lateinit var timeRunnable: BukkitRunnable
    var state: KnockedState = KnockedState.GROUND
    var time = dedTime
    var destroyed = false
    var isTreated = false
    var internalTimer = false
}
enum class KnockedState{GROUND, PLAYER_HEAD}
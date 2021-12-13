package pl.wolny.wolnynokaut.knocked

import com.comphenix.protocol.events.PacketAdapter
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.roundToInt

class KnockedBossbar(val player: Player, val time: Float, val time2: Float, private val plugin: JavaPlugin) {
    var timeDedLeft = time+1
    var timeRescueLeft = time2+1
    var running = false
    private val dedBossBar: BossBar = BossBar.bossBar(Component.text()
        .content("NOKAUT: $timeDedLeft")
        .color(TextColor.color(NamedTextColor.AQUA)),
        1F,
        BossBar.Color.RED, BossBar.Overlay.NOTCHED_20)
    private val rescueBossBar: BossBar = BossBar.bossBar(Component.text()
        .content("PODNOSZENIE: $timeRescueLeft")
        .color(TextColor.color(NamedTextColor.AQUA)),
        1F,
        BossBar.Color.GREEN, BossBar.Overlay.PROGRESS)
    private var knockedBossbarState = KnockedBossbarState.DED
    private fun getRunnable(): BukkitRunnable = object : BukkitRunnable(){
        override fun run() {
            if(knockedBossbarState == KnockedBossbarState.DED){
                timeDedLeft -= 1
                if(timeDedLeft <= 0){
                    stop()
                    player.hideBossBar(dedBossBar)
                    this.cancel()
                    return
                }
                dedBossBar.progress((1F / (time / timeDedLeft) * 100.0F).roundToInt() /100.0F)
                dedBossBar.name(Component.text()
                    .content("NOKAUT: $timeDedLeft")
                    .color(TextColor.color(NamedTextColor.AQUA)))
            }else{
                timeRescueLeft -= 1
                if(timeRescueLeft <= 0){
                    stop()
                    player.hideBossBar(rescueBossBar)
                    this.cancel()
                    return
                }
                rescueBossBar.progress((1F / (time2 / timeRescueLeft) * 100.0F).roundToInt() /100.0F)
                rescueBossBar.name(Component.text()
                    .content("PODNOSZENIE: $timeRescueLeft")
                    .color(TextColor.color(NamedTextColor.AQUA)))
            }
        }
    }
    lateinit var lastRunnalbe: BukkitRunnable

    fun start(){
        if(running){return}
        running = true
        lastRunnalbe = getRunnable()
        player.showBossBar(dedBossBar)
        lastRunnalbe.runTaskTimer(plugin, 0, 20)
    }
    fun stop(){
        running = false
        lastRunnalbe.cancel()
    }
    fun removeRender(){
        player.hideBossBar(dedBossBar)
        player.hideBossBar(rescueBossBar)
    }
    fun switchForRescue(){
        rescueBossBar.progress(1F)
        rescueBossBar.name(Component.text()
            .content("PODNOSZENIE: $timeRescueLeft")
            .color(TextColor.color(NamedTextColor.AQUA)))
        timeRescueLeft = time2+1
        knockedBossbarState = KnockedBossbarState.RESCUE
        player.hideBossBar(dedBossBar)
        player.showBossBar(rescueBossBar)
    }
    fun switchForDed(){
        knockedBossbarState = KnockedBossbarState.DED
        timeRescueLeft = time2
        player.hideBossBar(rescueBossBar)
        player.showBossBar(dedBossBar)
    }
}
enum class KnockedBossbarState{
    DED,RESCUE
}
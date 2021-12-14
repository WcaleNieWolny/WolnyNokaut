package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController

class LimboGeneralPacketAdapter(private val plugin: JavaPlugin, private val limboController: LimboController) : PacketAdapter(
    plugin,
    ListenerPriority.HIGHEST,
    PacketType.values().filter { a -> a.isClient }) {
    override fun onPacketReceiving(event: PacketEvent?) {
        if (event != null) {
            if (limboController.limboList.contains(event.player)) {
                event.isCancelled = limboController.limboLogicHandler.handle(event)
            }
        }
    }
}
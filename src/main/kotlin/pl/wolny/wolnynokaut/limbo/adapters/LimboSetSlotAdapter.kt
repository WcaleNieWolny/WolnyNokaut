package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType.Play.Server.SET_SLOT
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController

class LimboSetSlotAdapter(private val plugin: JavaPlugin, private val limboController: LimboController) : PacketAdapter(
    plugin,
    ListenerPriority.NORMAL,
    SET_SLOT
){
    override fun onPacketSending(event: PacketEvent?) {
        if (event != null) {
            limboController.limboLogicHandler.handleSlotEvent(event)
        }
    }
}
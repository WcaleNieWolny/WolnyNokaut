package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController

class LimboMetaDataAdapter(private val plugin: JavaPlugin, private val limboController: LimboController) : PacketAdapter(
    plugin,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.ENTITY_METADATA) {
    override fun onPacketSending(event: PacketEvent?) {
        if (event != null) {
            if (limboController.limboList.any { it.entityId == event.packet.integers.read(0) }) {
                limboController.limboLogicHandler.handleMetaData(event)
            }
        } else {
            throw (RuntimeException("Packet event is null?"))
        }
    }
}
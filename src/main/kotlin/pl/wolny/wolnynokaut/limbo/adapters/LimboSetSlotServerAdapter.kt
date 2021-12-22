package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType.Play.Server.SET_SLOT
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController

class LimboSetSlotServerAdapter(private val plugin: JavaPlugin, private val slotMap: MutableMap<Player, Int>) : PacketAdapter(
    plugin,
    ListenerPriority.NORMAL,
    SET_SLOT,
){
    override fun onPacketSending(event: PacketEvent?) {
        if (event != null) {
            handleSetSlotEvent(event)
        }
    }
    fun handleSetSlotEvent(event: PacketEvent) {
        if (slotMap.contains(event.player)) {
            if(event.packet.integers.read(0) == -99){
                event.packet.integers.write(0, -1)
                return
            }
            if(event.packet.integers.read(0) == -88){
                event.packet.integers.write(0, -2)
            }
            if(event.packet.itemModifier.read(0).type == Material.FILLED_MAP){
                return
            }
            event.isCancelled = true
        }
    }
}
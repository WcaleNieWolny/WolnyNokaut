package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.map.MapFactory

class LimboGeneralPacketController(
    plugin: JavaPlugin,
    private val limboController: LimboController,
    private val slotMap: MutableMap<Player, Int>,
    private val mapFactory: MapFactory
) : PacketAdapter(
    plugin,
    ListenerPriority.NORMAL,
    PacketType.values().filter { a -> a.isClient }) {
    override fun onPacketReceiving(event: PacketEvent?) {
        if (event != null) {
            if (limboController.limboList.contains(event.player)) {
                handle(event)
            }
        }
    }
    private fun handle(event: PacketEvent) {
        val player = event.player
        when (event.packet.type) {
            PacketType.Play.Client.KEEP_ALIVE -> {
                event.isCancelled = false
                return
            }
            PacketType.Play.Client.WINDOW_CLICK -> {
                handleSlotClick(player)
                event.isCancelled = true
                return
            }
            PacketType.Play.Client.CHAT -> {
                event.isCancelled = false
                return
            }
            PacketType.Play.Client.POSITION -> {
                if(limboController.positionList.contains(player)){
                    if(limboController.positionList.contains(player)){
                        event.isCancelled = true
                        handlePositionPacket(player, event.packet)
                    }
                }
            }
            PacketType.Play.Client.LOOK -> {
                if(limboController.positionList.contains(player)){
                    event.isCancelled = true
                    handlePositionPacket(player, event.packet)
                }
            }
            PacketType.Play.Client.POSITION_LOOK -> {
                if(limboController.positionList.contains(player)){
                    event.isCancelled = true
                    handlePositionPacket(player, event.packet)
                }
            }
            PacketType.Play.Client.HELD_ITEM_SLOT -> {
                if (slotMap.contains(player)){
                    sendSlotPacket(player, slotMap[event.player]!!)
                }
            }
            PacketType.Play.Client.BLOCK_DIG -> {
                handleDigEvent(event)
                event.isCancelled = true
                return
            }
            PacketType.Play.Client.STEER_VEHICLE -> {
                if(event.packet.booleans.read(1)){
                    return
                }
            }
        }
        event.isCancelled = false
    }

    private fun getCompressedAngle(value: Float): Float {
        return (value * 256.0f / 360.0f)
    }

    private fun handlePositionPacket(player: Player, dataPacket: PacketContainer) {
        val packet = PacketContainer(PacketType.Play.Server.POSITION)
        val location = player.location

        when (dataPacket.type) {
            PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK -> {
                val x = dataPacket.doubles.read(0)
                val y = dataPacket.doubles.read(1)
                val z = dataPacket.doubles.read(2)


                if (x == location.x && y == location.y && z == location.z) {
                    println("nah")
                    return
                }
            }
            PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK -> {
                val yaw = dataPacket.float.read(0)
                val pitch = dataPacket.float.read(1)

                if (pitch == location.pitch && yaw == location.yaw) {
                    return
                }
            }
        }

        packet.modifier.writeDefaults()
        packet.doubles.write(0, player.location.x)
        packet.doubles.write(1, player.location.y)
        packet.doubles.write(2, player.location.z)
        packet.float.write(0, 0F)
        packet.float.write(1, 90F)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    fun sendSlotPacket(player: Player, int: Int) {
        val packet = PacketContainer(PacketType.Play.Server.HELD_ITEM_SLOT)
        packet.integers.write(0, int)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    fun handleSlotClick(player: Player){
        val packet = PacketContainer(PacketType.Play.Server.SET_SLOT)
        packet.integers.write(0, -99)
        packet.integers.write(1, -1)
        packet.itemModifier.write(0, ItemStack(Material.AIR, 1))
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
        val packet2 = PacketContainer(PacketType.Play.Server.SET_SLOT)
        packet2.integers.write(0, -88) //-2
        packet2.integers.write(1, 36)
        packet2.itemModifier.write(0, mapFactory.generateMapItem(player))
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet2)
    }
    fun handleDigEvent(event: PacketEvent) {
        val enum = event.packet.playerDigTypes.read(0)
        if (enum == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || enum == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
            val location = event.packet.blockPositionModifier.read(0)
            Bukkit.getServer().scheduler.scheduleSyncDelayedTask(plugin, {
                val packet = PacketContainer(PacketType.Play.Server.BLOCK_CHANGE)
                packet.blockPositionModifier.write(0, location)
                val block = event.player.world.getBlockAt(location.x, location.y, location.z)
                val wrappedBlock = WrappedBlockData.createData(block.type)
                packet.blockData.write(0, wrappedBlock)
                ProtocolLibrary.getProtocolManager().sendServerPacket(event.player, packet)
            }, 0)
        }
    }
}
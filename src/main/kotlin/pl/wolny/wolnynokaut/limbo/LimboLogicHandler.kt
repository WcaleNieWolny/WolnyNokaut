package pl.wolny.wolnynokaut.limbo

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class LimboLogicHandler(val plugin: JavaPlugin, val slotMap: MutableMap<Player, Int>) {
    private val protocolManager = ProtocolLibrary.getProtocolManager()
    fun handle(event: PacketEvent) : Boolean{
        when (event.packet.type) {
            PacketType.Play.Client.KEEP_ALIVE -> {
                return false
            }
            PacketType.Play.Client.CHAT -> return false
            PacketType.Play.Client.POSITION -> {
                //                                val packet = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT)
                //                                packet.integers.write(0, player.entityId)
                //                                packet.doubles.write(0, player.location.x)
                //                                packet.doubles.write(1, player.location.y)
                //                                packet.doubles.write(2, player.location.z)
                //                                packet.bytes.write(0, (player.location.yaw * 256.0F / 360.0F).toInt().toByte())
                //                                packet.bytes.write(1, (player.location.pitch * 256.0F / 360.0F).toInt().toByte())
                sendPositionPacket(event.player)
            }
            PacketType.Play.Client.LOOK -> {
                sendPositionPacket(event.player)
            }
            PacketType.Play.Client.HELD_ITEM_SLOT -> {
                if(slotMap.contains(event.player))
                    sendSlotPacket(event.player, slotMap[event.player]!!)
            }
            PacketType.Play.Client.BLOCK_DIG ->{
                handleDigEvent(event)
            }
        }
        return true
    }
    fun handleDigEvent(event: PacketEvent){
        val enum = event.packet.playerDigTypes.read(0)
        if(enum == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || enum == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK){
            val location = event.packet.blockPositionModifier.read(0)
            Bukkit.getServer().scheduler.scheduleSyncDelayedTask(plugin, {
                val packet = PacketContainer(PacketType.Play.Server.BLOCK_CHANGE)
                packet.blockPositionModifier.write(0, location)
                val block = event.player.world.getBlockAt(location.x, location.y, location.z)
                val wrappedBlock = WrappedBlockData.createData(block.type)
                packet.blockData.write(0, wrappedBlock)
                protocolManager.sendServerPacket(event.player, packet)
            }, 0)
        }
    }
    fun handleMetaData(event: PacketEvent){
        val pose = EnumWrappers.EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val watchableCollection: MutableList<WrappedWatchableObject> = event.packet.watchableCollectionModifier!!.read(0)
        if(watchableCollection.size < 6) {
            return
        }
        watchableCollection[6] = WrappedWatchableObject(
            WrappedDataWatcher.WrappedDataWatcherObject(
                6,
                serializer
            ), pose.toNms()
        )
        event.packet.watchableCollectionModifier.write(0, watchableCollection)
    }
    private fun handleSlotEvent(event: PacketEvent){
        if(slotMap.contains(event.player)){
            event.isCancelled = true
            sendSlotPacket(event.player, slotMap[event.player]!!)
        }
    }
    private fun sendPositionPacket(player: Player){
        val packet = PacketContainer(PacketType.Play.Server.POSITION)
        packet.modifier.writeDefaults()
        packet.doubles.write(0, player.location.x);
        packet.doubles.write(1, player.location.y);
        packet.doubles.write(2, player.location.z);
        packet.float.write(0, 0F);
        packet.float.write(1, 90F);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }
    fun sendSlotPacket(player: Player, int: Int){
        val packet = PacketContainer(PacketType.Play.Server.HELD_ITEM_SLOT)
        packet.integers.write(0, int)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }
}
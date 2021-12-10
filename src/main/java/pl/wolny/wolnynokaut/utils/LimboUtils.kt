package pl.wolny.wolnynokaut.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.PacketType.Play.Client.*
import com.comphenix.protocol.PacketType.Play.Server.BLOCK_CHANGE
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pl.wolny.wolnynokaut.map.MapFactory
import java.util.*


class LimboUtils (val plugin: JavaPlugin, private val mapFactory: MapFactory){
    private val limboList = mutableListOf<Player>()
    private val slotMap = mutableMapOf<Player, Int>()
    init {
        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(
            object : PacketAdapter(
                plugin, ListenerPriority.HIGHEST,
                PacketType.values().filter { a -> a.isClient }
            ) {
                override fun onPacketReceiving(event: PacketEvent?) {
                    if (event != null) {
                        if (limboList.contains(event.player)) {
                            when (event.packet.type) {
                                PacketType.Play.Client.KEEP_ALIVE -> {
                                    return
                                }
                                PacketType.Play.Client.CHAT -> return
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
                                HELD_ITEM_SLOT -> {
                                    if(slotMap.contains(event.player))
                                        sendSlotPacket(event.player, slotMap[event.player]!!)
                                }
                                BLOCK_DIG ->{
                                    val enum = event.packet.playerDigTypes.read(0)
                                    if(enum == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || enum == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK){
                                        val location = event.packet.blockPositionModifier.read(0)
                                        Bukkit.getServer().scheduler.scheduleSyncDelayedTask(plugin, {
                                            val packet = PacketContainer(BLOCK_CHANGE)
                                            packet.blockPositionModifier.write(0, location)
                                            val block = event.player.world.getBlockAt(location.x, location.y, location.z)
                                            val wrappedBlock = WrappedBlockData.createData(block.type)
                                            packet.blockData.write(0, wrappedBlock)
                                            protocolManager.sendServerPacket(event.player, packet)
                                        }, 0)
                                    }
                                }
                            }
                            event.isCancelled = true
                        }
                    }
                }
            })
        protocolManager.addPacketListener(
            object : PacketAdapter(
                plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.SET_SLOT
            ) {
                override fun onPacketSending(event: PacketEvent?) {
                    if (event != null) {
                        if(slotMap.contains(event.player)){
                            event.isCancelled = true
                            sendSlotPacket(event.player, slotMap[event.player]!!)
                        }
                    }
                }
            })
        protocolManager.addPacketListener(
            object : PacketAdapter(
                plugin, ListenerPriority.HIGHEST,
                PacketType.Play.Server.ENTITY_METADATA
            ) {
                override fun onPacketSending(event: PacketEvent?) {
                    if (event != null) {
                        if(limboList.any { it.entityId == event.packet.integers.read(0)}){
                            val pose = EntityPose.SWIMMING
                            val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
                            val watchableCollection: MutableList<WrappedWatchableObject> = event.packet.watchableCollectionModifier!!.read(0)
                            if(watchableCollection.size < 6) {
                                return
                            }
                            watchableCollection[6] = WrappedWatchableObject(
                                WrappedDataWatcherObject(
                                    6,
                                    serializer
                                ), pose.toNms()
                            )
                            event.packet.watchableCollectionModifier.write(0, watchableCollection)
                        }
                    }else{
                        throw (RuntimeException("Packet event is null?"))
                    }
                }

            })

    }

    fun setInLimbo(player: Player){
        val playerInv: MutableMap<Int, ItemStack?> = mutableMapOf()
        getPlayerInventoryHashMap(player, playerInv)
        player.inventory.clear()
        mapFactory.generateMap(player.world, player)
        player.updateInventory()
        limboList.add(player)
        slotMap[player] = 4
        sendSlotPacket(player, 4)
        player.inventory.clear()
        giveBackPlayerInventory(player, playerInv)
    }
    private fun formatYawOrPitch(int: Int): Byte{
        return (int * 256.0F / 360.0F).toInt().toByte()
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
    private fun getPlayerInventoryHashMap(player: Player, map: MutableMap<Int, ItemStack?>){
        for (i in 0..44){
            map[i] = player.inventory.getItem(i)
        }
    }
    private fun giveBackPlayerInventory(player: Player, map: MutableMap<Int, ItemStack?>){
        map.forEach{
            if(it.value != null){
                player.inventory.setItem(it.key, it.value)
            }

        }
    }
    private fun sendSlotPacket(player: Player, int: Int){
        val packet = PacketContainer(PacketType.Play.Server.HELD_ITEM_SLOT)
        packet.integers.write(0, int)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }
    fun removeFromLimbo(player: Player){
        limboList.remove(player)
        slotMap.remove(player)
        player.updateInventory()
    }
    fun updatePlayerSlot(player: Player, int: Int){
        slotMap[player] = int
    }
    fun removePlayerSlotLimitation(player: Player){
        slotMap.remove(player)
    }
    fun standUp(player: Player){
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, player.entityId)
        val pose = EntityPose.STANDING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != player }.forEach{player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket)}
    }
}
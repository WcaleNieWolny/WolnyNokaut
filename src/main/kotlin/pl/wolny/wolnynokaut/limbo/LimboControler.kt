package pl.wolny.wolnynokaut.limbo

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.map.MapFactory


class LimboControler (val plugin: JavaPlugin, private val mapFactory: MapFactory){
    private val limboList = mutableListOf<Player>()
    private val slotMap = mutableMapOf<Player, Int>()
    val limboLogicHandler: LimboLogicHandler = LimboLogicHandler(plugin, slotMap)
    fun init(){
        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(
            object : PacketAdapter(
                plugin, ListenerPriority.HIGHEST,
                PacketType.values().filter { a -> a.isClient }
            ) {
                override fun onPacketReceiving(event: PacketEvent?) {
                    if (event != null) {
                        if (limboList.contains(event.player)) {
                            event.isCancelled = limboLogicHandler.handle(event)
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
                        limboLogicHandler.handleDigEvent(event)
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
                            limboLogicHandler.handleMetaData(event)
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
        limboLogicHandler.sendSlotPacket(player, 4)
        player.inventory.clear()
        giveBackPlayerInventory(player, playerInv)
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
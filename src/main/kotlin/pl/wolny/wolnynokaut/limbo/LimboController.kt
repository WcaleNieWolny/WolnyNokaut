package pl.wolny.wolnynokaut.limbo

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.limbo.adapters.LimboGeneralPacketController
import pl.wolny.wolnynokaut.limbo.adapters.LimboMetaDataAdapter
import pl.wolny.wolnynokaut.limbo.adapters.LimboSetSlotServerAdapter

import pl.wolny.wolnynokaut.map.MapFactory
import pl.wolny.wolnynokaut.utils.sendFakeGameMode


class LimboController(private val plugin: JavaPlugin, private val mapFactory: MapFactory, cache: KnockedCache) {
    val limboList = mutableListOf<Player>()
    val slotMap = mutableMapOf<Player, Int>()
    val positionList = mutableListOf<Player>()
    val metaList = mutableListOf<Player>()
    private val generalController = LimboGeneralPacketController(plugin, this, slotMap, mapFactory)
    fun init() {
        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(generalController)
        protocolManager.addPacketListener(LimboSetSlotServerAdapter(plugin, slotMap))
        protocolManager.addPacketListener(LimboMetaDataAdapter(plugin, this))
    }

    fun setInLimbo(player: Player) {
        val playerInv: MutableMap<Int, ItemStack?> = mutableMapOf()
        getPlayerInventoryHashMap(player, playerInv)
        player.inventory.clear()
        mapFactory.generateMap(player)
        player.updateInventory()
        limboList.add(player)
        positionList.add(player)
        metaList.add(player)
        slotMap[player] = 0
        generalController.sendSlotPacket(player, 4)
        player.inventory.clear()
        giveBackPlayerInventory(player, playerInv)
    }

    private fun getPlayerInventoryHashMap(player: Player, map: MutableMap<Int, ItemStack?>) {
        for (i in 0..44) {
            map[i] = player.inventory.getItem(i)
        }
    }

    private fun giveBackPlayerInventory(player: Player, map: MutableMap<Int, ItemStack?>) {
        map.forEach {
            if (it.value != null) {
                player.inventory.setItem(it.key, it.value)
            }

        }
    }

    fun removeFromLimbo(player: Player) {
        limboList.remove(player)
        slotMap.remove(player)
        positionList.remove(player)
        metaList.remove(player)
        player.updateInventory()
    }

    fun updatePlayerSlot(player: Player, int: Int) {
        slotMap[player] = int
        generalController.sendSlotPacket(player, int)
    }

    fun removePlayerSlotLimitation(player: Player) {
        slotMap.remove(player)
    }

    fun standUp(player: Player) {
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, player.entityId)
        val pose = EntityPose.STANDING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != player }
            .forEach { player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket) }
    }

    fun forceGround(player: Player) {
        val entityMetadataPacket = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)
        entityMetadataPacket.integers.write(0, player.entityId)
        val pose = EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val wrappedWatchableObjects = WrappedDataWatcher()
        wrappedWatchableObjects.setObject(WrappedDataWatcherObject(6, serializer), pose.toNms())
        entityMetadataPacket.watchableCollectionModifier.write(0, wrappedWatchableObjects.watchableObjects)
        Bukkit.getOnlinePlayers().filter { player1 -> player1 != player.player }
            .forEach { player2 -> ProtocolLibrary.getProtocolManager().sendServerPacket(player2, entityMetadataPacket) }
        player.sendFakeGameMode(GameMode.ADVENTURE)
    }
}
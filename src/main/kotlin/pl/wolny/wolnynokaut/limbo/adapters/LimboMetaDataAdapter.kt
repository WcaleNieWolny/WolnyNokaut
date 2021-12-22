package pl.wolny.wolnynokaut.limbo.adapters

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.limbo.LimboController

class LimboMetaDataAdapter(private val plugin: JavaPlugin, private val limboController: LimboController) : PacketAdapter(
    plugin,
    ListenerPriority.HIGHEST,
    PacketType.Play.Server.ENTITY_METADATA) {
    override fun onPacketSending(event: PacketEvent?) {
        if (event != null) {
            if (limboController.metaList.any { it.entityId == event.packet.integers.read(0) }) {
                handleMetaData(event)
            }
        } else {
            throw (RuntimeException("Packet event is null?"))
        }
    }
    fun handleMetaData(event: PacketEvent) {
        val pose = EnumWrappers.EntityPose.SWIMMING
        val serializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
        val watchableCollection: MutableList<WrappedWatchableObject> =
            event.packet.watchableCollectionModifier!!.read(0)
        if (watchableCollection.size < 6) {
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
}
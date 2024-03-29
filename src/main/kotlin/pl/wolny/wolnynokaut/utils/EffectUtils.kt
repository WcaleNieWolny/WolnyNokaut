package pl.wolny.wolnynokaut.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

fun Player.sendFakeEffect(value: Byte, hideParticle: Boolean) {
    val packetContainer = PacketContainer(PacketType.Play.Server.ENTITY_EFFECT)
    packetContainer.integers.write(0, player?.entityId ?: -1)
    packetContainer.bytes.write(0, value)
    packetContainer.bytes.write(1, 127)
    packetContainer.integers.write(1, Int.MAX_VALUE - 1)
    packetContainer.bytes.write(2, 1)
    packetContainer.bytes.write(2, (if (hideParticle) 0 else 1).toByte())
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer)
}

fun Player.removeFakeEffect(value: PotionEffectType) {
    val packetContainer = PacketContainer(PacketType.Play.Server.REMOVE_ENTITY_EFFECT)
    packetContainer.integers.write(0, player?.entityId ?: -1)
    packetContainer.effectTypes.write(0, value)
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer)
}
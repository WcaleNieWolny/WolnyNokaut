package pl.wolny.wolnynokaut.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun Player.sendFakeGameMode(gameMode: GameMode) {
    val gameModeInt: Int = when (gameMode) {
        GameMode.SURVIVAL -> 0
        GameMode.ADVENTURE -> 2
        GameMode.CREATIVE -> 1
        GameMode.SPECTATOR -> 3
    }
    val packet = PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE)
    packet.gameStateIDs.write(0, gameModeInt)
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
}

fun Player.resetFakeGamemode() {
    player!!.sendFakeGameMode(player!!.gameMode)
}
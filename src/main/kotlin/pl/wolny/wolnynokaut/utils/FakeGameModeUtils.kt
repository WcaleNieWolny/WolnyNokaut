package pl.wolny.wolnynokaut.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

object FakeGameModeUtils {
    fun sendFakeGameMode(player: Player, gameMode: GameMode){
        val gameModeFloat: Float = when(gameMode){
            GameMode.SURVIVAL -> 0F
            GameMode.ADVENTURE -> 2F
            GameMode.CREATIVE -> 1F
            GameMode.SPECTATOR -> 3F
        }
        val packet = PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE)
        packet.gameStateIDs.write(0, 3)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }
    fun resetGamemode(player: Player){
        sendFakeGameMode(player, player.gameMode)
    }
}
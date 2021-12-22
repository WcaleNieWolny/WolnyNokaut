package pl.wolny.wolnynokaut.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

fun Player.sendLegacyMessage(string: String) {
    player?.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(string))
}
object ComponentUtils {
    fun format(string: String) : Component = LegacyComponentSerializer.legacyAmpersand().deserialize(string)
}
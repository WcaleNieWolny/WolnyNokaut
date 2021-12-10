package pl.wolny.wolnynokaut.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class ComponentUtils {
    companion object{
        fun format(string: String) : Component = LegacyComponentSerializer.legacyAmpersand().deserialize(string)
    }
}
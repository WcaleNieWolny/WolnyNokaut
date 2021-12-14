package pl.wolny.wolnynokaut.controlers

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController

class PlayerHealthController(
    private val cache: KnockedCache,
    private val plugin: JavaPlugin,
    private val knockedController: KnockedController): Listener {


}
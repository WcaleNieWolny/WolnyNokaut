package pl.wolny.wolnynokaut

import net.dzikoysk.cdn.KCdnFactory
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.source.Source
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.commands.HarakiriCommand
import pl.wolny.wolnynokaut.controlers.PlayerHealthController
import pl.wolny.wolnynokaut.controlers.RescueController
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController
import pl.wolny.wolnynokaut.knocked.KnockedFactory
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.listeners.DamageListener
import pl.wolny.wolnynokaut.listeners.DeathListener
import pl.wolny.wolnynokaut.listeners.SneakListener
import pl.wolny.wolnynokaut.map.MapDataFile
import pl.wolny.wolnynokaut.map.MapFactory
import java.io.File


class WolnyNokaut : JavaPlugin() {
    private lateinit var limboController: LimboController
    private lateinit var mapFactory: MapFactory
    private lateinit var knockedFactory: KnockedFactory
    private lateinit var knockedCache: KnockedCache
    private lateinit var config: NokautConfig
    private lateinit var knockedController: KnockedController
    private lateinit var playerHealthController: PlayerHealthController
    private lateinit var rescueController: RescueController
    override fun onEnable() {
        // Plugin startup logic
        val file = File(this.dataFolder, "map_data.cdn")
        val cdn = KCdnFactory.createYamlLike()
        val mapSource = Source.of(file)
        val mapDataFileResult = cdn.loadAs<MapDataFile>(mapSource)
        if (!mapDataFileResult.isErr) {
            logger.info("Map data file error: ${mapDataFileResult.error}")
            pluginLoader.disablePlugin(this)
            return
        }
        val mapDataFile = mapDataFileResult.get()
        val configFile = File(this.dataFolder, "config.cdn")
        val configSource = Source.of(configFile)
        val configResult = cdn.load(configSource, NokautConfig::class.java)
        if (configResult.isErr) {
            logger.info("Map data file error: ${mapDataFileResult.error}")
            pluginLoader.disablePlugin(this)
            return
        }
        config = configResult.get()
        cdn.render(mapDataFile, Source.of(file))
        cdn.render(config, configSource)
        mapFactory = MapFactory(mapDataFile.mapId, cdn, mapDataFile, file)
        limboController = LimboController(this, mapFactory)
        limboController.init()
        knockedCache = KnockedCache()
        knockedFactory = KnockedFactory(this, limboController, config, knockedCache)
        knockedCache.factory = knockedFactory
        knockedController = knockedFactory.createControler()
        playerHealthController = PlayerHealthController(cache = knockedCache, plugin = this, knockedController = knockedController)
        rescueController = RescueController(
            knockedController, config.healXP, config.resuscitationForHeal1,
            config.resuscitationForHeal2, config.treatmentTime, this, knockedCache)
        registerListeners()
        getCommand("harakiri")?.setExecutor(HarakiriCommand(knockedCache, knockedController, config.notAllowed, config.harakiriDisallow, config.harakiriPermit))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(playerHealthController, this)
        manager.registerEvents(rescueController, this)
    }
}
package pl.wolny.wolnynokaut

import net.dzikoysk.cdn.KCdnFactory
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.source.Source
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.commands.DropPlayerCommand
import pl.wolny.wolnynokaut.commands.HarakiriCommand
import pl.wolny.wolnynokaut.commands.PickUpCommand
import pl.wolny.wolnynokaut.controlers.RescueController
import pl.wolny.wolnynokaut.hook.WorldGuardHook
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController
import pl.wolny.wolnynokaut.knocked.KnockedFactory
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.map.MapDataFile
import pl.wolny.wolnynokaut.map.MapFactory
import pl.wolny.wolnynokaut.transfer.TransferController
import java.io.File
import java.util.logging.Level


class WolnyNokaut : JavaPlugin() {
    private val worldGuardHook = WorldGuardHook()
    private lateinit var limboController: LimboController
    private lateinit var mapFactory: MapFactory
    private lateinit var knockedFactory: KnockedFactory
    private lateinit var knockedCache: KnockedCache
    private lateinit var config: NokautConfig
    private lateinit var knockedController: KnockedController
    private lateinit var rescueController: RescueController
    private lateinit var transferController: TransferController
    override fun onEnable() {
        // Plugin startup logic
        val file = File(this.dataFolder, "map_data.cdn")
        val cdn = KCdnFactory.createYamlLike()
        checkDataFolder()
        val mapSource = Source.of(file)
        val mapDataFileResult = cdn.loadAs<MapDataFile>(mapSource)
        if (mapDataFileResult.isErr) {
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

        worldGuardHook.init()

        knockedCache = KnockedCache()
        mapFactory = MapFactory(mapDataFile.mapId, cdn, mapDataFile, file)
        limboController = LimboController(this, mapFactory, knockedCache)
        limboController.init()
        knockedFactory = KnockedFactory(this, limboController, config, worldGuardHook, knockedCache)
        knockedCache.factory = knockedFactory
        knockedController = knockedFactory.createControler()
        rescueController = RescueController(
            knockedController, config.healXP, config.resuscitationForHeal1,
            config.resuscitationForHeal2, config.treatmentTime, this, knockedCache)
        transferController = TransferController(limboController, knockedCache, knockedController)
        registerListeners()
        getCommand("harakiri")?.setExecutor(HarakiriCommand(knockedCache, knockedController, config.notAllowed, config.harakiriDisallow, config.harakiriPermit))
        getCommand("pickup")?.setExecutor(PickUpCommand(transferController, knockedCache, config.notAllowed, config.noPlayerAsArgument, config.playerOffline, config.playerToFar, config.playerNotKnocked, config.pickedSucessfull))
        getCommand("ground")?.setExecutor(DropPlayerCommand(transferController, knockedCache, config.notAllowed, config.noPlayerToDrop))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(rescueController, this)
        manager.registerEvents(knockedController, this)
    }
    private fun checkDataFolder(){
        val dataFile = this.dataFolder
        try {
            if (!dataFile.exists()) {
                dataFile.mkdir()
            }
        } catch (exception: SecurityException) {
            logger.log(
                Level.SEVERE, """
                Can not create plugin files! No permission! Shutting down!
                Stack Trace: ${exception.printStackTrace()}
            """.trimIndent()
            )
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }
}
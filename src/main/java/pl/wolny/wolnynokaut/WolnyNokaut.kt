package pl.wolny.wolnynokaut

import net.dzikoysk.cdn.CdnFactory
import net.dzikoysk.cdn.source.Source
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pl.wolny.wolnynokaut.commands.HarakiriCmd
import pl.wolny.wolnynokaut.knocked.KnockedCache

import pl.wolny.wolnynokaut.knocked.KnockedFactory
import pl.wolny.wolnynokaut.listeners.DeathListener
import pl.wolny.wolnynokaut.listeners.SneakListener
import pl.wolny.wolnynokaut.map.MapDataFile
import pl.wolny.wolnynokaut.map.MapFactory
import pl.wolny.wolnynokaut.utils.ImageUtils
import pl.wolny.wolnynokaut.utils.LimboUtils
import java.io.File


class WolnyNokaut : JavaPlugin() {
    private lateinit var limboUtils: LimboUtils
    private lateinit var imageUtils: ImageUtils
    private lateinit var mapFactory: MapFactory
    private lateinit var knockedFactory: KnockedFactory
    private lateinit var knockedCache: KnockedCache
    private lateinit var config: NokautConfig
    override fun onEnable() {
        // Plugin startup logic
        imageUtils = ImageUtils()
        val file = File(this.dataFolder.absolutePath + "/map_data.cdn")
        val cdn = CdnFactory.createYamlLike()
        val mapDataFile: MapDataFile = cdn.load(Source.of(file), MapDataFile::class.java)
        config = cdn.load(Source.of(File(this.dataFolder.absolutePath + "/config.cdn")), NokautConfig::class.java)
        cdn.render(config, File(this.dataFolder.absolutePath + "/config.cdn"))
        mapFactory = MapFactory(mapDataFile.mapId, imageUtils, cdn, mapDataFile, file)
        limboUtils = LimboUtils(this, mapFactory)
        knockedCache = KnockedCache()
        knockedFactory = KnockedFactory(this, limboUtils, config, knockedCache)
        knockedCache.factory = knockedFactory
        registerListeners()
        getCommand("harakiri")?.setExecutor(HarakiriCmd(config = config, cache = knockedCache))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
    fun registerListeners(){
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(DeathListener(knockedCache, this), this)
        manager.registerEvents(SneakListener(knockedCache), this)
    }
}
package pl.wolny.wolnynokaut.map

import net.dzikoysk.cdn.Cdn
import net.dzikoysk.cdn.source.Source
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import pl.wolny.wolnynokaut.utils.ImageUtils
import java.io.File


class MapFactory(
    private var id: Int,
    private val cdn: Cdn,
    private val mapDataFile: MapDataFile,
    private val file: File
) {
    private val inputSteam = ImageUtils.loadInputStream("ded.png")
    fun generateMap(player: Player) {
        val map = generateMapItem(player)
        player.inventory.setItem(0, map)
        player.sendMap((map.itemMeta as MapMeta).mapView!!)
    }
    fun generateMapItem(player: Player): ItemStack {
        val map: MapView = if (id == -1) {
            Bukkit.createMap(player.world)
        } else {
            Bukkit.getMap(id)!!
        }
        if (id == -1) {
            id = map.id
            mapDataFile.mapId = id
            cdn.render(mapDataFile, Source.of(file))
        }
        map.scale = MapView.Scale.CLOSEST
        map.renderers.clear()
        map.addRenderer(CustomMapRender(inputSteam))
        val i = ItemStack(Material.FILLED_MAP, 1)
        val meta: MapMeta = i.itemMeta as MapMeta
        meta.mapView = map
        i.itemMeta = meta
        return i
    }
}
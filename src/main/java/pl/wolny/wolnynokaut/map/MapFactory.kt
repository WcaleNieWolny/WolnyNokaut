package pl.wolny.wolnynokaut.map

import net.dzikoysk.cdn.Cdn
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import pl.wolny.wolnynokaut.utils.ImageUtils
import java.io.File


class MapFactory(private var id: Int, private val imageUtils: ImageUtils, private val cdn: Cdn, private val mapDataFile: MapDataFile, private val file: File) {
    private val inputSteam = imageUtils.loadInputStream("ded.png")
    fun generateMap(world: World, player: Player){
        val map: MapView = if(id == -1){
            Bukkit.createMap(world)
        }else{ Bukkit.getMap(id)!!}
        if(id == -1){
            id = map.id
            mapDataFile.mapId = id
            cdn.render(mapDataFile, file)
        }
        map.scale = MapView.Scale.CLOSEST
        map.renderers.clear()
        map.addRenderer(CustomMapRender(imageUtils, inputSteam))
        val i = ItemStack(Material.FILLED_MAP, 1)
        val meta: MapMeta = i.itemMeta as MapMeta
        meta.mapView = map
        i.itemMeta = meta
        player.inventory.setItem(4, i)
        player.sendMap(map)
    }
}
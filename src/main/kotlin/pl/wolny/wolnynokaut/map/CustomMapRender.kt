package pl.wolny.wolnynokaut.map

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import pl.wolny.wolnynokaut.utils.ImageUtils
import java.io.InputStream


class CustomMapRender(private val inputStream: InputStream) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        if (inputStream == null) {
            return
        }
        val image = ImageUtils.loadImage(inputStream) ?: return
        canvas.drawImage(0, 0, image)
    }
}
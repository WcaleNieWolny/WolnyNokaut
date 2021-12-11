package pl.wolny.wolnynokaut.utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO


object ImageUtils {
    fun loadInputStream(fileName: String) : InputStream{
        return javaClass.classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("file not found! $fileName")
    }
    fun loadImage(inputStream: InputStream): BufferedImage{
        return ImageIO.read(inputStream)
    }
}
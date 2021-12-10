package pl.wolny.wolnynokaut.utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO


class ImageUtils {
    private val classLoader: ClassLoader = javaClass.classLoader
    fun loadInputStream(fileName: String) : InputStream{
        return classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("file not found! $fileName")
    }
    fun loadImage(inputStream: InputStream): BufferedImage{
        return ImageIO.read(inputStream)
    }
}
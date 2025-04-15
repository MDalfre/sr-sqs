package commons

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import javax.imageio.ImageIO

class ImageHelper {
    fun loadImageBitmap(resourcePath: String): ImageBitmap {
        this.javaClass.classLoader.getResourceAsStream(resourcePath)?.let { inputStream ->
            return ImageIO.read(inputStream).toComposeImageBitmap()
        } ?: throw IllegalArgumentException("Resource not found: $resourcePath")
    }
}
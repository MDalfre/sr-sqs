package commons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource

@Suppress("MagicNumber")
object Util {

    fun Float.toPercentage() = (this * 100).toInt()

    @Composable
    fun appIcon() = remember { BitmapPainter(ImageHelper().loadImageBitmap("sr-sqs-icon.png")) }

    @Composable
    fun appLogo() = remember { BitmapPainter(ImageHelper().loadImageBitmap("logo2.png")) }
}

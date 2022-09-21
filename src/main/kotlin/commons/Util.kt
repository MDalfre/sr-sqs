package commons

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Suppress("MagicNumber")
object Util {

    fun Float.toPercentage() = (this * 100).toInt()

    @Composable
    fun appIcon() = painterResource("sr-sqs-icon.png")

    @Composable
    fun appLogo() = painterResource("logo2.png")
}

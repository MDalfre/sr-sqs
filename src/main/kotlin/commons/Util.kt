package commons

@Suppress("MagicNumber")
object Util {

    fun Float.toPercentage() = (this * 100).toInt()
}

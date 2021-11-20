package logger

import java.lang.StringBuilder
import kotlin.math.roundToInt

class ProgressIndicatorLogger(private val totalCount: Int) {
    fun log(currentCount: Int) {
        val requestBufferSeconds = 2
        val requestCountPerOneBookBuild = 2
        val bookCountsPerReadBooksPage = 20
        val estimatedTime = totalCount * requestBufferSeconds * requestCountPerOneBookBuild + (totalCount / bookCountsPerReadBooksPage).toDouble().roundToInt()
        val text = StringBuilder().apply {
            append("\r")
            append("Estimated: $estimatedTime Sec, Progress: $currentCount/$totalCount")
        }
        print(text)
    }

    fun clean() = print("\r")
}

package logger

import java.lang.StringBuilder
import kotlin.math.roundToInt

class ProgressIndicatorLogger(private val totalCount: Int) {
    fun log(currentCount: Int) {
        val averageSecondsForOneRequestBuffer = 3
        val averageRequestCountPerOneBookBuild = 2
        val bookCountsPerReadBooksPage = 20
        val estimatedTime = totalCount * averageSecondsForOneRequestBuffer * averageRequestCountPerOneBookBuild + (totalCount / bookCountsPerReadBooksPage).toDouble().roundToInt()
        val text = StringBuilder().apply {
            append("\r")
            append("Estimated: ${estimatedTime}sec, Progress: $currentCount/$totalCount")
        }
        print(text)
    }

    fun clean() = print("\r")
}

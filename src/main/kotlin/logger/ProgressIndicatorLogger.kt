package logger

import java.lang.StringBuilder
import kotlin.math.roundToInt

class ProgressIndicatorLogger(private val totalCount: Int) {
    private val estimatedTime = totalCount * 2 + (totalCount / 20).toDouble().roundToInt()

    fun log(currentCount: Int) {
        val text = StringBuilder().apply {
            append("\r")
            append("Estimated: $estimatedTime Sec, Progress: $currentCount/$totalCount")
        }
        print(text)
    }

    fun clean() = print("\r")
}

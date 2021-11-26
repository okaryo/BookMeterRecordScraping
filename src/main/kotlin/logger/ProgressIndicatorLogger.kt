package logger

class ProgressIndicatorLogger(private val startTime: Long, private val totalCount: Int) {
    fun log(currentCount: Int) {
        val text = "[Info] ${formattedElapsedTime()} elapsed, $currentCount/$totalCount completed."
        println(text)
    }

    fun finish(currentCount: Int) {
        if (currentCount == 0) {
            println("[Info] There are no newly added BookMeter records.")
        } else if (currentCount != totalCount) {
            println("[Info] New $currentCount BookMeter records added!")
        }
        println("[Info] Task completed, took ${formattedElapsedTime()}")
    }

    private fun formattedElapsedTime(): String {
        val currentTime = System.currentTimeMillis()
        val elapsedSeconds = (currentTime - startTime) / 1000
        val elapsedMinutes = elapsedSeconds / 60
        val minutesString = if (elapsedMinutes > 0) "${elapsedMinutes}m" else ""
        val secondsString = "${(elapsedSeconds / 1000) % 60}s"
        return "$minutesString$secondsString"
    }
}

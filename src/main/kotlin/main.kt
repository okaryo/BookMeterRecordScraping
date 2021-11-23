import builder.ReadingRecordListBuilder
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import repository.PageRepository
import webdriver.WebDriver
import java.io.File

@DelicateCoroutinesApi
suspend fun main(args: Array<String>) {
    val userId = args.first().toInt()
    val webDriver = WebDriver()
    val fetchPagesService = PageRepository(userId, webDriver)
    val readingRecordList = ReadingRecordListBuilder(fetchPagesService).execute()
    val recordListJson = Json.encodeToString(readingRecordList)
    val formattedJson = JSONObject(recordListJson).toString(4)

    if (args.size >= 2) {
        val fileName = args[1]
        val file = File("./generated/$fileName")
        file.writeText(formattedJson)
    } else {
        println(formattedJson)
    }

    webDriver.close()
}

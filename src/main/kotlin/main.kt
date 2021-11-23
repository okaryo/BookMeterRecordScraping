import builder.ReadingRecordListBuilder
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ReadingRecordList
import org.json.JSONObject
import repository.PageRepository
import webdriver.WebDriver
import java.io.File
import java.io.InvalidObjectException
import java.security.InvalidParameterException

@DelicateCoroutinesApi
suspend fun main(args: Array<String>) {
    val userId = args.first().toInt()
    val webDriver = WebDriver()
    val fetchPagesService = PageRepository(userId, webDriver)
    val builder = ReadingRecordListBuilder(fetchPagesService)

    val readingRecordList = if (args.size >= 3) {
        if (args[2] != "only-diff") throw InvalidParameterException("Invalid Arguments!")

        val jsonString = File("./generated/main.json").readText()
        if (jsonString.isBlank()) throw InvalidObjectException("Json File is Empty!")

        val existingReadingRecords = ReadingRecordList.fromString(jsonString)
        builder.execute(existingReadingRecords)
    } else {
        builder.execute()
    }
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

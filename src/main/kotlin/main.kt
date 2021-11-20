import builder.ReadingRecordListBuilder
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import repository.PageRepository

@DelicateCoroutinesApi
suspend fun main(args: Array<String>) {
    val userId = args.first().toInt()
    val fetchPagesService = PageRepository(userId)
    val readingRecordList = ReadingRecordListBuilder(fetchPagesService).execute()
    val recordListJson = Json.encodeToString(readingRecordList)
    println(JSONObject(recordListJson).toString(4))
}

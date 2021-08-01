import kotlinx.coroutines.*
import model.Author
import model.Book
import model.Record
import model.RecordList
import org.jsoup.Jsoup
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.jsoup.nodes.Document
import java.lang.StringBuilder

@DelicateCoroutinesApi
fun main(args: Array<String>) {
    val userId = args.first().toInt()
    // TODO: スクレイピング中に表示するメッセージを考える
    val bookMeterHTMLScraping = BookMeterHTMLScraping(userId)
    var paginationPage = 1
    var totalPages = 0
    val records = mutableListOf<Record>()
    var document = bookMeterHTMLScraping.getHTMLDocument(paginationPage)
    while (document.select(".group__book").isNotEmpty()) {
        GlobalScope.launch(Dispatchers.IO) {
            val buildAndAddRecords = async {
                document.select(".group__book").map { element ->
                    val dateString = element.select(".detail__date").first()!!.text()
                    val titleElement = element.select(".detail__title").first()!!
                    val title = titleElement.select("a").first()!!.text()
                    val authorElement = element.select(".detail__authors").first()!!
                    val author = authorElement.select("a").first()!!.text()
                    val bookPage = element.select(".detail__page").first()!!.text().toInt()
                    totalPages += bookPage
                    val book = Book(title, Author(author), bookPage)
                    val date = if (dateString == "日付不明") {
                        null
                    } else {
                        val splitDate = dateString.split("/").map(String::toInt)
                        LocalDate(splitDate[0], splitDate[1], splitDate[2])
                    }
                    Record(book = book, date = date)
                }.let { records.addAll(it) }
            }
            val waitOneSecondForScrapingManners = async { delay(1000) }
            buildAndAddRecords.await()
            waitOneSecondForScrapingManners.await()
        }
        // TODO: タイトル修正の際にリファクタ
        val string = StringBuilder()
        string.apply {
            append("\r")
            append("$paginationPage %")
        }
        print(string)
        paginationPage++
        document = bookMeterHTMLScraping.getHTMLDocument(paginationPage)
    }
    print("\r")
    val recordList = RecordList(recordsCount = records.size, totalPages = totalPages, records = records)
    val recordListJson = Json.encodeToString(recordList)
    println(JSONObject(recordListJson).toString(4))
}

// TODO: 後でリファクタ
class BookMeterHTMLScraping(private val userId: Int) {
    fun getHTMLDocument(paginationPage: Int): Document {
        return Jsoup.connect("https://bookmeter.com/users/$userId/books/read?page=$paginationPage").get()
    }
}

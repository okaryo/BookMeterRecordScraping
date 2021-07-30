import kotlinx.coroutines.*
import model.Author
import model.Book
import model.Record
import model.RecordList
import org.jsoup.Jsoup
import java.time.LocalDate

suspend fun main(args: Array<String>) {
    val userId = args.first().toInt()
    // TODO: スクレイピング中に表示するメッセージを考える
    withContext(Dispatchers.IO) {
        var page = 1
        var totalPages = 0
        val records = mutableListOf<Record>()
        while (true) {
            val document = Jsoup.connect("https://bookmeter.com/users/$userId/books/read?page=$page").get()
            if (document.select(".group__book").isNullOrEmpty()) break

            val parseDocument = async {
                document.select(".group__book").map { element ->
                    val dateString = element.select(".detail__date").first()!!.text()
                    val titleElement = element.select(".detail__title").first()!!
                    val title = titleElement.select("a").first()!!.text()
                    val authorElement = element.select(".detail__authors").first()!!
                    val author = authorElement.select("a").first()!!.text()
                    val page = element.select(".detail__page").first()!!.text().toInt()
                    totalPages += page
                    val book = Book(title, Author(author), page)
                    val date = if (dateString == "日付不明") {
                        null
                    } else {
                        val splitDate = dateString.split("/").map(String::toInt)
                        LocalDate.of(splitDate[0], splitDate[1], splitDate[2])
                    }
                    Record(book = book, date = date)
                }.let { records.addAll(it) }
                page++
            }
            val waitOneSecondForScrapingManners = async { delay(1000) }
            parseDocument.await()
            waitOneSecondForScrapingManners.await()
        }
        println(RecordList(recordsCount = records.size, totalPages = totalPages, records = records))
    }
}

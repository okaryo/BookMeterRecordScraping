import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Author
import model.Book
import model.Record
import model.RecordList
import org.jsoup.Jsoup
import java.time.LocalDate

suspend fun main(args: Array<String>) {
    val userId = args.first().toInt()
    // TODO: スクレイピング中に表示するメッセージを考える
    // TODO: スクレイピング時のマナーをちゃんとする
    withContext(Dispatchers.IO) {
        val document = Jsoup.connect("https://bookmeter.com/users/$userId/books/read?page=1").get()
        var totalPages = 0
        val records = document.select(".group__book").map { element ->
            val dateString = element.select(".detail__date").first()!!.text()
            val titleElement = element.select(".detail__title").first()!!
            val title = titleElement.select("a").first()!!.text()
            val authorElement = element.select(".detail__authors").first()!!
            val author = authorElement.select("a").first()!!.text()
            val page = element.select(".detail__page").first()!!.text().toInt()
            totalPages += page
            val book = Book(title, Author(author), page)
            val splitDate = dateString.split("/").map(String::toInt)
            Record(book = book, date = LocalDate.of(splitDate[0], splitDate[1], splitDate[2]))
        }
        println(RecordList(recordsCount = records.size, totalPages = totalPages, records = records))
    }
}

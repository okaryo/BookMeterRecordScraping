import model.Author
import model.Book
import org.jsoup.Jsoup

fun main() {
    println("Hello World!")
    // TODO: coroutine使う
    // TODO: スクレイピング中に表示するメッセージを考える
    // TODO: consoleからの使用を考えているので、外部からユーザーIDを受け付けるようにする
    // TODO: スクレイピング時のマナーをちゃんとする
//    val document = Jsoup.connect("https://bookmeter.com/users/739784/books/read").get()
//    val list = mutableListOf<Book>()
//    document.select(".book__detail").map { element ->
//        val date = element.select(".detail__date").first()!!.text()
//        val titleElement = element.select(".detail__title").first()!!
//        val title = titleElement.select("a").first()!!.text()
//        val authorElement = element.select(".detail__authors").first()!!
//        val author = authorElement.select("a").first()!!.text()
//        val book = Book(title, Author(author))
//        list.add(book)
//    }
//    println(list)
}

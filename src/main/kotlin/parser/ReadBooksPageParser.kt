package parser

import kotlinx.datetime.LocalDate
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class ReadBooksPageParser(private val document: Document ) {
    fun getBookGroupElements(): Elements = document.select(".group__book")

    fun getTotalReadBooksCount(): Int {
        val userDataElement = document.select(".userdata-nav").first()!!
        val readBooksCountText = userDataElement.select(".userdata-nav__count").first()!!.text()
        return readBooksCountText.removeSuffix("冊").toInt()
    }

    fun getAuthorName(bookElement: Element): String {
        val authorElement = bookElement.select(".detail__authors").first()!!
        return authorElement.select("a").first()!!.text()
    }

    fun getBookPage(bookElement: Element): Int {
        return bookElement.select(".detail__page").first()!!.text().toInt()
    }

    fun getBookId(bookElement: Element): Int {
        val bookThumbnailElement = bookElement.select(".thumbnail__cover").first()!!
        val bookDetailPageUrl = bookThumbnailElement.select("a").first()!!.attr("href")
        return bookDetailPageUrl.removePrefix("/books/").toInt()
    }

    fun getDate(bookElement: Element): LocalDate? {
        val dateString = bookElement.select(".detail__date").first()!!.text()
        return if (dateString == "日付不明") {
            null
        } else {
            val splitDate = dateString.split("/").map(String::toInt)
            LocalDate(splitDate[0], splitDate[1], splitDate[2])
        }
    }

    fun getReviewId(bookElement: Element): Int {
        val reviewIconElement = bookElement.select(".icon__review").first()!!
        val reviewPageUrl = reviewIconElement.attr("href")
        return reviewPageUrl.removePrefix("/reviews/").toInt()
    }

    fun hasReview(bookElement: Element): Boolean {
        return bookElement.select(".icon__review").isNotEmpty()
    }

    fun existsBooks(): Boolean {
        return getBookGroupElements().isNotEmpty()
    }
}

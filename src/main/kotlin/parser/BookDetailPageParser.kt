package parser

import org.jsoup.nodes.Document

class BookDetailPageParser(private val document: Document) {
    fun getBookTitle(): String {
        return document.select(".inner__title").first()!!.text()
    }

    fun getBookUrl(): String {
        val bookImageElement = document.select(".group__image").first()!!
        return bookImageElement.select("a").first()!!.attr("abs:href")
    }
}

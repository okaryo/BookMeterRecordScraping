package parser

import org.jsoup.nodes.Document

class ReviewPageParser(private val document: Document) {
    fun getReview(): String {
        val reviewTextElement = document.select(".frame__content__text").first()!!
        return reviewTextElement.select("span").first()!!.text()
    }
}

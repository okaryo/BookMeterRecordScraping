package repository

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.system.exitProcess

class PageRepository(private val userId: Int) {
    companion object {
        const val baseUrl = "https://bookmeter.com"
        const val maxRetry = 20
    }

    fun getReadBooksPageDocument(paginationPage: Int): Document {
        val readBooksPageUrlByPagination = "$baseUrl/users/$userId/books/read?page=$paginationPage"
        return getDocumentBy(readBooksPageUrlByPagination)
    }

    fun getBookDetailPageDocument(bookId: Int): Document {
        val bookDetailPageUrl = "$baseUrl/books/$bookId"
        return getDocumentBy(bookDetailPageUrl)
    }

    fun getReviewPageDocument(reviewId: Int): Document {
        val reviewPageUrl = "$baseUrl/reviews/$reviewId"
        return getDocumentBy(reviewPageUrl)
    }

    private fun getDocumentBy(url: String): Document {
        var retryCount = 0
        var document: Document? = null

        while (retryCount < maxRetry) {
            if (document != null) break

            try {
                Thread.sleep(awaitingTimeBetweenRequest(retryCount))
                Jsoup.connect(url).timeout(10000).get().let {
                    document = it
                }
                retryCount = 0
            } catch (e: Exception) {
                retryCount++
                if (retryCount == maxRetry) {
                    print("\r")
                    println("Failed $maxRetry times to fetch the Review page!")
                    exitProcess(1)
                }
            }
        }
        return document!!
    }

    private fun awaitingTimeBetweenRequest(retryCount: Int): Long {
        return 1000 * (retryCount + 1).toLong()
    }
}

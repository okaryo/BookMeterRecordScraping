package repository

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class PageRepository(private val userId: Int) {
    companion object {
        const val baseUrl = "https://bookmeter.com"
        const val maxRetry = 5
    }

    fun getReadBooksPageDocument(paginationPage: Int): Document {
        var retryCount = 0
        var document: Document? = null
        val readBooksPageUrl = "$baseUrl/users/$userId/books/read"

        while (retryCount < maxRetry) {
            if (document != null) break

            try {
                Thread.sleep(1000)
                Jsoup.connect("$readBooksPageUrl?page=$paginationPage").timeout(10000).get().let {
                    document = it
                }
                retryCount = 0
            } catch (e: Exception) {
                retryCount++
                if (retryCount == 5) throw e
            }
        }
        return document!!
    }

    fun getBookDetailPageDocument(bookId: Int): Document {
        var retryCount = 0
        var document: Document? = null
        val bookDetailPageUrl = "$baseUrl/books/$bookId"

        while (retryCount < maxRetry) {
            if (document != null) break

            try {
                Thread.sleep(1000)
                Jsoup.connect(bookDetailPageUrl).timeout(10000).get().let {
                    document = it
                }
                retryCount = 0
            } catch (e: Exception) {
                retryCount++
                if (retryCount == 5) throw e
            }
        }
        return document!!
    }

    fun getReviewPageDocument(reviewId: Int): Document {
        var retryCount = 0
        var document: Document? = null
        val reviewPageUrl = "$baseUrl/reviews/$reviewId"

        while (retryCount < maxRetry) {
            if (document != null) break

            try {
                Thread.sleep(1000)
                Jsoup.connect(reviewPageUrl).timeout(10000).get().let {
                    document = it
                }
                retryCount = 0
            } catch (e: Exception) {
                retryCount++
                if (retryCount == 5) throw e
            }
        }
        return document!!
    }
}

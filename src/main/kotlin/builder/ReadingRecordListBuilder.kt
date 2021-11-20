package builder

import kotlinx.coroutines.*
import logger.ProgressIndicatorLogger
import model.Author
import model.Book
import model.ReadingRecord
import model.ReadingRecordList
import parser.BookDetailPageParser
import parser.ReadBooksPageParser
import parser.ReviewPageParser
import repository.*

class ReadingRecordListBuilder(private val repository: PageRepository) {
    @DelicateCoroutinesApi
    suspend fun execute(): ReadingRecordList {
        var paginationPage = 1
        var document = repository.getReadBooksPageDocument(paginationPage)
        var parseReadBooksPageService = ReadBooksPageParser(document)
        val totalReadBooksCount = parseReadBooksPageService.getTotalReadBooksCount()
        val progressIndicatorLogger = ProgressIndicatorLogger(totalReadBooksCount)
        val records = mutableListOf<ReadingRecord>()
        progressIndicatorLogger.log(0)

        while (parseReadBooksPageService.existsBooks()) {
            GlobalScope.launch(Dispatchers.IO) {
                launch  {
                    val readingRecords = buildReadingRecordsInPage(parseReadBooksPageService)
                    records.addAll(readingRecords)
                }
            }.join()
            progressIndicatorLogger.log(records.size)
            paginationPage++
            document = repository.getReadBooksPageDocument(paginationPage)
            parseReadBooksPageService = ReadBooksPageParser(document)
        }
        progressIndicatorLogger.clean()
        val totalPages = records.sumOf { record -> record.book.page }
        return ReadingRecordList(recordsCount = records.size, totalPages = totalPages, records = records)
    }

    private fun buildReadingRecordsInPage(parseReadBooksPageService: ReadBooksPageParser): List<ReadingRecord> {
        return parseReadBooksPageService.getBookGroupElements().map { element ->
            val date = parseReadBooksPageService.getDate(element)
            val authorName = parseReadBooksPageService.getAuthorName(element)
            val bookPage= parseReadBooksPageService.getBookPage(element)
            val bookId = parseReadBooksPageService.getBookId(element)
            val parseBookDetailPageService = runBlocking {
                val document = repository.getBookDetailPageDocument(bookId)
                BookDetailPageParser(document)
            }
            val title = parseBookDetailPageService.getBookTitle()
            val bookUrl = parseBookDetailPageService.getBookUrl()
            val review = if (parseReadBooksPageService.hasReview(element)) {
                val reviewId = parseReadBooksPageService.getReviewId(element)
                val parseReviewPageService = runBlocking {
                    val document = repository.getReviewPageDocument(reviewId)
                    ReviewPageParser(document)
                }
                parseReviewPageService.getReview()
            } else {
                ""
            }
            val book = Book(title, Author(authorName), bookPage, url = bookUrl)
            ReadingRecord(book = book, date = date, review = review)
        }
    }
}

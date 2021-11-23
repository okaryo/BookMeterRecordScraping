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
        var readBooksPageParser = ReadBooksPageParser(document)
        val totalReadBooksCount = readBooksPageParser.getTotalReadBooksCount()
        val progressIndicatorLogger = ProgressIndicatorLogger(totalReadBooksCount)
        val records = mutableListOf<ReadingRecord>()
        progressIndicatorLogger.log(0)

        while (readBooksPageParser.existsBooks()) {
            GlobalScope.launch(Dispatchers.IO) {
                launch  {
                    val readingRecords = buildReadingRecordsInPage(readBooksPageParser)
                    records.addAll(readingRecords)
                }
            }.join()
            progressIndicatorLogger.log(records.size)
            paginationPage++
            document = repository.getReadBooksPageDocument(paginationPage)
            readBooksPageParser = ReadBooksPageParser(document)
            break
        }
        progressIndicatorLogger.clean()
        val totalPages = records.sumOf { record -> record.book.page }
        return ReadingRecordList(recordsCount = records.size, totalPages = totalPages, records = records)
    }

    private fun buildReadingRecordsInPage(readBooksPageParser: ReadBooksPageParser): List<ReadingRecord> {
        return readBooksPageParser.getBookGroupElements().map { element ->
            val date = readBooksPageParser.getDate(element)
            val authorName = readBooksPageParser.getAuthorName(element)
            val bookPage= readBooksPageParser.getBookPage(element)
            val bookId = readBooksPageParser.getBookId(element)
            val bookDetailPageParser = runBlocking {
                val document = repository.getBookDetailPageDocument(bookId)
                BookDetailPageParser(document)
            }
            val title = bookDetailPageParser.getBookTitle()
            val bookUrl = bookDetailPageParser.getBookUrl()
            val review = if (readBooksPageParser.hasReview(element)) {
                val reviewId = readBooksPageParser.getReviewId(element)
                val reviewPageParser = runBlocking {
                    val document = repository.getReviewPageDocument(reviewId)
                    ReviewPageParser(document)
                }
                reviewPageParser.getReview()
            } else {
                ""
            }
            val book = Book(title, Author(authorName), bookPage, url = bookUrl)
            ReadingRecord(book = book, date = date, review = review)
        }
    }
}

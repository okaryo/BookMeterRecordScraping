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
        val progressIndicatorLogger = ProgressIndicatorLogger(System.currentTimeMillis(), totalReadBooksCount)
        var records = ReadingRecordList(0, 0, listOf())
        progressIndicatorLogger.log(0)

        while (readBooksPageParser.existsBooks()) {
            GlobalScope.launch(Dispatchers.IO) {
                launch  {
                    val readingRecords = buildReadingRecords(readBooksPageParser)
                    records = records.add(readingRecords)
                }
            }.join()
            progressIndicatorLogger.log(records.size())
            paginationPage++
            document = repository.getReadBooksPageDocument(paginationPage)
            readBooksPageParser = ReadBooksPageParser(document)
        }
        progressIndicatorLogger.finish(records.size())
        return records
    }

    @DelicateCoroutinesApi
    suspend fun execute(existingRecords: ReadingRecordList): ReadingRecordList {
        var paginationPage = 1
        var document = repository.getReadBooksPageDocument(paginationPage)
        var readBooksPageParser = ReadBooksPageParser(document)
        val totalReadBooksCount = readBooksPageParser.getTotalReadBooksCount()
        val progressIndicatorLogger = ProgressIndicatorLogger(System.currentTimeMillis(), totalReadBooksCount)
        var notYetSavedRecords = ReadingRecordList(0, 0, listOf())
        progressIndicatorLogger.log(0)

        var hasSameRecord = false
        while (readBooksPageParser.existsBooks()) {
            GlobalScope.launch(Dispatchers.IO) {
                launch  {
                    val newRecords = buildReadingRecords(readBooksPageParser).filter {
                        if (existingRecords.hasSameRecord(it)) {
                            hasSameRecord = true
                            false
                        } else {
                            true
                        }
                    }
                    notYetSavedRecords = notYetSavedRecords.add(newRecords)
                }
            }.join()
            progressIndicatorLogger.log(notYetSavedRecords.size())
            if (hasSameRecord) break

            paginationPage++
            document = repository.getReadBooksPageDocument(paginationPage)
            readBooksPageParser = ReadBooksPageParser(document)
        }
        progressIndicatorLogger.finish(notYetSavedRecords.size())
        return existingRecords.prepend(notYetSavedRecords)
    }

    private fun buildReadingRecords(readBooksPageParser: ReadBooksPageParser): List<ReadingRecord> {
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
            val format = if (review.contains("Audibleにて。")) {
                "Audible"
            } else if (review.contains("Kindleにて。")) {
                "Kindle"
            } else if (review.contains("電子書籍にて。")) {
                "Ebook"
            } else {
                "Paper"
            }
            val isRereading = review.contains("再読。")
            val book = Book(title, Author(authorName), bookPage, url = bookUrl)
            ReadingRecord(book = book, date = date, review = review, format = format, isRereading = isRereading)
        }
    }
}

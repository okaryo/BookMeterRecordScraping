package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class ReadingRecordList(
    val recordsCount: Int,
    val totalPages: Int,
    val records: List<ReadingRecord>,
) {
    companion object {
        fun fromString(string: String): ReadingRecordList {
            return Json.decodeFromString(string)
        }
    }

    fun size(): Int = this.records.size

    fun add(records: List<ReadingRecord>): ReadingRecordList {
        val newTotalPages = this.totalPages + records.sumOf { record -> record.book.page }
        val newRecordsCount = this.recordsCount + records.size
        val newRecords = this.records + records
        return ReadingRecordList(newTotalPages, newRecordsCount, newRecords)
    }

    fun prepend(records: ReadingRecordList): ReadingRecordList {
        val newTotalPages = this.totalPages + records.totalPages
        val newRecordsCount = this.recordsCount + records.size()
        val newRecords = this.records + records.records
        return ReadingRecordList(newTotalPages, newRecordsCount, newRecords)
    }

    fun hasSameRecord(record: ReadingRecord): Boolean {
        return this.records.contains(record)
    }
}

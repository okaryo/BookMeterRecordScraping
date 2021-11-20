package model

import kotlinx.serialization.Serializable

@Serializable
data class ReadingRecordList(
    val recordsCount: Int,
    val totalPages: Int,
    val records: List<ReadingRecord>,
)

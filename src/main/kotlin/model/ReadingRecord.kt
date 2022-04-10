package model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class ReadingRecord(
    val book: Book,
    val review: String?,
    val date: LocalDate?,
    val format: String,
    val isRereading: Boolean,
)

package model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class Record(
    val book: Book,
    val date: LocalDate?,
)

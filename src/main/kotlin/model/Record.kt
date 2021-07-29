package model

import java.time.LocalDate

data class Record(
    val book: Book,
    val thoughts: String?,
    val date: LocalDate,
)

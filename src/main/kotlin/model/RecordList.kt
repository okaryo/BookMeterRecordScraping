package model

import kotlinx.serialization.Serializable

@Serializable
data class RecordList(
    val recordsCount: Int,
    val totalPages: Int,
    val records: List<Record>,
)

package model

data class RecordList(
    val recordsCount: Int,
    val totalPages: Int,
    val records: List<Record>,
)

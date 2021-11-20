package model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val title: String,
    val author: Author,
    val page: Int,
    val url: String?,
)

package com.august.spiritscribe.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Whiskey(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val distillery: String,
    val type: WhiskeyType,
    val age: Int?,
    val year: Int?,
    val abv: Double,
    val price: Double?,
    val region: String?,
    val description: String,
    val rating: Int?,
    val imageUris: List<String>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class WhiskeyType {
    BOURBON,
    SCOTCH,
    IRISH,
    JAPANESE,
    RYE,
    CANADIAN,
    OTHER
}

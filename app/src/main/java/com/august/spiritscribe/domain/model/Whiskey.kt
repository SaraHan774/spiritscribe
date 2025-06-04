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

data class FlavorProfile(
    val id: String = UUID.randomUUID().toString(),
    val whiskeyId: String,
    val sweetness: Int, // 1-5
    val smokiness: Int, // 1-5
    val spiciness: Int, // 1-5
    val fruitiness: Int, // 1-5
    val woodiness: Int, // 1-5
    val notes: List<String> // e.g. ["vanilla", "caramel", "oak"]
) 
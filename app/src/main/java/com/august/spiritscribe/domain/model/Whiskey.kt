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
    BLENDED,           // JSON에서 "Blended" 타입
    BLENDEDMALT,       // JSON에서 "BlendedMalt" 타입  
    IRISHPOTSTILL,     // JSON에서 "IrishPotStill" 타입
    SINGLEMALT,        // JSON에서 "SingleMalt" 타입
    SINGLEGRAIN,       // JSON에서 "SingleGrain" 타입
    TAIWANESE,         // JSON에서 "Taiwanese" 타입
    OTHER;

    companion object {
        // JSON 문자열을 WhiskeyType enum으로 변환하는 헬퍼 함수
        fun fromString(typeString: String): WhiskeyType {
            return when (typeString.uppercase()) {
                "BOURBON" -> BOURBON
                "SCOTCH" -> SCOTCH
                "IRISH" -> IRISH
                "JAPANESE" -> JAPANESE
                "RYE" -> RYE
                "CANADIAN" -> CANADIAN
                "BLENDED" -> BLENDED
                "BLENDEDMALT" -> BLENDEDMALT
                "IRISHPOTSTILL" -> IRISHPOTSTILL
                "SINGLEMALT" -> SINGLEMALT
                "SINGLEGRAIN" -> SINGLEGRAIN
                "TAIWANESE" -> TAIWANESE
                else -> OTHER
            }
        }
    }
}

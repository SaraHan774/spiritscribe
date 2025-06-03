package com.august.spiritscribe.domain.model

import java.util.UUID

/**
 * Represents a whiskey tasting note with detailed information about the whiskey.
 */
data class WhiskeyNote(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val distillery: String,
    val origin: String,
    val type: String,
    val age: Int?,
    val year: Int?,
    val abv: Double,
    val price: Double?,
    val sampled: Boolean,
    val color: ColorMeter,
    val flavors: List<FlavorProfile>,
    val additionalNotes: String,
    val finalRating: FinalRating,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String? = null
)

data class FlavorProfile(
    val flavor: Flavor,
    val intensity: Int // Scale from 0 to 5
)

enum class Flavor {
    SWEET, SPICY, SMOKY, FRUITY, WOODY, PEATY, COFFEE, MINTY, 
    NUTTY, HERBAL, FLORAL, DARK_FRUIT, CITRUS, VANILLA
}

data class ColorMeter(
    val hue: String,
    val intensity: Int // Scale from 0 to 5
)

data class FinalRating(
    val appearance: Int, // Scale from 0 to 100
    val nose: Int,      // Scale from 0 to 100
    val taste: Int,     // Scale from 0 to 100
    val finish: Int,    // Scale from 0 to 100
    val overall: Int    // Scale from 0 to 100
) {
    val averageScore: Int
        get() = (appearance + nose + taste + finish + overall) / 5
} 
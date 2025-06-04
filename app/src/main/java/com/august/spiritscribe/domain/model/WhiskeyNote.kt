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
    MALT {      // Grainy, cereal notes
        override val emoji = "🌾"
        override val displayName = "$emoji Malt"
    },
    FRUIT {     // Fresh fruits
        override val emoji = "🍎"
        override val displayName = "$emoji Fruit"
    },
    DRIED {     // Dried fruits, raisins
        override val emoji = "🍇"
        override val displayName = "$emoji Dried"
    },
    FLORAL {    // Flowers, botanical
        override val emoji = "🌸"
        override val displayName = "$emoji Floral"
    },
    CITRUS {    // Orange, lemon
        override val emoji = "🍊"
        override val displayName = "$emoji Citrus"
    },
    SPICE {     // Pepper, cinnamon
        override val emoji = "🌶️"
        override val displayName = "$emoji Spice"
    },
    WOOD {      // Oak, cedar
        override val emoji = "🪵"
        override val displayName = "$emoji Wood"
    },
    PEAT {      // Smoky, earthy
        override val emoji = "💨"
        override val displayName = "$emoji Peat"
    },
    NUTS {      // Almond, walnut
        override val emoji = "🥜"
        override val displayName = "$emoji Nuts"
    },
    TOFFEE {    // Caramel, butterscotch
        override val emoji = "🍯"
        override val displayName = "$emoji Toffee"
    },
    VANILLA {   // Vanilla, cream
        override val emoji = "🍶"
        override val displayName = "$emoji Vanilla"
    },
    HONEY {     // Sweet, nectar
        override val emoji = "🍯"
        override val displayName = "$emoji Honey"
    },
    HERB {      // Fresh herbs
        override val emoji = "🌿"
        override val displayName = "$emoji Herb"
    },
    CHAR {      // Charred wood, tobacco
        override val emoji = "🔥"
        override val displayName = "$emoji Char"
    };

    abstract val emoji: String
    abstract val displayName: String
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
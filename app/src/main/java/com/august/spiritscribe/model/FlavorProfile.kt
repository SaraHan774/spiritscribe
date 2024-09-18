package com.august.spiritscribe.model

data class FlavorProfile(
    val flavor: Flavor,
    val intensity: Int // A rating scale, e.g., 0 to 5
)

enum class Flavor {
    SWEET, SPICY, SMOKY, FRUITY, WOODY, PEATY, COFFEE, MINTY, NUTTY, HERBAL, FLORAL, DARK_FRUIT, CITRUS, VANILLA
}
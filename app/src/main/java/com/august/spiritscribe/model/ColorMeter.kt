package com.august.spiritscribe.model

data class ColorMeter(
    val color: WhiskeyColor
)

enum class WhiskeyColor {
    CLEAR, STRAW, HONEY, GOLD, AMBER, CARAMEL, MAHOGANY
}
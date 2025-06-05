package com.august.spiritscribe.domain.model

data class FlavorProfile(
    val aroma: List<String> = emptyList(),
    val palate: List<String> = emptyList(),
    val finish: List<String> = emptyList()
) {
    companion object {
        // 미리 정의된 향 카테고리
        val AROMA_CATEGORIES = listOf(
            "Fruity" to listOf("Apple", "Pear", "Citrus", "Berry", "Dried Fruit"),
            "Floral" to listOf("Rose", "Violet", "Lavender", "Heather"),
            "Woody" to listOf("Oak", "Cedar", "Pine", "Sandalwood"),
            "Spicy" to listOf("Cinnamon", "Nutmeg", "Pepper", "Ginger"),
            "Sweet" to listOf("Vanilla", "Honey", "Caramel", "Toffee"),
            "Smoky" to listOf("Peat", "Tobacco", "Leather", "BBQ")
        )

        // 미리 정의된 맛 카테고리
        val PALATE_CATEGORIES = listOf(
            "Sweet" to listOf("Honey", "Caramel", "Vanilla", "Toffee"),
            "Spicy" to listOf("Pepper", "Cinnamon", "Ginger", "Nutmeg"),
            "Woody" to listOf("Oak", "Cedar", "Pine"),
            "Fruity" to listOf("Apple", "Citrus", "Berry", "Dried Fruit"),
            "Nutty" to listOf("Almond", "Walnut", "Hazelnut"),
            "Malty" to listOf("Grain", "Cereal", "Bread")
        )

        // 미리 정의된 피니시 특성
        val FINISH_CHARACTERISTICS = listOf(
            "Length" to listOf("Short", "Medium", "Long"),
            "Character" to listOf("Warm", "Spicy", "Sweet", "Dry"),
            "Intensity" to listOf("Mild", "Moderate", "Strong")
        )
    }
} 
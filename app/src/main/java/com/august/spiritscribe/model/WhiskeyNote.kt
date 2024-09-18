package com.august.spiritscribe.model

/**
 * Represents a whiskey tasting note with detailed information about the whiskey.
 *
 * @property id Unique identifier for each whiskey note, often used for database operations to distinguish between entries.
 * @property name The name of the specific whiskey (e.g., "Lagavulin 16"), providing an easy way to identify the bottle being reviewed.
 * @property distillery The name of the distillery where the whiskey was produced, adding context about the whiskey's origin and production method.
 * @property origin The region or country of origin (e.g., "Scotland"), which often influences the whiskey's flavor profile.
 * @property type The category or style of whiskey (e.g., "Single Malt," "Bourbon"), describing the whiskey’s characteristics and production process.
 * @property age The age of the whiskey in years, indicating how long it was matured, which often affects its flavor and quality.
 * @property price The price of the whiskey bottle, giving context on the value and affordability of the whiskey being reviewed.
 * @property sampled A boolean indicating whether the whiskey has been tasted. Useful for distinguishing between notes on bottles you’ve sampled and those you plan to try in the future.
 * @property color Describes the whiskey's color, which can indicate its age, the type of cask used for aging, and its overall appearance.
 * @property flavors A list of flavor profiles and their intensities (e.g., smoky, fruity), capturing the complexity and nuances of the whiskey's taste.
 * @property additionalNotes Free-text field for extra observations or personal impressions about the whiskey, allowing the note-taker to add subjective comments.
 * @property finalRating A breakdown of the whiskey’s rating in categories like appearance, taste, mouthfeel, and an overall score, providing a quantified assessment of the whiskey's quality.
 */
data class WhiskeyNote(
    val id: String,
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
    val finalRating: FinalRating
)

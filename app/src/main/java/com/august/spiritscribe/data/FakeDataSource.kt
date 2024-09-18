package com.august.spiritscribe.data

import com.august.spiritscribe.model.ColorMeter
import com.august.spiritscribe.model.FinalRating
import com.august.spiritscribe.model.Flavor
import com.august.spiritscribe.model.FlavorProfile
import com.august.spiritscribe.model.WhiskeyColor
import com.august.spiritscribe.model.WhiskeyNote
import com.august.spiritscribe.ui.note.NoteUIM
import com.august.spiritscribe.ui.note.toNoteUIMs

object FakeDataSource {
    val whiskeyNotes = listOf(
        WhiskeyNote(
            id = "1",
            name = "Lagavulin 16",
            distillery = "Lagavulin",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 16,
            year = 2001,
            abv = 43.0,
            price = 95.99,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.AMBER),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.SMOKY, intensity = 4),
                FlavorProfile(flavor = Flavor.PEATY, intensity = 5)
            ),
            additionalNotes = "Rich and intense with a smoky finish.",
            finalRating = FinalRating(appearance = 4.5f, taste = 4.8f, mouthfeel = 4.6f, overall = 4.7f)
        ),
        WhiskeyNote(
            id = "2",
            name = "Glenlivet 12",
            distillery = "Glenlivet",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 12,
            year = 2003,
            abv = 40.0,
            price = 50.75,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.GOLD),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.FRUITY, intensity = 3),
                FlavorProfile(flavor = Flavor.SWEET, intensity = 4)
            ),
            additionalNotes = "Smooth with hints of vanilla.",
            finalRating = FinalRating(appearance = 4.0f, taste = 4.2f, mouthfeel = 4.1f, overall = 4.3f)
        ),
        WhiskeyNote(
            id = "3",
            name = "Macallan Double Cask",
            distillery = "Macallan",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 15,
            year = 2010,
            abv = 46.0,
            price = 120.99,
            sampled = false,
            color = ColorMeter(color = WhiskeyColor.CARAMEL),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.WOODY, intensity = 4),
                FlavorProfile(flavor = Flavor.VANILLA, intensity = 3)
            ),
            additionalNotes = "Complex, with a fruity aftertaste.",
            finalRating = FinalRating(appearance = 4.3f, taste = 4.7f, mouthfeel = 4.5f, overall = 4.6f)
        ),
        WhiskeyNote(
            id = "4",
            name = "Ardbeg 10",
            distillery = "Ardbeg",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 10,
            year = 2005,
            abv = 50.0,
            price = 85.0,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.MAHOGANY),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.PEATY, intensity = 5),
                FlavorProfile(flavor = Flavor.SMOKY, intensity = 4)
            ),
            additionalNotes = "A bit too strong on the palate.",
            finalRating = FinalRating(appearance = 4.2f, taste = 4.1f, mouthfeel = 4.0f, overall = 4.1f)
        ),
        WhiskeyNote(
            id = "5",
            name = "Glenfiddich 18",
            distillery = "Glenfiddich",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 18,
            year = 2003,
            abv = 43.5,
            price = 140.75,
            sampled = false,
            color = ColorMeter(color = WhiskeyColor.GOLD),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.FRUITY, intensity = 4),
                FlavorProfile(flavor = Flavor.SPICY, intensity = 3)
            ),
            additionalNotes = "Excellent balance of flavors.",
            finalRating = FinalRating(appearance = 4.5f, taste = 4.8f, mouthfeel = 4.7f, overall = 4.8f)
        ),
        WhiskeyNote(
            id = "6",
            name = "Glenlivet 12",
            distillery = "Glenlivet",
            origin = "Scotland",
            type = "Blended Scotch",
            age = 12,
            year = 2005,
            abv = 40.0,
            price = 59.95,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.AMBER),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.SWEET, intensity = 3),
                FlavorProfile(flavor = Flavor.FRUITY, intensity = 2)
            ),
            additionalNotes = "Smooth with hints of vanilla.",
            finalRating = FinalRating(appearance = 4.0f, taste = 4.2f, mouthfeel = 4.1f, overall = 4.2f)
        ),
        WhiskeyNote(
            id = "7",
            name = "Macallan Double Cask",
            distillery = "Macallan",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 15,
            year = 2012,
            abv = 46.0,
            price = 110.99,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.CARAMEL),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.WOODY, intensity = 4),
                FlavorProfile(flavor = Flavor.SPICY, intensity = 3)
            ),
            additionalNotes = "Complex, with a fruity aftertaste.",
            finalRating = FinalRating(appearance = 4.4f, taste = 4.7f, mouthfeel = 4.6f, overall = 4.7f)
        ),
        WhiskeyNote(
            id = "8",
            name = "Lagavulin 16",
            distillery = "Lagavulin",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 16,
            year = 2001,
            abv = 43.0,
            price = 95.25,
            sampled = false,
            color = ColorMeter(color = WhiskeyColor.AMBER),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.PEATY, intensity = 5),
                FlavorProfile(flavor = Flavor.SMOKY, intensity = 4)
            ),
            additionalNotes = "Rich and intense with a smoky finish.",
            finalRating = FinalRating(appearance = 4.6f, taste = 4.9f, mouthfeel = 4.8f, overall = 4.8f)
        ),
        WhiskeyNote(
            id = "9",
            name = "Ardbeg 10",
            distillery = "Ardbeg",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 10,
            year = 2008,
            abv = 48.2,
            price = 75.5,
            sampled = true,
            color = ColorMeter(color = WhiskeyColor.MAHOGANY),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.SMOKY, intensity = 4),
                FlavorProfile(flavor = Flavor.SPICY, intensity = 3)
            ),
            additionalNotes = "A bit too strong on the palate.",
            finalRating = FinalRating(appearance = 4.2f, taste = 4.3f, mouthfeel = 4.2f, overall = 4.3f)
        ),
        WhiskeyNote(
            id = "10",
            name = "Glenfiddich 18",
            distillery = "Glenfiddich",
            origin = "Scotland",
            type = "Single Malt Scotch",
            age = 18,
            year = 2000,
            abv = 43.5,
            price = 140.75,
            sampled = false,
            color = ColorMeter(color = WhiskeyColor.GOLD),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.FRUITY, intensity = 4),
                FlavorProfile(flavor = Flavor.WOODY, intensity = 3)
            ),
            additionalNotes = "Excellent balance of flavors.",
            finalRating = FinalRating(appearance = 4.5f, taste = 4.6f, mouthfeel = 4.5f, overall = 4.6f)
        )
    )


    fun getNoteUIM() : List<NoteUIM> {
        return whiskeyNotes.toNoteUIMs()
    }
}
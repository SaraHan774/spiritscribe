package com.august.spiritscribe.data

import com.august.spiritscribe.domain.model.ColorMeter
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.FlavorProfile
import com.august.spiritscribe.domain.model.FinalRating
import com.august.spiritscribe.domain.model.WhiskeyNote
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
            color = ColorMeter(
                hue = "Amber",
                intensity = 4
            ),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.VANILLA, intensity = 4),
                FlavorProfile(flavor = Flavor.HERB, intensity = 5)
            ),
            additionalNotes = "Rich and intense with a smoky finish.",
            finalRating = FinalRating(
                appearance = 90,
                nose = 95,
                taste = 96,
                finish = 92,
                overall = 94
            )
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
            color = ColorMeter(
                hue = "Gold",
                intensity = 3
            ),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.HERB, intensity = 3),
                FlavorProfile(flavor = Flavor.HERB, intensity = 4)
            ),
            additionalNotes = "Smooth with hints of vanilla.",
            finalRating = FinalRating(
                appearance = 85,
                nose = 88,
                taste = 84,
                finish = 82,
                overall = 85
            )
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
            color = ColorMeter(
                hue = "Caramel",
                intensity = 5
            ),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.HERB, intensity = 4),
                FlavorProfile(flavor = Flavor.VANILLA, intensity = 3)
            ),
            additionalNotes = "Complex, with a fruity aftertaste.",
            finalRating = FinalRating(
                appearance = 92,
                nose = 94,
                taste = 90,
                finish = 91,
                overall = 92
            )
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
            color = ColorMeter(
                hue = "Mahogany",
                intensity = 4
            ),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.HERB, intensity = 5),
                FlavorProfile(flavor = Flavor.HERB, intensity = 4)
            ),
            additionalNotes = "A bit too strong on the palate.",
            finalRating = FinalRating(
                appearance = 84,
                nose = 82,
                taste = 80,
                finish = 82,
                overall = 82
            )
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
            color = ColorMeter(
                hue = "Gold",
                intensity = 3
            ),
            flavors = listOf(
                FlavorProfile(flavor = Flavor.HERB, intensity = 4),
                FlavorProfile(flavor = Flavor.HERB, intensity = 3)
            ),
            additionalNotes = "Excellent balance of flavors.",
            finalRating = FinalRating(
                appearance = 96,
                nose = 94,
                taste = 96,
                finish = 95,
                overall = 95
            )
        )
    )


    fun getNoteUIM() : List<NoteUIM> {
        return whiskeyNotes.toNoteUIMs()
    }
}
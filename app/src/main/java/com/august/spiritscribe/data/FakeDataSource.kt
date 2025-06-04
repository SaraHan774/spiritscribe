package com.august.spiritscribe.data

import com.august.spiritscribe.ui.note.NoteUIM

object FakeDataSource {
    fun getNoteUIM(): List<NoteUIM> = listOf(
        NoteUIM(
            id = "1",
            name = "Macallan 12",
            year = "2021",
            age = "12",
            abv = "43%",
            description = "Rich and complex with notes of dried fruits, vanilla, and spice.",
            imageUrl = "https://www.themacallan.com/sites/default/files/2019-03/Double-Cask-12-Years-Old.png"
        ),
        NoteUIM(
            id = "2",
            name = "Highland Park 18",
            year = "2020",
            age = "18",
            abv = "43%",
            description = "Balanced sweetness with heather honey, dark chocolate, and gentle smoke.",
            imageUrl = "https://www.highlandparkwhisky.com/wp-content/uploads/2019/03/Highland-Park-18-Year-Old-Viking-Pride.png"
        ),
        NoteUIM(
            id = "3",
            name = "Lagavulin 16",
            year = "2019",
            age = "16",
            abv = "43%",
            description = "Intense peat smoke with iodine, rich sweetness and hints of salt and wood.",
            imageUrl = "https://www.malts.com/media/1596/lagavulin-16-year-old-single-malt-scotch-whisky.png"
        )
    )
}
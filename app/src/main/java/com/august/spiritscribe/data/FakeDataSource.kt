package com.august.spiritscribe.data

import com.august.spiritscribe.model.Note
import com.august.spiritscribe.ui.note.NoteUIM
import com.august.spiritscribe.ui.note.toNoteUIMs

object FakeDataSource {

    private val whiskeyNotes = listOf(
        Note(
            id = "1",
            distillery = "Glenfiddich",
            bottler = "Original",
            year = 2001,
            age = 12,
            abv = 45.0,
            nose = "Fruity",
            palate = "Smooth",
            finish = "Long",
            rating = 4.5f,
            tastingDate = "2024-09-12",
            notes = "Great texture and depth."
        ),
        Note(
            id = "2",
            distillery = "Lagavulin",
            bottler = "Independent",
            year = 1998,
            age = 16,
            abv = 55.3,
            nose = "Peaty",
            palate = "Bold",
            finish = "Lingering",
            rating = 4.8f,
            tastingDate = "2024-09-12",
            notes = "Excellent balance of flavors."
        ),
        Note(
            id = "3",
            distillery = "Macallan",
            bottler = "Special Edition",
            year = null,
            age = 18,
            abv = 50.0,
            nose = "Vanilla",
            palate = "Rich",
            finish = "Warm",
            rating = 4.0f,
            tastingDate = "2024-09-12",
            notes = "Complex, with a fruity aftertaste."
        ),
        Note(
            id = "4",
            distillery = "Ardbeg",
            bottler = "Original",
            year = 2012,
            age = 8,
            abv = 48.5,
            nose = "Smoky",
            palate = "Bold",
            finish = "Dry",
            rating = 3.7f,
            tastingDate = "2024-09-12",
            notes = "A bit too strong on the palate."
        ),
        Note(
            id = "5",
            distillery = "Glenlivet",
            bottler = "Independent",
            year = 1995,
            age = 21,
            abv = 43.2,
            nose = "Spicy",
            palate = "Smooth",
            finish = "Short",
            rating = 4.2f,
            tastingDate = "2024-09-12",
            notes = "Smooth with hints of vanilla."
        ),
        Note(
            id = "6",
            distillery = "Glenfiddich",
            bottler = "Special Edition",
            year = null,
            age = 15,
            abv = 47.0,
            nose = "Fruity",
            palate = "Sweet",
            finish = "Long",
            rating = 4.6f,
            tastingDate = "2024-09-12",
            notes = "Great texture and depth."
        ),
        Note(
            id = "7",
            distillery = "Lagavulin",
            bottler = "Original",
            year = 2005,
            age = 10,
            abv = 49.5,
            nose = "Peaty",
            palate = "Complex",
            finish = "Warm",
            rating = 4.4f,
            tastingDate = "2024-09-12",
            notes = "Excellent balance of flavors."
        ),
        Note(
            id = "8",
            distillery = "Macallan",
            bottler = "Independent",
            year = 2010,
            age = 12,
            abv = 46.7,
            nose = "Vanilla",
            palate = "Rich",
            finish = "Dry",
            rating = 4.1f,
            tastingDate = "2024-09-12",
            notes = "Smooth with hints of vanilla."
        ),
        Note(
            id = "9",
            distillery = "Ardbeg",
            bottler = "Special Edition",
            year = null,
            age = 9,
            abv = 52.3,
            nose = "Smoky",
            palate = "Bold",
            finish = "Lingering",
            rating = 3.9f,
            tastingDate = "2024-09-12",
            notes = "A bit too strong on the palate."
        ),
        Note(
            id = "10",
            distillery = "Glenlivet",
            bottler = "Original",
            year = 2000,
            age = 20,
            abv = 44.8,
            nose = "Spicy",
            palate = "Smooth",
            finish = "Short",
            rating = 4.7f,
            tastingDate = "2024-09-12",
            notes = "Complex, with a fruity aftertaste."
        )
    )

    fun getNoteUIM() : List<NoteUIM> {
        return whiskeyNotes.toNoteUIMs()
    }
}
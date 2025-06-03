package com.august.spiritscribe.domain.repository

import com.august.spiritscribe.domain.model.WhiskeyNote
import kotlinx.coroutines.flow.Flow

interface WhiskeyNoteRepository {
    suspend fun createNote(note: WhiskeyNote): Result<WhiskeyNote>
    suspend fun updateNote(note: WhiskeyNote): Result<WhiskeyNote>
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun getNoteById(noteId: String): Result<WhiskeyNote>
    fun getNotes(filters: NoteFilters = NoteFilters()): Flow<List<WhiskeyNote>>
    fun searchNotes(query: String): Flow<List<WhiskeyNote>>
}

data class NoteFilters(
    val searchQuery: String = "",
    val type: String? = null,
    val origin: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Int? = null,
    val sampledOnly: Boolean = false,
    val sortBy: NoteSortOption = NoteSortOption.CREATED_DESC
)

enum class NoteSortOption {
    CREATED_DESC,
    CREATED_ASC,
    NAME_ASC,
    NAME_DESC,
    RATING_DESC,
    RATING_ASC
} 
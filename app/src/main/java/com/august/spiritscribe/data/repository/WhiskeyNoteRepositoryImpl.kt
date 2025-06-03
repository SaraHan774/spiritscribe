package com.august.spiritscribe.data.repository

import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.repository.NoteFilters
import com.august.spiritscribe.domain.repository.NoteSortOption
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WhiskeyNoteRepositoryImpl @Inject constructor(
    private val whiskeyNoteDao: WhiskeyNoteDao
) : WhiskeyNoteRepository {

    override suspend fun createNote(note: WhiskeyNote): Result<WhiskeyNote> = runCatching {
        val entity = WhiskeyNoteEntity.fromDomain(note)
        whiskeyNoteDao.insertNote(entity)
        note
    }

    override suspend fun updateNote(note: WhiskeyNote): Result<WhiskeyNote> = runCatching {
        val entity = WhiskeyNoteEntity.fromDomain(note)
        whiskeyNoteDao.updateNote(entity)
        note
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> = runCatching {
        whiskeyNoteDao.getNoteById(noteId)?.let { 
            whiskeyNoteDao.deleteNote(it)
        } ?: throw IllegalArgumentException("Note not found")
    }

    override suspend fun getNoteById(noteId: String): Result<WhiskeyNote> = runCatching {
        whiskeyNoteDao.getNoteById(noteId)?.toDomain() 
            ?: throw IllegalArgumentException("Note not found")
    }

    override fun getNotes(filters: NoteFilters): Flow<List<WhiskeyNote>> {
        val flow = when (filters.sortBy) {
            NoteSortOption.CREATED_DESC -> whiskeyNoteDao.getNotesByCreatedDesc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
            NoteSortOption.CREATED_ASC -> whiskeyNoteDao.getNotesByCreatedAsc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
            NoteSortOption.NAME_ASC -> whiskeyNoteDao.getNotesByNameAsc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
            NoteSortOption.NAME_DESC -> whiskeyNoteDao.getNotesByNameDesc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
            NoteSortOption.RATING_DESC -> whiskeyNoteDao.getNotesByRatingDesc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
            else -> whiskeyNoteDao.getNotesByCreatedDesc(
                searchQuery = filters.searchQuery,
                type = filters.type,
                origin = filters.origin,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                minRating = filters.minRating,
                sampledOnly = filters.sampledOnly
            )
        }
        return flow.map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchNotes(query: String): Flow<List<WhiskeyNote>> {
        return whiskeyNoteDao.searchNotes(query)
            .map { entities -> entities.map { it.toDomain() } }
    }
} 
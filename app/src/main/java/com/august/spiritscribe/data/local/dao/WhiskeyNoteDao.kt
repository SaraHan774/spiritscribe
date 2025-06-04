package com.august.spiritscribe.data.local.dao

import androidx.room.*
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WhiskeyNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: WhiskeyNoteEntity)

    @Update
    suspend fun updateNote(note: WhiskeyNoteEntity)

    @Delete
    suspend fun deleteNote(note: WhiskeyNoteEntity)

    @Query("SELECT * FROM whiskey_notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): WhiskeyNoteEntity?

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR additionalNotes LIKE '%' || :searchQuery || '%')
        AND (:type IS NULL OR type = :type)
        AND (:origin IS NULL OR origin = :origin)
        AND (:minPrice IS NULL OR price >= :minPrice)
        AND (:maxPrice IS NULL OR price <= :maxPrice)
        AND (:minRating IS NULL OR overall >= :minRating)
        AND (:sampledOnly = 0 OR sampled = 1)
        ORDER BY name DESC
    """)
    fun getNotesByCreatedDesc(
        searchQuery: String = "",
        type: String? = null,
        origin: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sampledOnly: Boolean = false
    ): Flow<List<WhiskeyNoteEntity>>

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR additionalNotes LIKE '%' || :searchQuery || '%')
        AND (:type IS NULL OR type = :type)
        AND (:origin IS NULL OR origin = :origin)
        AND (:minPrice IS NULL OR price >= :minPrice)
        AND (:maxPrice IS NULL OR price <= :maxPrice)
        AND (:minRating IS NULL OR overall >= :minRating)
        AND (:sampledOnly = 0 OR sampled = 1)
        ORDER BY name ASC
    """)
    fun getNotesByCreatedAsc(
        searchQuery: String = "",
        type: String? = null,
        origin: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sampledOnly: Boolean = false
    ): Flow<List<WhiskeyNoteEntity>>

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR additionalNotes LIKE '%' || :searchQuery || '%')
        AND (:type IS NULL OR type = :type)
        AND (:origin IS NULL OR origin = :origin)
        AND (:minPrice IS NULL OR price >= :minPrice)
        AND (:maxPrice IS NULL OR price <= :maxPrice)
        AND (:minRating IS NULL OR overall >= :minRating)
        AND (:sampledOnly = 0 OR sampled = 1)
        ORDER BY name ASC
    """)
    fun getNotesByNameAsc(
        searchQuery: String = "",
        type: String? = null,
        origin: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sampledOnly: Boolean = false
    ): Flow<List<WhiskeyNoteEntity>>

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR additionalNotes LIKE '%' || :searchQuery || '%')
        AND (:type IS NULL OR type = :type)
        AND (:origin IS NULL OR origin = :origin)
        AND (:minPrice IS NULL OR price >= :minPrice)
        AND (:maxPrice IS NULL OR price <= :maxPrice)
        AND (:minRating IS NULL OR overall >= :minRating)
        AND (:sampledOnly = 0 OR sampled = 1)
        ORDER BY name DESC
    """)
    fun getNotesByNameDesc(
        searchQuery: String = "",
        type: String? = null,
        origin: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sampledOnly: Boolean = false
    ): Flow<List<WhiskeyNoteEntity>>

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR additionalNotes LIKE '%' || :searchQuery || '%')
        AND (:type IS NULL OR type = :type)
        AND (:origin IS NULL OR origin = :origin)
        AND (:minPrice IS NULL OR price >= :minPrice)
        AND (:maxPrice IS NULL OR price <= :maxPrice)
        AND (:minRating IS NULL OR overall >= :minRating)
        AND (:sampledOnly = 0 OR sampled = 1)
        ORDER BY overall DESC
    """)
    fun getNotesByRatingDesc(
        searchQuery: String = "",
        type: String? = null,
        origin: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sampledOnly: Boolean = false
    ): Flow<List<WhiskeyNoteEntity>>

    @Query("""
        SELECT * FROM whiskey_notes 
        WHERE name LIKE '%' || :query || '%' 
        OR additionalNotes LIKE '%' || :query || '%'
        OR distillery LIKE '%' || :query || '%'
        ORDER BY name DESC
    """)
    fun searchNotes(query: String): Flow<List<WhiskeyNoteEntity>>
} 
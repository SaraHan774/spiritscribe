package com.august.spiritscribe.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WhiskeyDao {
    @Query("SELECT * FROM whiskeys ORDER BY createdAt DESC")
    fun getAllWhiskeys(): Flow<List<WhiskeyEntity>>

    @Query("SELECT * FROM whiskeys WHERE id = :id")
    fun getWhiskeyById(id: String): Flow<WhiskeyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhiskey(whiskey: WhiskeyEntity)

    @Update
    suspend fun updateWhiskey(whiskey: WhiskeyEntity)

    @Delete
    suspend fun deleteWhiskey(whiskey: WhiskeyEntity)

    @Query("SELECT * FROM flavor_profiles WHERE whiskeyId = :whiskeyId")
    fun getFlavorProfileForWhiskey(whiskeyId: String): Flow<FlavorProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlavorProfile(flavorProfile: FlavorProfileEntity)

    @Transaction
    suspend fun insertWhiskeyWithProfile(whiskey: WhiskeyEntity, flavorProfile: FlavorProfileEntity) {
        insertWhiskey(whiskey)
        insertFlavorProfile(flavorProfile)
    }

    @Query("SELECT * FROM whiskeys WHERE name LIKE '%' || :query || '%' OR distillery LIKE '%' || :query || '%'")
    fun searchWhiskeys(query: String): Flow<List<WhiskeyEntity>>
} 
package com.august.spiritscribe.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlavorProfileDao {
    @Query("SELECT * FROM flavor_profiles WHERE whiskeyId = :whiskeyId")
    fun getFlavorProfilesByWhiskeyId(whiskeyId: String): Flow<List<FlavorProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlavorProfile(flavorProfile: FlavorProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlavorProfiles(flavorProfiles: List<FlavorProfileEntity>)

    @Update
    suspend fun updateFlavorProfile(flavorProfile: FlavorProfileEntity)

    @Delete
    suspend fun deleteFlavorProfile(flavorProfile: FlavorProfileEntity)

    @Query("DELETE FROM flavor_profiles WHERE whiskeyId = :whiskeyId")
    suspend fun deleteFlavorProfilesByWhiskeyId(whiskeyId: String)
} 
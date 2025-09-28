package com.august.spiritscribe.di

import android.content.Context
import androidx.room.Room
import com.august.spiritscribe.data.local.SpiritScribeDatabase
import com.august.spiritscribe.data.local.dao.FlavorProfileDao
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SpiritScribeDatabase {
        android.util.Log.d("DatabaseModule", "Creating database instance")
        return Room.databaseBuilder(
            context,
            SpiritScribeDatabase::class.java,
            "spiritscribe.db"
        )
        .fallbackToDestructiveMigration()
        .addCallback(SpiritScribeDatabase.createCallback(context))
        .build()
        .also { 
            android.util.Log.d("DatabaseModule", "Database instance created")
            SpiritScribeDatabase.setInstance(it)
        }
    }

    @Provides
    @Singleton
    fun provideWhiskeyDao(database: SpiritScribeDatabase): WhiskeyDao {
        return database.whiskeyDao()
    }

    @Provides
    @Singleton
    fun provideWhiskeyNoteDao(database: SpiritScribeDatabase): WhiskeyNoteDao {
        return database.whiskeyNoteDao()
    }

    @Provides
    @Singleton
    fun provideFlavorProfileDao(database: SpiritScribeDatabase): FlavorProfileDao {
        return database.flavorProfileDao()
    }
} 
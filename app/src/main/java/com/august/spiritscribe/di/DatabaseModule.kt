package com.august.spiritscribe.di

import android.content.Context
import androidx.room.Room
import com.august.spiritscribe.data.local.SpiritScribeDatabase
import com.august.spiritscribe.data.local.dao.FlavorProfileDao
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.data.repository.WhiskeyNoteRepositoryImpl
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
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
        val callback = SpiritScribeDatabase.createCallback(context)
        val database = Room.databaseBuilder(
            context,
            SpiritScribeDatabase::class.java,
            "spiritscribe_v5.db"  // 새로운 데이터베이스 파일명으로 확실한 재생성
        )
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()
        SpiritScribeDatabase.setInstance(database)
        return database
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

    @Provides
    @Singleton
    fun provideWhiskeyNoteRepository(whiskeyNoteDao: WhiskeyNoteDao): WhiskeyNoteRepository {
        return WhiskeyNoteRepositoryImpl(whiskeyNoteDao)
    }
    
    @Provides
    @Singleton
    fun provideWhiskeyRepository(
        whiskeyDao: WhiskeyDao, 
        whiskeyNoteDao: WhiskeyNoteDao
    ): com.august.spiritscribe.domain.repository.WhiskeyRepository {
        return com.august.spiritscribe.data.repository.WhiskeyRepositoryImpl(whiskeyDao, whiskeyNoteDao)
    }
} 
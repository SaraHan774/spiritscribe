package com.august.spiritscribe.data.repository

import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.repository.WhiskeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WhiskeyRepositoryImpl @Inject constructor(
    private val whiskeyDao: WhiskeyDao,
    private val whiskeyNoteDao: WhiskeyNoteDao
) : WhiskeyRepository {

    override fun getAllWhiskies(): Flow<List<Whiskey>> {
        return whiskeyDao.getAllWhiskeys()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getWhiskeyById(whiskeyId: String): Result<Whiskey> {
        return try {
            android.util.Log.d("WhiskeyRepository", "🔍 위스키 ID로 검색: $whiskeyId")
            val entity = whiskeyDao.getWhiskeyById(whiskeyId)
                .first() // 첫 번째 값만 가져오기
            
            if (entity == null) {
                android.util.Log.w("WhiskeyRepository", "⚠️ 위스키를 찾을 수 없음: $whiskeyId")
                Result.failure(IllegalArgumentException("Whiskey not found: $whiskeyId"))
            } else {
                android.util.Log.d("WhiskeyRepository", "✅ 위스키 찾음: ${entity.name} (${entity.distillery})")
                Result.success(entity.toDomain())
            }
        } catch (e: Exception) {
            android.util.Log.e("WhiskeyRepository", "❌ 위스키 검색 실패: $whiskeyId", e)
            Result.failure(e)
        }
    }

    override fun getNotesForWhiskey(whiskeyName: String, distillery: String): Flow<List<WhiskeyNote>> {
        android.util.Log.d("WhiskeyRepository", "🔍 노트 검색 시작: 이름='$whiskeyName', 증류소='$distillery'")
        return whiskeyNoteDao.getNotesByWhiskeyNameAndDistillery(whiskeyName, distillery)
            .map { entities -> 
                android.util.Log.d("WhiskeyRepository", "📊 DAO에서 받은 엔티티 수: ${entities.size}")
                entities.forEach { entity ->
                    android.util.Log.d("WhiskeyRepository", "  - 엔티티: ${entity.name} (${entity.distillery})")
                }
                entities.map { it.toDomain() }
            }
    }

    override fun searchWhiskies(query: String): Flow<List<Whiskey>> {
        return whiskeyDao.searchWhiskeys(query)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun getTotalNoteCount(): Int {
        return whiskeyNoteDao.getTotalNoteCount()
    }
    
    override suspend fun getFirstFiveNotes(): List<WhiskeyNote> {
        return whiskeyNoteDao.getFirstFiveNotes().map { it.toDomain() }
    }
}

/**
 * WhiskeyEntity를 Whiskey domain 모델로 변환
 */
private fun com.august.spiritscribe.data.local.entity.WhiskeyEntity.toDomain(): Whiskey {
    return Whiskey(
        id = id,
        name = name,
        distillery = distillery,
        type = type,
        age = age,
        year = year,
        abv = abv,
        price = price,
        region = region,
        description = description,
        rating = rating,
        imageUris = imageUris,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

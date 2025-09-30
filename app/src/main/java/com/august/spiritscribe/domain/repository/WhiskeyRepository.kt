package com.august.spiritscribe.domain.repository

import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import kotlinx.coroutines.flow.Flow

interface WhiskeyRepository {
    /**
     * 모든 위스키 목록을 가져옵니다.
     */
    fun getAllWhiskies(): Flow<List<Whiskey>>
    
    /**
     * 특정 위스키의 정보를 가져옵니다.
     */
    suspend fun getWhiskeyById(whiskeyId: String): Result<Whiskey>
    
    /**
     * 특정 위스키에 대한 모든 노트를 시간순으로 가져옵니다.
     */
    fun getNotesForWhiskey(whiskeyName: String, distillery: String): Flow<List<WhiskeyNote>>
    
    /**
     * 위스키를 검색합니다.
     */
    fun searchWhiskies(query: String): Flow<List<Whiskey>>
    
    /**
     * 디버깅용: 전체 노트 수를 가져옵니다.
     */
    suspend fun getTotalNoteCount(): Int
    
    /**
     * 디버깅용: 처음 5개 노트를 가져옵니다.
     */
    suspend fun getFirstFiveNotes(): List<WhiskeyNote>
}

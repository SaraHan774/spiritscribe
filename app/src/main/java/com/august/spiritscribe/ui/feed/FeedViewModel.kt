package com.august.spiritscribe.ui.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.repository.WhiskeyRepository
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WhiskeyFeedItem(
    val id: String,
    val name: String,
    val distillery: String,
    val type: String,
    val age: Int?,
    val year: Int?,
    val abv: Double,
    val price: Double?,
    val region: String?,
    val description: String,
    val rating: Int?,
    val imageUris: List<String>,
    val timestamp: Long,
    val noteCount: Int // 이 위스키에 대한 노트 개수
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val whiskeyRepository: WhiskeyRepository,
    private val whiskeyNoteRepository: WhiskeyNoteRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // 피드 아이템들을 Flow로 관리
    private val _feedItems = MutableStateFlow<List<WhiskeyFeedItem>>(emptyList())
    val feedItems: StateFlow<List<WhiskeyFeedItem>> = _feedItems.asStateFlow()

    init {
        loadFeedData()
    }

    private fun loadFeedData() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                
                // 모든 위스키를 가져와서 각각의 노트 개수와 함께 피드 아이템으로 변환
                whiskeyRepository.getAllWhiskies()
                .map { whiskies -> 
                    // 각 위스키에 대해 노트 개수를 계산
                    whiskies.map { whiskey ->
                        val noteCount = try {
                            whiskeyRepository.getNotesForWhiskey(whiskey.name, whiskey.distillery)
                                .first()
                                .size
                        } catch (e: Exception) {
                            0 // 노트 조회 실패 시 0으로 설정
                        }
                        whiskey.toFeedItem(noteCount)
                    }
                }
                .catch { throwable ->
                    error = "Failed to load feed data: ${throwable.message}"
                }
                .collect { feedItems ->
                    _feedItems.value = feedItems
                    isLoading = false
                }
            } catch (e: Exception) {
                error = "Failed to load feed data: ${e.message}"
                isLoading = false
            }
        }
    }

    /**
     * Whiskey를 WhiskeyFeedItem으로 변환하는 확장 함수
     */
    private fun Whiskey.toFeedItem(noteCount: Int): WhiskeyFeedItem = WhiskeyFeedItem(
        id = this.id,
        name = this.name,
        distillery = this.distillery,
        type = this.type.name,
        age = this.age,
        year = this.year,
        abv = this.abv,
        price = this.price,
        region = this.region,
        description = this.description,
        rating = this.rating,
        imageUris = this.imageUris,
        timestamp = java.time.ZoneOffset.UTC.let { this.createdAt.toEpochSecond(it) * 1000 },
        noteCount = noteCount // 실제 노트 개수 사용
    )

    fun refresh() {
        loadFeedData()
    }
} 
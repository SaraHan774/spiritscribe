package com.august.spiritscribe.ui.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.repository.NoteFilters
import com.august.spiritscribe.domain.repository.NoteSortOption
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val whiskeyName: String,
    val distillery: String,
    val notes: String,
    val rating: Int,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val imageUrl: String?
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val whiskeyNoteRepository: WhiskeyNoteRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // 피드 아이템들을 Flow로 관리
    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

    // 좋아요 상태 관리 (임시로 메모리에서 관리)
    private val likedNotes = mutableSetOf<String>()

    init {
        loadFeedData()
    }

    private fun loadFeedData() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                
                // 위스키 노트를 최신순으로 가져와서 피드 아이템으로 변환
                whiskeyNoteRepository.getNotes(
                    NoteFilters(sortBy = NoteSortOption.CREATED_DESC)
                )
                .map { notes -> notes.map { it.toFeedItem() } }
                .catch { throwable ->
                    error = "Failed to load feed data: ${throwable.message}"
                    android.util.Log.e("FeedViewModel", "Error loading feed", throwable)
                }
                .collect { feedItems ->
                    _feedItems.value = feedItems
                    isLoading = false
                }
            } catch (e: Exception) {
                error = "Failed to load feed data: ${e.message}"
                isLoading = false
                android.util.Log.e("FeedViewModel", "Error in loadFeedData", e)
            }
        }
    }

    /**
     * WhiskeyNote를 FeedItem으로 변환하는 확장 함수
     */
    private fun WhiskeyNote.toFeedItem(): FeedItem = FeedItem(
        id = this.id,
        userId = this.userId ?: "anonymous",
        userName = "Whiskey Enthusiast", // 실제 사용자 시스템이 있을 때 변경
        userAvatarUrl = null, // 실제 사용자 시스템이 있을 때 변경
        whiskeyName = this.name,
        distillery = this.distillery,
        notes = this.additionalNotes,
        rating = (this.finalRating.overall / 20).coerceIn(1, 5), // 100점 기준을 5점 기준으로 변환
        timestamp = this.createdAt,
        likesCount = (0..50).random(), // 임시 랜덤 값
        commentsCount = (0..20).random(), // 임시 랜덤 값
        isLiked = likedNotes.contains(this.id),
        imageUrl = this.imageUrl
    )

    fun refresh() {
        loadFeedData()
    }

    fun toggleLike(noteId: String) {
        viewModelScope.launch {
            try {
                if (likedNotes.contains(noteId)) {
                    likedNotes.remove(noteId)
                } else {
                    likedNotes.add(noteId)
                }
                
                // 현재 피드 아이템들을 업데이트하여 좋아요 상태 반영
                val currentItems = _feedItems.value
                val updatedItems = currentItems.map { item ->
                    if (item.id == noteId) {
                        item.copy(
                            isLiked = likedNotes.contains(noteId),
                            likesCount = if (likedNotes.contains(noteId)) item.likesCount + 1 else item.likesCount - 1
                        )
                    } else {
                        item
                    }
                }
                _feedItems.value = updatedItems
                
            } catch (e: Exception) {
                error = "Failed to update like: ${e.message}"
                android.util.Log.e("FeedViewModel", "Error toggling like", e)
            }
        }
    }
} 
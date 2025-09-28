package com.august.spiritscribe.ui.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val whiskeyName: String,
    val notes: String,
    val rating: Int,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    // TODO: Inject repositories
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val feedItems: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 3
        )
    ) {
        // TODO: Replace with actual paging source from repository
        MockFeedPagingSource()
    }.flow.cachedIn(viewModelScope)

    fun refresh() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                // TODO: Implement refresh logic
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleLike(noteId: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement like/unlike functionality
            } catch (e: Exception) {
                error = "Failed to update like: ${e.message}"
            }
        }
    }
}

// Mock implementation for demonstration
private class MockFeedPagingSource : androidx.paging.PagingSource<Int, FeedItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItem> {
        return try {
            // Simulate network delay
            kotlinx.coroutines.delay(1000)

            val nextKey = params.key ?: 0
            val items = List(params.loadSize) { index ->
                FeedItem(
                    // FakeDataSource note ids are "1","2","3"; map feed ids to these to support detail screen
                    id = listOf("1", "2", "3")[(nextKey + index) % 3],
                    userId = "user${(nextKey + index) % 5}",
                    userName = "Whiskey Lover ${(nextKey + index) % 5}",
                    userAvatarUrl = null,
                    whiskeyName = "Sample Whiskey ${nextKey + index}",
                    notes = "This is a delightful whiskey with notes of vanilla, caramel, and oak. " +
                            "The finish is smooth with a hint of spice.",
                    rating = (3..5).random(),
                    timestamp = System.currentTimeMillis() - ((nextKey + index) * 3600000L),
                    likesCount = (0..100).random(),
                    commentsCount = (0..50).random(),
                    isLiked = (nextKey + index) % 3 == 0
                )
            }

            LoadResult.Page(
                data = items,
                prevKey = if (nextKey == 0) null else nextKey - 1,
                nextKey = nextKey + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<Int, FeedItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
} 
package com.august.spiritscribe.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val userTitle: String = "",
    val profileImageUrl: String? = null,
    val stats: UserStats = UserStats(),
    val preferences: UserPreferences = UserPreferences(),
    val recentActivity: List<ActivityItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class UserStats(
    val tastedCount: Int = 0,
    val reviewCount: Int = 0,
    val favoritesCount: Int = 0
)

data class UserPreferences(
    val favoriteTypes: List<String> = emptyList(),
    val flavorProfile: List<String> = emptyList()
)

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val whiskeyName: String,
    val timestamp: Long
)

enum class ActivityType {
    TASTING_NOTE,
    FAVORITE,
    RATING
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // TODO: Inject repositories
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // TODO: Load actual data from repositories
                _uiState.value = ProfileUiState(
                    userName = "John Whiskey",
                    userTitle = "Whiskey Enthusiast",
                    profileImageUrl = null,
                    stats = UserStats(
                        tastedCount = 42,
                        reviewCount = 38,
                        favoritesCount = 12
                    ),
                    preferences = UserPreferences(
                        favoriteTypes = listOf("Single Malt", "Bourbon", "Islay"),
                        flavorProfile = listOf("Smoky", "Sweet", "Fruity")
                    ),
                    recentActivity = listOf(
                        ActivityItem(
                            id = "1",
                            type = ActivityType.TASTING_NOTE,
                            whiskeyName = "Macallan 12 Double Cask",
                            timestamp = System.currentTimeMillis() - 7_200_000 // 2 hours ago
                        ),
                        ActivityItem(
                            id = "2",
                            type = ActivityType.FAVORITE,
                            whiskeyName = "Highland Park 18",
                            timestamp = System.currentTimeMillis() - 86_400_000 // 1 day ago
                        ),
                        ActivityItem(
                            id = "3",
                            type = ActivityType.TASTING_NOTE,
                            whiskeyName = "Lagavulin 16",
                            timestamp = System.currentTimeMillis() - 259_200_000 // 3 days ago
                        )
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load profile: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onEditProfile() {
        // TODO: Implement edit profile functionality
    }
} 
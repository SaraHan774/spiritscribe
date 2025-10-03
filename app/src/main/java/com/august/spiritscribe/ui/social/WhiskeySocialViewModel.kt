package com.august.spiritscribe.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.ui.social.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WhiskeySocialUiState(
    val feedState: SocialFeedState = SocialFeedState(),
    val exploreState: ExploreState = ExploreState(),
    val profileState: UserProfileState = UserProfileState(),
    val checkInState: CheckInState = CheckInState()
)

@HiltViewModel
class WhiskeySocialViewModel @Inject constructor(
    // TODO: Inject repositories
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WhiskeySocialUiState())
    val uiState: StateFlow<WhiskeySocialUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        loadFeed()
        loadExplore()
        loadProfile()
    }
    
    // 피드 관련 함수들
    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                feedState = _uiState.value.feedState.copy(isLoading = true)
            )
            
            try {
                // TODO: Load actual data from repository
                val mockPosts = generateMockPosts()
                _uiState.value = _uiState.value.copy(
                    feedState = SocialFeedState(
                        posts = mockPosts,
                        isLoading = false
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    feedState = _uiState.value.feedState.copy(
                        error = "피드를 불러올 수 없습니다: ${e.message}"
                    )
                )
            }
        }
    }
    
    fun refreshFeed() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                feedState = _uiState.value.feedState.copy(isRefreshing = true)
            )
            
            try {
                // TODO: Refresh data from repository
                val mockPosts = generateMockPosts()
                _uiState.value = _uiState.value.copy(
                    feedState = SocialFeedState(
                        posts = mockPosts,
                        isRefreshing = false
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    feedState = _uiState.value.feedState.copy(
                        isRefreshing = false,
                        error = "피드를 새로고침할 수 없습니다: ${e.message}"
                    )
                )
            }
        }
    }
    
    fun loadMorePosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                feedState = _uiState.value.feedState.copy(isLoading = true)
            )
            
            try {
                // TODO: Load more posts from repository
                val currentPosts = _uiState.value.feedState.posts
                val morePosts = generateMockPosts().take(5)
                _uiState.value = _uiState.value.copy(
                    feedState = SocialFeedState(
                        posts = currentPosts + morePosts,
                        isLoading = false
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    feedState = _uiState.value.feedState.copy(
                        isLoading = false,
                        error = "추가 포스트를 불러올 수 없습니다: ${e.message}"
                    )
                )
            }
        }
    }
    
    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                // TODO: Like post via repository
                val currentPosts = _uiState.value.feedState.posts
                val updatedPosts = currentPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            isLikedByMe = !post.isLikedByMe,
                            likesCount = if (post.isLikedByMe) post.likesCount - 1 else post.likesCount + 1
                        )
                    } else {
                        post
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    feedState = _uiState.value.feedState.copy(posts = updatedPosts)
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // 탐색 관련 함수들
    fun loadExplore() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                exploreState = _uiState.value.exploreState.copy(isLoading = true)
            )
            
            try {
                // TODO: Load explore data from repository
                val mockTrendingPosts = generateMockPosts().take(3)
                val mockSuggestedUsers = generateMockUsers()
                val mockTrendingWhiskies = generateMockTrendingWhiskies()
                
                _uiState.value = _uiState.value.copy(
                    exploreState = ExploreState(
                        trendingPosts = mockTrendingPosts,
                        suggestedUsers = mockSuggestedUsers,
                        trendingWhiskies = mockTrendingWhiskies,
                        isLoading = false
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    exploreState = _uiState.value.exploreState.copy(
                        error = "탐색 데이터를 불러올 수 없습니다: ${e.message}"
                    )
                )
            }
        }
    }
    
    fun refreshExplore() {
        loadExplore()
    }
    
    // 프로필 관련 함수들
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                profileState = _uiState.value.profileState.copy(isLoading = true)
            )
            
            try {
                // TODO: Load current user profile from repository
                val mockUser = generateMockCurrentUser()
                val mockUserPosts = generateMockPosts().take(10)
                
                _uiState.value = _uiState.value.copy(
                    profileState = UserProfileState(
                        user = mockUser,
                        posts = mockUserPosts,
                        isLoading = false,
                        isOwnProfile = true
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    profileState = _uiState.value.profileState.copy(
                        error = "프로필을 불러올 수 없습니다: ${e.message}"
                    )
                )
            }
        }
    }
    
    fun refreshProfile() {
        loadProfile()
    }
    
    // 네비게이션 함수들
    fun navigateToComments(postId: String) {
        // TODO: Navigate to comments screen
    }
    
    fun navigateToUserProfile(userId: String) {
        // TODO: Navigate to user profile screen
    }
    
    fun navigateToWhiskeyDetail(whiskeyId: String) {
        // TODO: Navigate to whiskey detail screen
    }
    
    fun navigateToPostDetail(postId: String) {
        // TODO: Navigate to post detail screen
    }
    
    fun navigateToCreateCheckIn() {
        // TODO: Navigate to create check-in screen
    }
    
    fun navigateToEditProfile() {
        // TODO: Navigate to edit profile screen
    }
    
    // Mock 데이터 생성 함수들
    private fun generateMockPosts(): List<WhiskeyCheckIn> {
        return listOf(
            WhiskeyCheckIn(
                id = "1",
                userId = "user1",
                username = "whiskeylover1",
                userDisplayName = "위스키러버",
                userProfileImageUrl = null,
                whiskeyId = "whiskey1",
                whiskeyName = "Macallan 18",
                whiskeyImageUrl = null,
                location = "강남 위스키바",
                rating = 4.5f,
                notes = "정말 부드럽고 복잡한 맛이에요. 오크 향이 특히 좋습니다!",
                images = emptyList(),
                tags = listOf("싱글몰트", "스모키", "프리미엄"),
                likesCount = 24,
                commentsCount = 8,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 3600000 // 1시간 전
            ),
            WhiskeyCheckIn(
                id = "2",
                userId = "user2",
                username = "scotchfan",
                userDisplayName = "스카치팬",
                userProfileImageUrl = null,
                whiskeyId = "whiskey2",
                whiskeyName = "Lagavulin 16",
                whiskeyImageUrl = null,
                location = "집",
                rating = 4.8f,
                notes = "아일라의 대표작! 바닷바람과 피트의 조화가 완벽해요.",
                images = emptyList(),
                tags = listOf("아일라", "피티", "스모키"),
                likesCount = 42,
                commentsCount = 15,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 7200000 // 2시간 전
            ),
            WhiskeyCheckIn(
                id = "3",
                userId = "user3",
                username = "bourbonmaster",
                userDisplayName = "버번마스터",
                userProfileImageUrl = null,
                whiskeyId = "whiskey3",
                whiskeyName = "Woodford Reserve",
                whiskeyImageUrl = null,
                location = "부티크 위스키샵",
                rating = 4.2f,
                notes = "클래식한 버번의 맛. 바닐라와 캐러멜이 잘 느껴져요.",
                images = emptyList(),
                tags = listOf("버번", "바닐라", "캐러멜"),
                likesCount = 18,
                commentsCount = 5,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 10800000 // 3시간 전
            ),
            WhiskeyCheckIn(
                id = "4",
                userId = "user4",
                username = "japanesewhiskey",
                userDisplayName = "일본위스키러버",
                userProfileImageUrl = null,
                whiskeyId = "whiskey4",
                whiskeyName = "Hibiki 17",
                whiskeyImageUrl = null,
                location = "일식당",
                rating = 4.9f,
                notes = "일본 위스키의 정점! 정교하고 우아한 맛이 인상적이에요.",
                images = emptyList(),
                tags = listOf("일본위스키", "블렌디드", "우아함"),
                likesCount = 67,
                commentsCount = 23,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 14400000 // 4시간 전
            ),
            WhiskeyCheckIn(
                id = "5",
                userId = "user5",
                username = "ryelover",
                userDisplayName = "라이위스키러버",
                userProfileImageUrl = null,
                whiskeyId = "whiskey5",
                whiskeyName = "Sazerac Rye",
                whiskeyImageUrl = null,
                location = "칵테일바",
                rating = 4.0f,
                notes = "라이 위스키의 스파이시함이 좋아요. 칵테일 베이스로도 훌륭합니다.",
                images = emptyList(),
                tags = listOf("라이위스키", "스파이시", "칵테일"),
                likesCount = 31,
                commentsCount = 12,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 18000000 // 5시간 전
            )
        )
    }
    
    private fun generateMockUsers(): List<SocialUser> {
        return listOf(
            SocialUser(
                id = "user1",
                username = "whiskeylover1",
                displayName = "위스키러버",
                profileImageUrl = null,
                bio = "위스키를 사랑하는 사람입니다",
                stats = UserStats(
                    checkInsCount = 156,
                    followersCount = 1240,
                    followingCount = 890,
                    reviewsCount = 98,
                    favoritesCount = 45
                )
            ),
            SocialUser(
                id = "user2",
                username = "scotchfan",
                displayName = "스카치팬",
                profileImageUrl = null,
                bio = "스카치 위스키 전문가",
                stats = UserStats(
                    checkInsCount = 203,
                    followersCount = 890,
                    followingCount = 456,
                    reviewsCount = 145,
                    favoritesCount = 67
                )
            ),
            SocialUser(
                id = "user3",
                username = "bourbonmaster",
                displayName = "버번마스터",
                profileImageUrl = null,
                bio = "버번 위스키 컬렉터",
                stats = UserStats(
                    checkInsCount = 98,
                    followersCount = 567,
                    followingCount = 234,
                    reviewsCount = 67,
                    favoritesCount = 23
                )
            )
        )
    }
    
    private fun generateMockCurrentUser(): SocialUser {
        return SocialUser(
            id = "current_user",
            username = "spiritscribe_user",
            displayName = "SpiritScribe 사용자",
            profileImageUrl = null,
            bio = "위스키를 기록하고 취향을 증류하는 중입니다",
            isVerified = false,
            stats = UserStats(
                checkInsCount = 45,
                followersCount = 123,
                followingCount = 89,
                reviewsCount = 32,
                favoritesCount = 18
            )
        )
    }
    
    private fun generateMockTrendingWhiskies(): List<TrendingWhiskey> {
        return listOf(
            TrendingWhiskey(
                id = "trending1",
                name = "Macallan 18",
                distillery = "The Macallan",
                imageUrl = null,
                checkInsCount = 1247,
                averageRating = 4.6f,
                tags = listOf("싱글몰트", "프리미엄")
            ),
            TrendingWhiskey(
                id = "trending2",
                name = "Lagavulin 16",
                distillery = "Lagavulin",
                imageUrl = null,
                checkInsCount = 892,
                averageRating = 4.7f,
                tags = listOf("아일라", "스모키")
            ),
            TrendingWhiskey(
                id = "trending3",
                name = "Hibiki 17",
                distillery = "Suntory",
                imageUrl = null,
                checkInsCount = 1567,
                averageRating = 4.8f,
                tags = listOf("일본위스키", "블렌디드")
            )
        )
    }
}

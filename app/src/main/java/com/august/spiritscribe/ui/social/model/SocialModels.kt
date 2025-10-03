package com.august.spiritscribe.ui.social.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

// 사용자 정보
@Serializable
data class SocialUser(
    val id: String,
    val username: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val bio: String = "",
    val isVerified: Boolean = false,
    val stats: UserStats = UserStats(),
    val createdAt: Long = System.currentTimeMillis()
)

// 사용자 통계
@Serializable
data class UserStats(
    val checkInsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val reviewsCount: Int = 0,
    val favoritesCount: Int = 0
)

// 위스키 체크인 (포스트)
@Serializable
data class WhiskeyCheckIn(
    val id: String,
    val userId: String,
    val username: String,
    val userDisplayName: String,
    val userProfileImageUrl: String? = null,
    val whiskeyId: String,
    val whiskeyName: String,
    val whiskeyImageUrl: String? = null,
    val location: String? = null,
    val rating: Float? = null,
    val notes: String = "",
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 댓글
@Serializable
data class Comment(
    val id: String,
    val checkInId: String,
    val userId: String,
    val username: String,
    val userDisplayName: String,
    val userProfileImageUrl: String? = null,
    val content: String,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// 팔로우 관계
@Serializable
data class FollowRelationship(
    val id: String,
    val followerId: String,
    val followingId: String,
    val createdAt: Long = System.currentTimeMillis()
)

// 좋아요
@Serializable
data class Like(
    val id: String,
    val userId: String,
    val checkInId: String? = null,
    val commentId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// 소셜 피드 상태
data class SocialFeedState(
    val posts: List<WhiskeyCheckIn> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true
)

// 체크인 작성 상태
data class CheckInState(
    val selectedWhiskey: WhiskeyCheckIn? = null,
    val rating: Float = 0f,
    val notes: String = "",
    val location: String = "",
    val tags: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 사용자 프로필 상태
data class UserProfileState(
    val user: SocialUser? = null,
    val posts: List<WhiskeyCheckIn> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFollowing: Boolean = false,
    val isOwnProfile: Boolean = false
)

// 탐색 상태
data class ExploreState(
    val trendingPosts: List<WhiskeyCheckIn> = emptyList(),
    val suggestedUsers: List<SocialUser> = emptyList(),
    val trendingWhiskies: List<TrendingWhiskey> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 인기 위스키
@Serializable
data class TrendingWhiskey(
    val id: String,
    val name: String,
    val distillery: String,
    val imageUrl: String? = null,
    val checkInsCount: Int = 0,
    val averageRating: Float = 0f,
    val tags: List<String> = emptyList()
)

// 알림
@Serializable
data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val relatedUserId: String? = null,
    val relatedCheckInId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION
}

// 해시태그
@Serializable
data class Hashtag(
    val id: String,
    val name: String,
    val postsCount: Int = 0,
    val trendingScore: Float = 0f
)

// 위치 정보
@Serializable
data class Location(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val type: LocationType = LocationType.OTHER
)

@Serializable
enum class LocationType {
    WHISKEY_BAR,
    RESTAURANT,
    HOME,
    EVENT,
    OTHER
}

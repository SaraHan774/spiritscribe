package com.august.spiritscribe.data.local.entity

import androidx.room.*
import java.time.LocalDateTime

// 사용자 엔티티
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true),
        Index(value = ["created_at"])
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val location: String? = null,
    val website: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)

// 사용자 통계 엔티티
@Entity(
    tableName = "user_stats",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserStatsEntity(
    @PrimaryKey
    val userId: String,
    val checkInsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val reviewsCount: Int = 0,
    val favoritesCount: Int = 0,
    val totalRatingsCount: Int = 0,
    val averageRating: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)

// 팔로우 관계 엔티티
@Entity(
    tableName = "follows",
    indices = [
        Index(value = ["followerId"]),
        Index(value = ["followingId"]),
        Index(value = ["followerId", "followingId"], unique = true)
    ]
)
data class FollowEntity(
    @PrimaryKey
    val id: String,
    val followerId: String,
    val followingId: String,
    val createdAt: Long = System.currentTimeMillis()
)

// 체크인 엔티티
@Entity(
    tableName = "check_ins",
    indices = [
        Index(value = ["userId", "createdAt"]),
        Index(value = ["whiskeyId", "createdAt"]),
        Index(value = ["isPublic", "createdAt"]),
        Index(value = ["locationLat", "locationLng"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckInEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val whiskeyId: String,
    val location: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationType: LocationType = LocationType.OTHER,
    val rating: Double? = null,
    val notes: String? = null,
    val isPublic: Boolean = true,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 체크인 이미지 엔티티
@Entity(
    tableName = "check_in_images",
    indices = [
        Index(value = ["checkInId", "imageOrder"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckInImageEntity(
    @PrimaryKey
    val id: String,
    val checkInId: String,
    val imageUrl: String,
    val imageOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// 체크인 태그 엔티티
@Entity(
    tableName = "check_in_tags",
    indices = [
        Index(value = ["checkInId", "tagName"], unique = true),
        Index(value = ["tagName"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckInTagEntity(
    @PrimaryKey
    val id: String,
    val checkInId: String,
    val tagName: String,
    val createdAt: Long = System.currentTimeMillis()
)

// 좋아요 엔티티
@Entity(
    tableName = "likes",
    indices = [
        Index(value = ["userId", "createdAt"]),
        Index(value = ["userId", "checkInId"], unique = true),
        Index(value = ["userId", "commentId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LikeEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val checkInId: String? = null,
    val commentId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// 댓글 엔티티
@Entity(
    tableName = "comments",
    indices = [
        Index(value = ["checkInId", "createdAt"]),
        Index(value = ["userId", "createdAt"]),
        Index(value = ["parentCommentId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentCommentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val checkInId: String,
    val userId: String,
    val parentCommentId: String? = null,
    val content: String,
    val likesCount: Int = 0,
    val repliesCount: Int = 0,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 공유 엔티티
@Entity(
    tableName = "shares",
    indices = [
        Index(value = ["userId", "createdAt"]),
        Index(value = ["checkInId", "createdAt"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShareEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val checkInId: String,
    val platform: SharePlatform = SharePlatform.INTERNAL,
    val createdAt: Long = System.currentTimeMillis()
)

// 알림 엔티티
@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["userId", "isRead", "createdAt"]),
        Index(value = ["createdAt"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedCheckInId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedCommentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val relatedUserId: String? = null,
    val relatedCheckInId: String? = null,
    val relatedCommentId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// 해시태그 엔티티
@Entity(
    tableName = "hashtags",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["trendingScore"]),
        Index(value = ["postsCount"])
    ]
)
data class HashtagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val postsCount: Int = 0,
    val trendingScore: Double = 0.0,
    val lastUsedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

// 체크인-해시태그 연결 엔티티
@Entity(
    tableName = "check_in_hashtags",
    indices = [
        Index(value = ["checkInId", "hashtagId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = CheckInEntity::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HashtagEntity::class,
            parentColumns = ["id"],
            childColumns = ["hashtagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckInHashtagEntity(
    @PrimaryKey
    val id: String,
    val checkInId: String,
    val hashtagId: String,
    val createdAt: Long = System.currentTimeMillis()
)

// 위치 정보 엔티티
@Entity(
    tableName = "locations",
    indices = [
        Index(value = ["latitude", "longitude"]),
        Index(value = ["city", "country"]),
        Index(value = ["type"]),
        Index(value = ["rating"])
    ]
)
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val type: LocationType = LocationType.OTHER,
    val rating: Double? = null,
    val checkInsCount: Int = 0,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 열거형 타입들
enum class LocationType {
    WHISKEY_BAR,
    RESTAURANT,
    HOME,
    EVENT,
    OTHER
}

enum class SharePlatform {
    INTERNAL,
    FACEBOOK,
    TWITTER,
    INSTAGRAM,
    KAKAO
}

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION,
    SHARE,
    FEATURED
}

// 복합 엔티티 (JOIN 결과를 위한)
data class CheckInWithDetails(
    @Embedded val checkIn: CheckInEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "checkInId"
    )
    val images: List<CheckInImageEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "checkInId"
    )
    val tags: List<CheckInTagEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "checkInId"
    )
    val likes: List<LikeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "checkInId"
    )
    val comments: List<CommentEntity>
)

data class UserWithStats(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val stats: UserStatsEntity
)

data class CommentWithDetails(
    @Embedded val comment: CommentEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "commentId"
    )
    val likes: List<LikeEntity>
)

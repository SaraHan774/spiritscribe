package com.august.spiritscribe.data.local.dao

import androidx.room.*
import com.august.spiritscribe.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// 사용자 관련 DAO
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE display_name LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users ORDER BY created_at DESC LIMIT :limit")
    fun getRecentUsers(limit: Int = 20): Flow<List<UserEntity>>
}

// 사용자 통계 DAO
@Dao
interface UserStatsDao {
    
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStats(userId: String): UserStatsEntity?
    
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStatsFlow(userId: String): Flow<UserStatsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)
    
    @Update
    suspend fun updateUserStats(stats: UserStatsEntity)
    
    @Query("UPDATE user_stats SET checkInsCount = checkInsCount + 1 WHERE userId = :userId")
    suspend fun incrementCheckInsCount(userId: String)
    
    @Query("UPDATE user_stats SET followersCount = followersCount + 1 WHERE userId = :userId")
    suspend fun incrementFollowersCount(userId: String)
    
    @Query("UPDATE user_stats SET followersCount = followersCount - 1 WHERE userId = :userId")
    suspend fun decrementFollowersCount(userId: String)
    
    @Query("UPDATE user_stats SET followingCount = followingCount + 1 WHERE userId = :userId")
    suspend fun incrementFollowingCount(userId: String)
    
    @Query("UPDATE user_stats SET followingCount = followingCount - 1 WHERE userId = :userId")
    suspend fun decrementFollowingCount(userId: String)
}

// 팔로우 관계 DAO
@Dao
interface FollowDao {
    
    @Query("SELECT * FROM follows WHERE followerId = :userId ORDER BY createdAt DESC")
    fun getUserFollowing(userId: String): Flow<List<FollowEntity>>
    
    @Query("SELECT * FROM follows WHERE followingId = :userId ORDER BY createdAt DESC")
    fun getUserFollowers(userId: String): Flow<List<FollowEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followingId = :followingId)")
    suspend fun isFollowing(followerId: String, followingId: String): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followingId = :followingId)")
    fun isFollowingFlow(followerId: String, followingId: String): Flow<Boolean>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)
    
    @Delete
    suspend fun deleteFollow(follow: FollowEntity)
    
    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun unfollow(followerId: String, followingId: String)
    
    @Query("SELECT u.* FROM users u INNER JOIN follows f ON u.id = f.followingId WHERE f.followerId = :userId ORDER BY f.createdAt DESC")
    fun getFollowingUsers(userId: String): Flow<List<UserEntity>>
    
    @Query("SELECT u.* FROM users u INNER JOIN follows f ON u.id = f.followerId WHERE f.followingId = :userId ORDER BY f.createdAt DESC")
    fun getFollowerUsers(userId: String): Flow<List<UserEntity>>
}

// 체크인 DAO
@Dao
interface CheckInDao {
    
    @Transaction
    @Query("SELECT * FROM check_ins WHERE id = :checkInId")
    suspend fun getCheckInWithDetails(checkInId: String): CheckInWithDetails?
    
    @Transaction
    @Query("SELECT * FROM check_ins WHERE isPublic = 1 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getPublicCheckIns(limit: Int = 20, offset: Int = 0): Flow<List<CheckInWithDetails>>
    
    @Transaction
    @Query("""
        SELECT * FROM check_ins 
        WHERE userId IN (
            SELECT followingId FROM follows WHERE followerId = :userId
        ) 
        AND isPublic = 1 
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getUserFeed(userId: String, limit: Int = 20, offset: Int = 0): Flow<List<CheckInWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getUserCheckIns(userId: String, limit: Int = 20, offset: Int = 0): Flow<List<CheckInWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM check_ins WHERE whiskeyId = :whiskeyId AND isPublic = 1 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getWhiskeyCheckIns(whiskeyId: String, limit: Int = 20, offset: Int = 0): Flow<List<CheckInWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM check_ins WHERE isFeatured = 1 AND isPublic = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getFeaturedCheckIns(limit: Int = 10): Flow<List<CheckInWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long
    
    @Update
    suspend fun updateCheckIn(checkIn: CheckInEntity)
    
    @Delete
    suspend fun deleteCheckIn(checkIn: CheckInEntity)
    
    @Query("SELECT COUNT(*) FROM check_ins WHERE userId = :userId")
    suspend fun getCheckInCount(userId: String): Int
    
    @Query("SELECT AVG(rating) FROM check_ins WHERE whiskeyId = :whiskeyId AND rating IS NOT NULL")
    suspend fun getWhiskeyAverageRating(whiskeyId: String): Double?
    
    @Query("SELECT COUNT(*) FROM check_ins WHERE whiskeyId = :whiskeyId")
    suspend fun getWhiskeyCheckInCount(whiskeyId: String): Int
    
    // 위치 기반 검색
    @Transaction
    @Query("""
        SELECT * FROM check_ins 
        WHERE locationLat BETWEEN :minLat AND :maxLat 
        AND locationLng BETWEEN :minLng AND :maxLng 
        AND isPublic = 1 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getCheckInsByLocation(
        minLat: Double, 
        maxLat: Double, 
        minLng: Double, 
        maxLng: Double, 
        limit: Int = 50
    ): Flow<List<CheckInWithDetails>>
}

// 체크인 이미지 DAO
@Dao
interface CheckInImageDao {
    
    @Query("SELECT * FROM check_in_images WHERE checkInId = :checkInId ORDER BY imageOrder")
    suspend fun getCheckInImages(checkInId: String): List<CheckInImageEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInImage(image: CheckInImageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInImages(images: List<CheckInImageEntity>)
    
    @Delete
    suspend fun deleteCheckInImage(image: CheckInImageEntity)
    
    @Query("DELETE FROM check_in_images WHERE checkInId = :checkInId")
    suspend fun deleteCheckInImages(checkInId: String)
}

// 체크인 태그 DAO
@Dao
interface CheckInTagDao {
    
    @Query("SELECT * FROM check_in_tags WHERE checkInId = :checkInId")
    suspend fun getCheckInTags(checkInId: String): List<CheckInTagEntity>
    
    @Query("SELECT DISTINCT tagName FROM check_in_tags ORDER BY tagName")
    fun getAllTags(): Flow<List<String>>
    
    @Query("SELECT tagName, COUNT(*) as count FROM check_in_tags GROUP BY tagName ORDER BY count DESC LIMIT :limit")
    fun getPopularTags(limit: Int = 20): Flow<List<TagCount>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInTag(tag: CheckInTagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInTags(tags: List<CheckInTagEntity>)
    
    @Delete
    suspend fun deleteCheckInTag(tag: CheckInTagEntity)
    
    @Query("DELETE FROM check_in_tags WHERE checkInId = :checkInId")
    suspend fun deleteCheckInTags(checkInId: String)
    
    // 태그로 체크인 검색
    @Transaction
    @Query("""
        SELECT DISTINCT c.* FROM check_ins c 
        INNER JOIN check_in_tags t ON c.id = t.checkInId 
        WHERE t.tagName = :tagName AND c.isPublic = 1 
        ORDER BY c.createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getCheckInsByTag(tagName: String, limit: Int = 20, offset: Int = 0): Flow<List<CheckInWithDetails>>
}

data class TagCount(
    val tagName: String,
    val count: Int
)

// 좋아요 DAO
@Dao
interface LikeDao {
    
    @Query("SELECT * FROM likes WHERE checkInId = :checkInId AND userId = :userId")
    suspend fun getUserLikeForCheckIn(userId: String, checkInId: String): LikeEntity?
    
    @Query("SELECT * FROM likes WHERE commentId = :commentId AND userId = :userId")
    suspend fun getUserLikeForComment(userId: String, commentId: String): LikeEntity?
    
    @Query("SELECT COUNT(*) FROM likes WHERE checkInId = :checkInId")
    suspend fun getCheckInLikesCount(checkInId: String): Int
    
    @Query("SELECT COUNT(*) FROM likes WHERE commentId = :commentId")
    suspend fun getCommentLikesCount(commentId: String): Int
    
    @Query("SELECT * FROM likes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserLikes(userId: String): Flow<List<LikeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeEntity)
    
    @Delete
    suspend fun deleteLike(like: LikeEntity)
    
    @Query("DELETE FROM likes WHERE userId = :userId AND checkInId = :checkInId")
    suspend fun unlikeCheckIn(userId: String, checkInId: String)
    
    @Query("DELETE FROM likes WHERE userId = :userId AND commentId = :commentId")
    suspend fun unlikeComment(userId: String, commentId: String)
}

// 댓글 DAO
@Dao
interface CommentDao {
    
    @Transaction
    @Query("SELECT * FROM comments WHERE checkInId = :checkInId AND parentCommentId IS NULL ORDER BY createdAt ASC")
    fun getCheckInComments(checkInId: String): Flow<List<CommentWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM comments WHERE parentCommentId = :parentCommentId ORDER BY createdAt ASC")
    fun getCommentReplies(parentCommentId: String): Flow<List<CommentWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentWithDetails(commentId: String): CommentWithDetails?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long
    
    @Update
    suspend fun updateComment(comment: CommentEntity)
    
    @Delete
    suspend fun deleteComment(comment: CommentEntity)
    
    @Query("UPDATE comments SET isDeleted = 1 WHERE id = :commentId")
    suspend fun softDeleteComment(commentId: String)
    
    @Query("UPDATE comments SET repliesCount = repliesCount + 1 WHERE id = :parentCommentId")
    suspend fun incrementRepliesCount(parentCommentId: String)
    
    @Query("UPDATE comments SET repliesCount = repliesCount - 1 WHERE id = :parentCommentId")
    suspend fun decrementRepliesCount(parentCommentId: String)
    
    @Query("SELECT COUNT(*) FROM comments WHERE checkInId = :checkInId AND isDeleted = 0")
    suspend fun getCheckInCommentsCount(checkInId: String): Int
}

// 공유 DAO
@Dao
interface ShareDao {
    
    @Query("SELECT * FROM shares WHERE checkInId = :checkInId ORDER BY createdAt DESC")
    fun getCheckInShares(checkInId: String): Flow<List<ShareEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShare(share: ShareEntity)
    
    @Query("SELECT COUNT(*) FROM shares WHERE checkInId = :checkInId")
    suspend fun getCheckInSharesCount(checkInId: String): Int
}

// 알림 DAO
@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getUserNotifications(userId: String, limit: Int = 50, offset: Int = 0): Flow<List<NotificationEntity>>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadNotificationsCount(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationsCountFlow(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
    
    @Update
    suspend fun updateNotification(notification: NotificationEntity)
    
    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: String)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)
    
    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    suspend fun deleteOldNotifications(cutoffTime: Long)
}

// 해시태그 DAO
@Dao
interface HashtagDao {
    
    @Query("SELECT * FROM hashtags WHERE name = :name")
    suspend fun getHashtagByName(name: String): HashtagEntity?
    
    @Query("SELECT * FROM hashtags ORDER BY trendingScore DESC LIMIT :limit")
    fun getTrendingHashtags(limit: Int = 20): Flow<List<HashtagEntity>>
    
    @Query("SELECT * FROM hashtags WHERE name LIKE '%' || :query || '%' ORDER BY postsCount DESC LIMIT :limit")
    fun searchHashtags(query: String, limit: Int = 10): Flow<List<HashtagEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHashtag(hashtag: HashtagEntity)
    
    @Update
    suspend fun updateHashtag(hashtag: HashtagEntity)
    
    @Query("UPDATE hashtags SET postsCount = postsCount + 1, lastUsedAt = :timestamp WHERE name = :name")
    suspend fun incrementHashtagUsage(name: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE hashtags SET postsCount = postsCount - 1 WHERE name = :name")
    suspend fun decrementHashtagUsage(name: String)
}

// 체크인-해시태그 연결 DAO
@Dao
interface CheckInHashtagDao {
    
    @Query("SELECT h.* FROM hashtags h INNER JOIN check_in_hashtags ch ON h.id = ch.hashtagId WHERE ch.checkInId = :checkInId")
    suspend fun getCheckInHashtags(checkInId: String): List<HashtagEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInHashtag(checkInHashtag: CheckInHashtagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInHashtags(checkInHashtags: List<CheckInHashtagEntity>)
    
    @Delete
    suspend fun deleteCheckInHashtag(checkInHashtag: CheckInHashtagEntity)
    
    @Query("DELETE FROM check_in_hashtags WHERE checkInId = :checkInId")
    suspend fun deleteCheckInHashtags(checkInId: String)
}

// 위치 정보 DAO
@Dao
interface LocationDao {
    
    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: String): LocationEntity?
    
    @Query("SELECT * FROM locations WHERE name = :name AND city = :city")
    suspend fun getLocationByNameAndCity(name: String, city: String): LocationEntity?
    
    @Query("SELECT * FROM locations WHERE type = :type ORDER BY checkInsCount DESC LIMIT :limit")
    fun getLocationsByType(type: LocationType, limit: Int = 20): Flow<List<LocationEntity>>
    
    @Query("""
        SELECT * FROM locations 
        WHERE latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLng AND :maxLng 
        ORDER BY checkInsCount DESC 
        LIMIT :limit
    """)
    fun getLocationsByBounds(
        minLat: Double, 
        maxLat: Double, 
        minLng: Double, 
        maxLng: Double, 
        limit: Int = 50
    ): Flow<List<LocationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Query("UPDATE locations SET checkInsCount = checkInsCount + 1 WHERE id = :locationId")
    suspend fun incrementLocationCheckIns(locationId: String)
    
    @Query("UPDATE locations SET checkInsCount = checkInsCount - 1 WHERE id = :locationId")
    suspend fun decrementLocationCheckIns(locationId: String)
}

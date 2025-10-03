package com.august.spiritscribe.data.repository

import com.august.spiritscribe.data.local.dao.CheckInDao
import com.august.spiritscribe.data.local.dao.CheckInHashtagDao
import com.august.spiritscribe.data.local.dao.CheckInImageDao
import com.august.spiritscribe.data.local.dao.CheckInTagDao
import com.august.spiritscribe.data.local.dao.CommentDao
import com.august.spiritscribe.data.local.dao.FollowDao
import com.august.spiritscribe.data.local.dao.HashtagDao
import com.august.spiritscribe.data.local.dao.LikeDao
import com.august.spiritscribe.data.local.dao.LocationDao
import com.august.spiritscribe.data.local.dao.NotificationDao
import com.august.spiritscribe.data.local.dao.ShareDao
import com.august.spiritscribe.data.local.dao.UserDao
import com.august.spiritscribe.data.local.dao.UserStatsDao
import com.august.spiritscribe.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// 소셜 기능을 위한 Repository 인터페이스
interface SocialRepository {
    
    // === 사용자 관련 ===
    suspend fun getCurrentUser(): UserEntity?
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun getUserByUsername(username: String): UserEntity?
    suspend fun searchUsers(query: String): Flow<List<UserEntity>>
    suspend fun updateUserProfile(user: UserEntity)
    suspend fun getUserStats(userId: String): UserStatsEntity?
    
    // === 팔로우 관련 ===
    fun getUserFollowers(userId: String): Flow<List<UserEntity>>
    fun getUserFollowing(userId: String): Flow<List<UserEntity>>
    suspend fun followUser(followerId: String, followingId: String)
    suspend fun unfollowUser(followerId: String, followingId: String)
    suspend fun isFollowing(followerId: String, followingId: String): Boolean
    fun isFollowingFlow(followerId: String, followingId: String): Flow<Boolean>
    
    // === 체크인 관련 ===
    suspend fun createCheckIn(checkIn: CheckInEntity): String
    suspend fun getCheckInById(checkInId: String): CheckInWithDetails?
    fun getUserFeed(userId: String): Flow<List<CheckInWithDetails>>
    fun getPublicFeed(): Flow<List<CheckInWithDetails>>
    fun getUserCheckIns(userId: String): Flow<List<CheckInWithDetails>>
    fun getWhiskeyCheckIns(whiskeyId: String): Flow<List<CheckInWithDetails>>
    fun getFeaturedCheckIns(): Flow<List<CheckInWithDetails>>
    suspend fun deleteCheckIn(checkInId: String)
    suspend fun updateCheckIn(checkIn: CheckInEntity)
    
    // === 좋아요 관련 ===
    suspend fun likeCheckIn(userId: String, checkInId: String)
    suspend fun unlikeCheckIn(userId: String, checkInId: String)
    suspend fun likeComment(userId: String, commentId: String)
    suspend fun unlikeComment(userId: String, commentId: String)
    suspend fun isCheckInLiked(userId: String, checkInId: String): Boolean
    suspend fun isCommentLiked(userId: String, commentId: String): Boolean
    
    // === 댓글 관련 ===
    suspend fun addComment(comment: CommentEntity): String
    suspend fun getCheckInComments(checkInId: String): Flow<List<CommentWithDetails>>
    suspend fun getCommentReplies(commentId: String): Flow<List<CommentWithDetails>>
    suspend fun deleteComment(commentId: String)
    suspend fun updateComment(comment: CommentEntity)
    
    // === 공유 관련 ===
    suspend fun shareCheckIn(userId: String, checkInId: String, platform: SharePlatform)
    suspend fun getCheckInSharesCount(checkInId: String): Int
    
    // === 알림 관련 ===
    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>>
    suspend fun getUnreadNotificationsCount(userId: String): Int
    suspend fun markNotificationAsRead(notificationId: String)
    suspend fun markAllNotificationsAsRead(userId: String)
    suspend fun createNotification(notification: NotificationEntity)
    
    // === 해시태그 관련 ===
    suspend fun getTrendingHashtags(): Flow<List<HashtagEntity>>
    suspend fun searchHashtags(query: String): Flow<List<HashtagEntity>>
    suspend fun getCheckInsByHashtag(tagName: String): Flow<List<CheckInWithDetails>>
    
    // === 위치 관련 ===
    suspend fun getLocationsByType(type: LocationType): Flow<List<LocationEntity>>
    suspend fun getLocationsByBounds(
        minLat: Double, 
        maxLat: Double, 
        minLng: Double, 
        maxLng: Double
    ): Flow<List<LocationEntity>>
    suspend fun createOrUpdateLocation(location: LocationEntity): String
    
    // === 검색 관련 ===
    suspend fun searchCheckIns(query: String): Flow<List<CheckInWithDetails>>

    // === 통계 관련 ===
    suspend fun updateUserStats(userId: String)
    suspend fun updateWhiskeyStats(whiskeyId: String)
    suspend fun updateHashtagStats(tagName: String)
}

// Repository 구현체
class SocialRepositoryImpl(
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val followDao: FollowDao,
    private val checkInDao: CheckInDao,
    private val checkInImageDao: CheckInImageDao,
    private val checkInTagDao: CheckInTagDao,
    private val likeDao: LikeDao,
    private val commentDao: CommentDao,
    private val shareDao: ShareDao,
    private val notificationDao: NotificationDao,
    private val hashtagDao: HashtagDao,
    private val checkInHashtagDao: CheckInHashtagDao,
    private val locationDao: LocationDao,
    private val currentUserId: String // 현재 로그인한 사용자 ID
) : SocialRepository {
    
    // === 사용자 관련 ===
    override suspend fun getCurrentUser(): UserEntity? {
        return userDao.getUserById(currentUserId)
    }
    
    override suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
    
    override suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }
    
    override suspend fun searchUsers(query: String): Flow<List<UserEntity>> {
        return userDao.searchUsers(query)
    }
    
    override suspend fun updateUserProfile(user: UserEntity) {
        userDao.updateUser(user)
    }
    
    override suspend fun getUserStats(userId: String): UserStatsEntity? {
        return userStatsDao.getUserStats(userId)
    }
    
    // === 팔로우 관련 ===
    override fun getUserFollowers(userId: String): Flow<List<UserEntity>> {
        return followDao.getFollowerUsers(userId)
    }
    
    override fun getUserFollowing(userId: String): Flow<List<UserEntity>> {
        return followDao.getFollowingUsers(userId)
    }
    
    override suspend fun followUser(followerId: String, followingId: String) {
        val follow = FollowEntity(
            id = generateId(),
            followerId = followerId,
            followingId = followingId
        )
        followDao.insertFollow(follow)
        
        // 통계 업데이트
        userStatsDao.incrementFollowersCount(followingId)
        userStatsDao.incrementFollowingCount(followerId)
        
        // 알림 생성
        val notification = NotificationEntity(
            id = generateId(),
            userId = followingId,
            type = NotificationType.FOLLOW,
            title = "새로운 팔로워",
            message = "${getCurrentUser()?.displayName ?: "사용자"}가 당신을 팔로우하기 시작했습니다.",
            relatedUserId = followerId
        )
        notificationDao.insertNotification(notification)
    }
    
    override suspend fun unfollowUser(followerId: String, followingId: String) {
        followDao.unfollow(followerId, followingId)
        
        // 통계 업데이트
        userStatsDao.decrementFollowersCount(followingId)
        userStatsDao.decrementFollowingCount(followerId)
    }
    
    override suspend fun isFollowing(followerId: String, followingId: String): Boolean {
        return followDao.isFollowing(followerId, followingId)
    }
    
    override fun isFollowingFlow(followerId: String, followingId: String): Flow<Boolean> {
        return followDao.isFollowingFlow(followerId, followingId)
    }
    
    // === 체크인 관련 ===
    override suspend fun createCheckIn(checkIn: CheckInEntity): String {
        val checkInId = generateId()
        val newCheckIn = checkIn.copy(id = checkInId)
        
        checkInDao.insertCheckIn(newCheckIn)
        
        // 통계 업데이트
        userStatsDao.incrementCheckInsCount(checkIn.userId)
        
        // 해시태그 처리
        if (checkIn.notes?.isNotEmpty() == true) {
            val hashtags = extractHashtags(checkIn.notes!!)
            hashtags.forEach { tag ->
                createOrUpdateHashtag(tag)
                val hashtagEntity = hashtagDao.getHashtagByName(tag)
                hashtagEntity?.let { hashtag ->
                    checkInHashtagDao.insertCheckInHashtag(
                        CheckInHashtagEntity(
                            id = generateId(),
                            checkInId = checkInId,
                            hashtagId = hashtag.id
                        )
                    )
                }
            }
        }
        
        return checkInId
    }
    
    override suspend fun getCheckInById(checkInId: String): CheckInWithDetails? {
        return checkInDao.getCheckInWithDetails(checkInId)
    }
    
    override fun getUserFeed(userId: String): Flow<List<CheckInWithDetails>> {
        return checkInDao.getUserFeed(userId)
    }
    
    override fun getPublicFeed(): Flow<List<CheckInWithDetails>> {
        return checkInDao.getPublicCheckIns()
    }
    
    override fun getUserCheckIns(userId: String): Flow<List<CheckInWithDetails>> {
        return checkInDao.getUserCheckIns(userId)
    }
    
    override fun getWhiskeyCheckIns(whiskeyId: String): Flow<List<CheckInWithDetails>> {
        return checkInDao.getWhiskeyCheckIns(whiskeyId)
    }
    
    override fun getFeaturedCheckIns(): Flow<List<CheckInWithDetails>> {
        return checkInDao.getFeaturedCheckIns()
    }
    
    override suspend fun deleteCheckIn(checkInId: String) {
        // 관련 데이터 삭제
        checkInImageDao.deleteCheckInImages(checkInId)
        checkInTagDao.deleteCheckInTags(checkInId)
        checkInHashtagDao.deleteCheckInHashtags(checkInId)
        likeDao.deleteLike(LikeEntity(id = "", userId = "", checkInId = checkInId))
        
        // 체크인 삭제
        val checkIn = checkInDao.getCheckInWithDetails(checkInId)
        checkIn?.let {
            checkInDao.deleteCheckIn(it.checkIn)
        }
    }
    
    override suspend fun updateCheckIn(checkIn: CheckInEntity) {
        checkInDao.updateCheckIn(checkIn)
    }
    
    // === 좋아요 관련 ===
    override suspend fun likeCheckIn(userId: String, checkInId: String) {
        val like = LikeEntity(
            id = generateId(),
            userId = userId,
            checkInId = checkInId
        )
        likeDao.insertLike(like)
        
        // 알림 생성
        val checkIn = checkInDao.getCheckInWithDetails(checkInId)
        checkIn?.let { checkInDetails ->
            if (checkInDetails.checkIn.userId != userId) {
                val notification = NotificationEntity(
                    id = generateId(),
                    userId = checkInDetails.checkIn.userId,
                    type = NotificationType.LIKE,
                    title = "좋아요",
                    message = "${getCurrentUser()?.displayName ?: "사용자"}가 당신의 체크인을 좋아합니다.",
                    relatedUserId = userId,
                    relatedCheckInId = checkInId
                )
                notificationDao.insertNotification(notification)
            }
        }
    }
    
    override suspend fun unlikeCheckIn(userId: String, checkInId: String) {
        likeDao.unlikeCheckIn(userId, checkInId)
    }
    
    override suspend fun likeComment(userId: String, commentId: String) {
        val like = LikeEntity(
            id = generateId(),
            userId = userId,
            commentId = commentId
        )
        likeDao.insertLike(like)
    }
    
    override suspend fun unlikeComment(userId: String, commentId: String) {
        likeDao.unlikeComment(userId, commentId)
    }
    
    override suspend fun isCheckInLiked(userId: String, checkInId: String): Boolean {
        return likeDao.getUserLikeForCheckIn(userId, checkInId) != null
    }
    
    override suspend fun isCommentLiked(userId: String, commentId: String): Boolean {
        return likeDao.getUserLikeForComment(userId, commentId) != null
    }
    
    // === 댓글 관련 ===
    override suspend fun addComment(comment: CommentEntity): String {
        val commentId = generateId()
        val newComment = comment.copy(id = commentId)
        
        commentDao.insertComment(newComment)
        
        // 대댓글인 경우 부모 댓글의 repliesCount 증가
        if (comment.parentCommentId != null) {
            commentDao.incrementRepliesCount(comment.parentCommentId!!)
        }
        
        // 알림 생성
        val checkIn = checkInDao.getCheckInWithDetails(comment.checkInId)
        checkIn?.let { checkInDetails ->
            if (checkInDetails.checkIn.userId != comment.userId) {
                val notification = NotificationEntity(
                    id = generateId(),
                    userId = checkInDetails.checkIn.userId,
                    type = NotificationType.COMMENT,
                    title = "새 댓글",
                    message = "${getCurrentUser()?.displayName ?: "사용자"}가 당신의 체크인에 댓글을 남겼습니다.",
                    relatedUserId = comment.userId,
                    relatedCheckInId = comment.checkInId,
                    relatedCommentId = commentId
                )
                notificationDao.insertNotification(notification)
            }
        }
        
        return commentId
    }
    
    override suspend fun getCheckInComments(checkInId: String): Flow<List<CommentWithDetails>> {
        return commentDao.getCheckInComments(checkInId)
    }
    
    override suspend fun getCommentReplies(commentId: String): Flow<List<CommentWithDetails>> {
        return commentDao.getCommentReplies(commentId)
    }
    
    override suspend fun deleteComment(commentId: String) {
        val comment = commentDao.getCommentWithDetails(commentId)
        comment?.let { commentDetails ->
            // 대댓글인 경우 부모 댓글의 repliesCount 감소
            if (commentDetails.comment.parentCommentId != null) {
                commentDao.decrementRepliesCount(commentDetails.comment.parentCommentId!!)
            }
        }
        
        commentDao.softDeleteComment(commentId)
    }
    
    override suspend fun updateComment(comment: CommentEntity) {
        commentDao.updateComment(comment)
    }
    
    // === 공유 관련 ===
    override suspend fun shareCheckIn(userId: String, checkInId: String, platform: SharePlatform) {
        val share = ShareEntity(
            id = generateId(),
            userId = userId,
            checkInId = checkInId,
            platform = platform
        )
        shareDao.insertShare(share)
        
        // 알림 생성 (내부 공유인 경우)
        if (platform == SharePlatform.INTERNAL) {
            val checkIn = checkInDao.getCheckInWithDetails(checkInId)
            checkIn?.let { checkInDetails ->
                if (checkInDetails.checkIn.userId != userId) {
                    val notification = NotificationEntity(
                        id = generateId(),
                        userId = checkInDetails.checkIn.userId,
                        type = NotificationType.SHARE,
                        title = "공유됨",
                        message = "${getCurrentUser()?.displayName ?: "사용자"}가 당신의 체크인을 공유했습니다.",
                        relatedUserId = userId,
                        relatedCheckInId = checkInId
                    )
                    notificationDao.insertNotification(notification)
                }
            }
        }
    }
    
    override suspend fun getCheckInSharesCount(checkInId: String): Int {
        return shareDao.getCheckInSharesCount(checkInId)
    }
    
    // === 알림 관련 ===
    override fun getUserNotifications(userId: String): Flow<List<NotificationEntity>> {
        return notificationDao.getUserNotifications(userId)
    }
    
    override suspend fun getUnreadNotificationsCount(userId: String): Int {
        return notificationDao.getUnreadNotificationsCount(userId)
    }
    
    override suspend fun markNotificationAsRead(notificationId: String) {
        notificationDao.markNotificationAsRead(notificationId)
    }
    
    override suspend fun markAllNotificationsAsRead(userId: String) {
        notificationDao.markAllNotificationsAsRead(userId)
    }
    
    override suspend fun createNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }
    
    // === 해시태그 관련 ===
    override suspend fun getTrendingHashtags(): Flow<List<HashtagEntity>> {
        return hashtagDao.getTrendingHashtags()
    }
    
    override suspend fun searchHashtags(query: String): Flow<List<HashtagEntity>> {
        return hashtagDao.searchHashtags(query)
    }
    
    override suspend fun getCheckInsByHashtag(tagName: String): Flow<List<CheckInWithDetails>> {
        return checkInTagDao.getCheckInsByTag(tagName)
    }
    
    // === 위치 관련 ===
    override suspend fun getLocationsByType(type: LocationType): Flow<List<LocationEntity>> {
        return locationDao.getLocationsByType(type)
    }
    
    override suspend fun getLocationsByBounds(
        minLat: Double, 
        maxLat: Double, 
        minLng: Double, 
        maxLng: Double
    ): Flow<List<LocationEntity>> {
        return locationDao.getLocationsByBounds(minLat, maxLat, minLng, maxLng)
    }
    
    override suspend fun createOrUpdateLocation(location: LocationEntity): String {
        val existingLocation = locationDao.getLocationByNameAndCity(location.name, location.city ?: "")
        return if (existingLocation != null) {
            val updatedLocation = existingLocation.copy(
                checkInsCount = existingLocation.checkInsCount + 1,
                updatedAt = System.currentTimeMillis()
            )
            locationDao.updateLocation(updatedLocation)
            existingLocation.id
        } else {
            val locationId = generateId()
            val newLocation = location.copy(id = locationId)
            locationDao.insertLocation(newLocation)
            locationId
        }
    }
    
    // === 검색 관련 ===
    override suspend fun searchCheckIns(query: String): Flow<List<CheckInWithDetails>> {
        // 해시태그 검색
        if (query.startsWith("#")) {
            val tagName = query.substring(1)
            return getCheckInsByHashtag(tagName)
        }
        
        // 일반 텍스트 검색 (노트 내용에서 검색)
        // TODO: FTS (Full Text Search) 구현
        return checkInDao.getPublicCheckIns()
    }

    // === 통계 관련 ===
    override suspend fun updateUserStats(userId: String) {
        val checkInsCount = checkInDao.getCheckInCount(userId)
        val existingStats = userStatsDao.getUserStats(userId)
        
        val updatedStats = (existingStats ?: UserStatsEntity(userId = userId)).copy(
            checkInsCount = checkInsCount,
            updatedAt = System.currentTimeMillis()
        )
        
        userStatsDao.insertUserStats(updatedStats)
    }
    
    override suspend fun updateWhiskeyStats(whiskeyId: String) {
        // TODO: 위스키 통계 업데이트 로직
    }
    
    override suspend fun updateHashtagStats(tagName: String) {
        // TODO: 해시태그 통계 업데이트 로직
    }
    
    // === 헬퍼 함수들 ===
    private fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }
    
    private fun extractHashtags(text: String): List<String> {
        val hashtagRegex = "#(\\w+)".toRegex()
        return hashtagRegex.findAll(text)
            .map { it.groupValues[1] }
            .toList()
    }
    
    private suspend fun createOrUpdateHashtag(tagName: String) {
        val existingHashtag = hashtagDao.getHashtagByName(tagName)
        if (existingHashtag != null) {
            hashtagDao.incrementHashtagUsage(tagName)
        } else {
            val newHashtag = HashtagEntity(
                id = generateId(),
                name = tagName,
                postsCount = 1
            )
            hashtagDao.insertHashtag(newHashtag)
        }
    }
}

package com.august.spiritscribe.ui.social.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.august.spiritscribe.ui.social.model.SocialUser
import com.august.spiritscribe.ui.social.model.WhiskeyCheckIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 소셜 스토리 (최근 체크인)
@Composable
fun SocialStories(
    modifier: Modifier = Modifier,
    onStoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = modifier.padding(top = 16.dp).fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 내 스토리 (체크인하기)
        item {
            StoryItem(
                isMyStory = true,
                userDisplayName = "내 스토리",
                profileImageUrl = null,
                onClick = { /* TODO: Navigate to check-in */ }
            )
        }
        
        // 다른 사용자들의 스토리 (더미 데이터)
        items(getMockStories()) { story ->
            StoryItem(
                isMyStory = false,
                userDisplayName = story.displayName,
                profileImageUrl = story.profileImageUrl,
                onClick = { onStoryClick(story.id) }
            )
        }
    }
}

@Composable
private fun StoryItem(
    isMyStory: Boolean,
    userDisplayName: String,
    profileImageUrl: String?,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isMyStory) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .padding(if (isMyStory) 16.dp else 2.dp)
        ) {
            if (isMyStory) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "체크인하기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = userDisplayName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = userDisplayName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.width(60.dp)
        )
    }
}

// 소셜 피드
@Composable
fun SocialFeed(
    posts: List<WhiskeyCheckIn>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onWhiskeyClick: (String) -> Unit
) {
    if (isLoading && posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            PostCard(
                post = post,
                onLikeClick = { onLikePost(post.id) },
                onCommentClick = { onCommentPost(post.id) },
                onUserClick = { onUserClick(post.userId) },
                onWhiskeyClick = { onWhiskeyClick(post.whiskeyId) }
            )
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

// 포스트 카드
@Composable
fun PostCard(
    post: WhiskeyCheckIn,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onUserClick: () -> Unit,
    onWhiskeyClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "post_card_scale"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 사용자 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onUserClick() },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    AsyncImage(
                        model = post.userProfileImageUrl,
                        contentDescription = post.userDisplayName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onUserClick() }
                    )
                    Text(
                        text = formatTimeAgo(post.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 위스키 정보 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        isPressed = true
                        onWhiskeyClick() 
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = post.whiskeyImageUrl,
                        contentDescription = post.whiskeyName,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = post.whiskeyName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "디스틸러리 정보",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        post.rating?.let { rating ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "평점",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f/5", rating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            // 위치 정보
            post.location?.let { location ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "위치",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // 노트
            if (post.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = post.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 태그
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(post.tags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 액션 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 좋아요 버튼
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLikeClick() }
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "좋아요",
                            tint = if (post.isLikedByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.likesCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // 댓글 버튼
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCommentClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "댓글",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.commentsCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // 공유 버튼
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "공유",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { /* TODO: 공유 기능 */ }
                )
            }
        }
    }
}

// 트렌딩 포스트 카드
@Composable
fun TrendingPostCard(
    post: WhiskeyCheckIn,
    onUserClick: () -> Unit,
    onWhiskeyClick: () -> Unit
) {
    // TODO: 트렌딩 포스트 카드 구현
    PostCard(
        post = post,
        onLikeClick = { },
        onCommentClick = { },
        onUserClick = onUserClick,
        onWhiskeyClick = onWhiskeyClick
    )
}

// 추천 사용자 카드
@Composable
fun SuggestedUserCard(
    user: SocialUser,
    onUserClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.profileImageUrl,
                contentDescription = user.displayName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${user.stats.checkInsCount} 체크인 • ${user.stats.followersCount} 팔로워",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Button(
                onClick = onFollowClick,
                modifier = Modifier.height(32.dp)
            ) {
                Text("팔로우", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// 프로필 헤더
@Composable
fun ProfileHeader(
    user: SocialUser?,
    isOwnProfile: Boolean,
    onEditProfile: () -> Unit
) {
    // TODO: 프로필 헤더 구현
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user?.profileImageUrl,
                contentDescription = user?.displayName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = user?.displayName ?: "사용자",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "@${user?.username ?: "username"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            if (user?.bio?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 통계
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("체크인", user?.stats?.checkInsCount ?: 0)
                StatItem("팔로워", user?.stats?.followersCount ?: 0)
                StatItem("팔로잉", user?.stats?.followingCount ?: 0)
            }
            
            if (isOwnProfile) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onEditProfile) {
                    Text("프로필 편집")
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// 사용자 포스트 카드
@Composable
fun UserPostCard(
    post: WhiskeyCheckIn,
    onPostClick: () -> Unit,
    onWhiskeyClick: () -> Unit
) {
    // TODO: 사용자 포스트 카드 구현
    PostCard(
        post = post,
        onLikeClick = { },
        onCommentClick = { },
        onUserClick = { },
        onWhiskeyClick = onWhiskeyClick
    )
}

// 유틸리티 함수들
private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "방금 전"
        diff < 3600_000 -> "${diff / 60_000}분 전"
        diff < 86400_000 -> "${diff / 3600_000}시간 전"
        diff < 604800_000 -> "${diff / 86400_000}일 전"
        else -> SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun getMockStories(): List<SocialUser> {
    return listOf(
        SocialUser(
            id = "1",
            username = "whiskeylover1",
            displayName = "위스키러버",
            profileImageUrl = null
        ),
        SocialUser(
            id = "2",
            username = "scotchfan",
            displayName = "스카치팬",
            profileImageUrl = null
        ),
        SocialUser(
            id = "3",
            username = "bourbonmaster",
            displayName = "버번마스터",
            profileImageUrl = null
        )
    )
}

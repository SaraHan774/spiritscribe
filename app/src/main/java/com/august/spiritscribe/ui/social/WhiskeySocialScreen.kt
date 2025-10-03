package com.august.spiritscribe.ui.social

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.august.spiritscribe.ui.social.components.*
import com.august.spiritscribe.ui.social.model.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WhiskeySocialScreen(
    modifier: Modifier = Modifier,
    viewModel: WhiskeySocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = SocialTab.values()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        // 상단 탭바
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { 
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tab.title) },
                )
            }
        }

        // HorizontalPager로 스와이프 가능한 콘텐츠
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
                when (page) {
                0 -> {
                    // 홈 피드
                    HomeFeedTab(
                        feedState = uiState.feedState,
                        onRefresh = { viewModel.refreshFeed() },
                        onLoadMore = { viewModel.loadMorePosts() },
                        onLikePost = { postId -> viewModel.likePost(postId) },
                        onCommentPost = { postId -> viewModel.navigateToComments(postId) },
                        onUserClick = { userId -> viewModel.navigateToUserProfile(userId) },
                        onWhiskeyClick = { whiskeyId -> viewModel.navigateToWhiskeyDetail(whiskeyId) }
                    )
                }
                1 -> {
                    // 탐색
                    ExploreTab(
                        exploreState = uiState.exploreState,
                        onRefresh = { viewModel.refreshExplore() },
                        onUserClick = { userId -> viewModel.navigateToUserProfile(userId) },
                        onWhiskeyClick = { whiskeyId -> viewModel.navigateToWhiskeyDetail(whiskeyId) }
                    )
                }
                2 -> {
                    // 체크인
                    CheckInTab(
                        onWhiskeyClick = { whiskeyId -> viewModel.navigateToWhiskeyDetail(whiskeyId) },
                        onCheckInClick = { viewModel.navigateToCreateCheckIn() }
                    )
                }
                3 -> {
                    // 프로필
                    ProfileTab(
                        profileState = uiState.profileState,
                        onRefresh = { viewModel.refreshProfile() },
                        onEditProfile = { viewModel.navigateToEditProfile() },
                        onPostClick = { postId -> viewModel.navigateToPostDetail(postId) },
                        onUserClick = { userId -> viewModel.navigateToUserProfile(userId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeFeedTab(
    feedState: SocialFeedState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onLikePost: (String) -> Unit,
    onCommentPost: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onWhiskeyClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 스토리 섹션
        SocialStories(
            modifier = Modifier.padding(vertical = 8.dp),
            onStoryClick = { userId -> onUserClick(userId) }
        )
        
        // 피드
        SocialFeed(
            posts = feedState.posts,
            isLoading = feedState.isLoading,
            isRefreshing = feedState.isRefreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
            onLikePost = onLikePost,
            onCommentPost = onCommentPost,
            onUserClick = onUserClick,
            onWhiskeyClick = onWhiskeyClick
        )
    }
}

@Composable
private fun ExploreTab(
    exploreState: com.august.spiritscribe.ui.social.model.ExploreState,
    onRefresh: () -> Unit,
    onUserClick: (String) -> Unit,
    onWhiskeyClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "인기 포스트",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(exploreState.trendingPosts) { post ->
            // 트렌딩 포스트 아이템
            TrendingPostCard(
                post = post,
                onUserClick = { onUserClick(post.userId) },
                onWhiskeyClick = { onWhiskeyClick(post.whiskeyId) }
            )
        }
        
        item {
            Text(
                text = "추천 사용자",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        items(exploreState.suggestedUsers) { user ->
            // 추천 사용자 아이템
            SuggestedUserCard(
                user = user,
                onUserClick = { onUserClick(user.id) },
                onFollowClick = { /* TODO: Follow user */ }
            )
        }
    }
}

@Composable
private fun CheckInTab(
    onWhiskeyClick: (String) -> Unit,
    onCheckInClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "지금 마시는 위스키를\n체크인해보세요!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        FloatingActionButton(
            onClick = onCheckInClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "체크인하기"
            )
        }
    }
}

@Composable
private fun ProfileTab(
    profileState: UserProfileState,
    onRefresh: () -> Unit,
    onEditProfile: () -> Unit,
    onPostClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 프로필 헤더
            ProfileHeader(
                user = profileState.user,
                isOwnProfile = profileState.isOwnProfile,
                onEditProfile = onEditProfile
            )
        }
        
        items(profileState.posts) { post ->
            // 사용자 포스트
            UserPostCard(
                post = post,
                onPostClick = { onPostClick(post.id) },
                onWhiskeyClick = { /* TODO: Handle whiskey click */ }
            )
        }
    }
}

enum class SocialTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("피드", Icons.Default.Home),
    EXPLORE("탐색", Icons.Default.Explore),
    CHECKIN("체크인", Icons.Default.Add),
    PROFILE("프로필", Icons.Default.Person)
}

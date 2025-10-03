# SpiritScribe 위스키 소셜 구현 가이드

## 📋 개요
SpiritScribe 위스키 소셜 커뮤니티 기능의 단계별 구현 가이드입니다.

## 🎯 구현 단계

### Phase 1: 기본 소셜 기능 (4주)
- [ ] 사용자 프로필 시스템
- [ ] 체크인 기능
- [ ] 기본 피드
- [ ] 좋아요/댓글 시스템

### Phase 2: 고급 기능 (4주)
- [ ] 팔로우 시스템
- [ ] 해시태그 시스템
- [ ] 알림 시스템
- [ ] 검색 기능

### Phase 3: 커뮤니티 강화 (4주)
- [ ] 실시간 기능
- [ ] 위치 기반 서비스
- [ ] 그룹/토픽 기능
- [ ] 전문가 계정

## 🛠️ 기술 스택

### 프론트엔드 (Android)
```
- Jetpack Compose
- Navigation Component
- Hilt (Dependency Injection)
- Room Database
- Retrofit + OkHttp
- Coil (이미지 로딩)
- Kotlin Coroutines + Flow
```

### 백엔드 (권장)
```
- Node.js + Express / Kotlin + Spring Boot
- PostgreSQL / MySQL
- Redis (캐싱)
- Firebase (푸시 알림, 실시간 DB)
- AWS S3 / CloudFlare (이미지 저장)
```

### 실시간 기능
```
- Firebase Firestore
- WebSocket (Socket.io)
- Firebase Cloud Messaging
```

## 📱 Android 구현 가이드

### 1. 데이터베이스 설정

#### Room Database 업데이트
```kotlin
@Database(
    entities = [
        // 기존 엔티티들...
        UserEntity::class,
        UserStatsEntity::class,
        CheckInEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        // ... 기타 소셜 엔티티들
    ],
    version = 5, // 버전 업데이트
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // 기존 DAO들...
    abstract fun userDao(): UserDao
    abstract fun checkInDao(): CheckInDao
    abstract fun commentDao(): CommentDao
    // ... 기타 소셜 DAO들
}
```

#### 마이그레이션 처리
```kotlin
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 소셜 관련 테이블 생성
        database.execSQL("""
            CREATE TABLE users (
                id TEXT PRIMARY KEY NOT NULL,
                username TEXT NOT NULL,
                display_name TEXT NOT NULL,
                email TEXT NOT NULL,
                profile_image_url TEXT,
                bio TEXT,
                is_verified INTEGER NOT NULL DEFAULT 0,
                is_private INTEGER NOT NULL DEFAULT 0,
                location TEXT,
                website TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                last_login_at INTEGER
            )
        """)
        
        // 기타 테이블들...
    }
}
```

### 2. Repository 패턴 구현

#### SocialRepository 구현
```kotlin
@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val checkInDao: CheckInDao,
    private val apiService: SocialApiService,
    private val currentUserId: String
) : SocialRepository {
    
    override suspend fun createCheckIn(checkIn: CheckInEntity): String {
        return try {
            // 로컬에 먼저 저장
            val localId = checkInDao.insertCheckIn(checkIn)
            
            // 서버에 동기화
            val response = apiService.createCheckIn(checkIn.toDto())
            response.id
        } catch (e: Exception) {
            // 오프라인 모드 처리
            throw e
        }
    }
    
    override fun getUserFeed(userId: String): Flow<List<CheckInWithDetails>> {
        return checkInDao.getUserFeed(userId)
            .onEach { checkIns ->
                // 백그라운드에서 서버 동기화
                syncFeedFromServer(userId)
            }
    }
    
    private suspend fun syncFeedFromServer(userId: String) {
        try {
            val serverCheckIns = apiService.getUserFeed(userId)
            // 로컬 DB 업데이트
            serverCheckIns.forEach { checkIn ->
                checkInDao.insertCheckIn(checkIn.toEntity())
            }
        } catch (e: Exception) {
            // 오프라인 모드 - 로컬 데이터만 사용
        }
    }
}
```

### 3. ViewModel 구현

#### WhiskeySocialViewModel 확장
```kotlin
@HiltViewModel
class WhiskeySocialViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WhiskeySocialUiState())
    val uiState: StateFlow<WhiskeySocialUiState> = _uiState.asStateFlow()
    
    fun createCheckIn(
        whiskeyId: String,
        rating: Float,
        notes: String,
        location: String?,
        images: List<Uri>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val checkIn = CheckInEntity(
                    id = "", // Repository에서 생성
                    userId = currentUserId,
                    whiskeyId = whiskeyId,
                    rating = rating.toDouble(),
                    notes = notes,
                    location = location,
                    isPublic = true
                )
                
                val checkInId = socialRepository.createCheckIn(checkIn)
                
                // 이미지 업로드
                images.forEach { imageUri ->
                    uploadCheckInImage(checkInId, imageUri)
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
                
                // 피드 새로고침
                refreshFeed()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "체크인 생성 실패: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun uploadCheckInImage(checkInId: String, imageUri: Uri) {
        // Firebase Storage 또는 AWS S3에 이미지 업로드
        val imageUrl = imageUploadService.uploadImage(imageUri)
        
        val imageEntity = CheckInImageEntity(
            id = UUID.randomUUID().toString(),
            checkInId = checkInId,
            imageUrl = imageUrl,
            imageOrder = 0
        )
        
        socialRepository.addCheckInImage(imageEntity)
    }
}
```

### 4. UI 컴포넌트 구현

#### 체크인 생성 화면
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCheckInScreen(
    whiskeyId: String,
    viewModel: WhiskeySocialViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(0f) }
    var notes by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("체크인") },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.createCheckIn(
                                whiskeyId = whiskeyId,
                                rating = rating,
                                notes = notes,
                                location = location,
                                images = selectedImages
                            )
                        },
                        enabled = !uiState.isLoading && rating > 0
                    ) {
                        Text("공유")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 위스키 정보
            WhiskeyInfoCard(whiskeyId = whiskeyId)
            
            // 평점
            RatingSection(
                rating = rating,
                onRatingChange = { rating = it }
            )
            
            // 노트
            NotesSection(
                notes = notes,
                onNotesChange = { notes = it }
            )
            
            // 위치
            LocationSection(
                location = location,
                onLocationChange = { location = it }
            )
            
            // 이미지
            ImageSection(
                images = selectedImages,
                onImagesChange = { selectedImages = it }
            )
        }
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
```

### 5. 네트워크 레이어 구현

#### API 서비스 정의
```kotlin
interface SocialApiService {
    
    @POST("check-ins")
    suspend fun createCheckIn(@Body checkIn: CheckInDto): CheckInDto
    
    @GET("check-ins")
    suspend fun getFeed(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<CheckInDto>
    
    @GET("check-ins/{checkInId}")
    suspend fun getCheckIn(@Path("checkInId") checkInId: String): CheckInDto
    
    @POST("check-ins/{checkInId}/like")
    suspend fun likeCheckIn(@Path("checkInId") checkInId: String)
    
    @DELETE("check-ins/{checkInId}/like")
    suspend fun unlikeCheckIn(@Path("checkInId") checkInId: String)
    
    @POST("check-ins/{checkInId}/comments")
    suspend fun addComment(
        @Path("checkInId") checkInId: String,
        @Body comment: CommentDto
    ): CommentDto
    
    @GET("check-ins/{checkInId}/comments")
    suspend fun getComments(
        @Path("checkInId") checkInId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<CommentDto>
    
    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): List<UserDto>
    
    @POST("users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String)
    
    @DELETE("users/{userId}/follow")
    suspend fun unfollowUser(@Path("userId") userId: String)
}
```

#### Retrofit 모듈 설정
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSocialApiService(retrofit: Retrofit): SocialApiService {
        return retrofit.create(SocialApiService::class.java)
    }
}
```

## 🔥 Firebase 통합

### 1. Firebase 설정
```kotlin
// build.gradle.kts (앱 레벨)
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}
```

### 2. FCM 푸시 알림
```kotlin
@AndroidEntryPoint
class SpiritScribeFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        when (remoteMessage.data["type"]) {
            "LIKE" -> handleLikeNotification(remoteMessage)
            "COMMENT" -> handleCommentNotification(remoteMessage)
            "FOLLOW" -> handleFollowNotification(remoteMessage)
        }
    }
    
    private fun handleLikeNotification(remoteMessage: RemoteMessage) {
        val checkInId = remoteMessage.data["checkInId"]
        val userId = remoteMessage.data["userId"]
        
        // 알림 표시
        showNotification(
            title = "새로운 좋아요",
            body = "누군가 당신의 체크인을 좋아합니다.",
            data = mapOf(
                "type" to "LIKE",
                "checkInId" to checkInId,
                "userId" to userId
            )
        )
    }
}
```

### 3. Firestore 실시간 동기화
```kotlin
class FirestoreSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    fun syncCheckIns(userId: String): Flow<List<CheckInEntity>> {
        return callbackFlow {
            val listener = firestore
                .collection("check_ins")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val checkIns = snapshot?.documents?.map { document ->
                        document.toObject(CheckInDto::class.java)?.toEntity()
                    }?.filterNotNull() ?: emptyList()
                    
                    trySend(checkIns)
                }
            
            awaitClose { listener.remove() }
        }
    }
    
    fun syncNotifications(userId: String): Flow<List<NotificationEntity>> {
        return callbackFlow {
            val listener = firestore
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val notifications = snapshot?.documents?.map { document ->
                        document.toObject(NotificationDto::class.java)?.toEntity()
                    }?.filterNotNull() ?: emptyList()
                    
                    trySend(notifications)
                }
            
            awaitClose { listener.remove() }
        }
    }
}
```

## 📊 성능 최적화

### 1. 이미지 최적화
```kotlin
@Composable
fun OptimizedAsyncImage(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
```

### 2. 피드 최적화
```kotlin
@Composable
fun OptimizedFeed(
    posts: List<CheckInWithDetails>,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = posts,
            key = { _, post -> post.checkIn.id }
        ) { index, post ->
            PostCard(
                post = post,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement() // 애니메이션 최적화
            )
            
            // 무한 스크롤
            if (index == posts.size - 1) {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
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
```

### 3. 오프라인 지원
```kotlin
class OfflineManager @Inject constructor(
    private val database: AppDatabase,
    private val networkMonitor: NetworkMonitor
) {
    
    suspend fun syncPendingData() {
        if (networkMonitor.isOnline()) {
            // 오프라인 중 생성된 데이터 동기화
            syncPendingCheckIns()
            syncPendingLikes()
            syncPendingComments()
        }
    }
    
    private suspend fun syncPendingCheckIns() {
        val pendingCheckIns = database.checkInDao().getPendingSyncCheckIns()
        
        pendingCheckIns.forEach { checkIn ->
            try {
                apiService.createCheckIn(checkIn.toDto())
                database.checkInDao().markAsSynced(checkIn.id)
            } catch (e: Exception) {
                // 동기화 실패 - 다음에 다시 시도
            }
        }
    }
}
```

## 🧪 테스트 전략

### 1. 단위 테스트
```kotlin
@RunWith(MockitoJUnitRunner::class)
class WhiskeySocialViewModelTest {
    
    @Mock
    private lateinit var socialRepository: SocialRepository
    
    @InjectMocks
    private lateinit var viewModel: WhiskeySocialViewModel
    
    @Test
    fun `체크인 생성 성공`() = runTest {
        // Given
        val checkIn = CheckInEntity(
            id = "test_id",
            userId = "user_123",
            whiskeyId = "whiskey_456",
            rating = 4.5,
            notes = "테스트 노트"
        )
        
        whenever(socialRepository.createCheckIn(any()))
            .thenReturn("test_id")
        
        // When
        viewModel.createCheckIn(
            whiskeyId = "whiskey_456",
            rating = 4.5f,
            notes = "테스트 노트",
            location = null,
            images = emptyList()
        )
        
        // Then
        verify(socialRepository).createCheckIn(any())
        assertTrue(viewModel.uiState.value.error == null)
    }
}
```

### 2. UI 테스트
```kotlin
@RunWith(AndroidJUnit4::class)
class WhiskeySocialScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun 체크인_생성_화면_표시() {
        composeTestRule.setContent {
            SpiritScribeTheme {
                CreateCheckInScreen(whiskeyId = "test_whiskey")
            }
        }
        
        composeTestRule.onNodeWithText("체크인").assertIsDisplayed()
        composeTestRule.onNodeWithText("공유").assertIsDisplayed()
    }
    
    @Test
    fun 평점_입력_테스트() {
        composeTestRule.setContent {
            SpiritScribeTheme {
                CreateCheckInScreen(whiskeyId = "test_whiskey")
            }
        }
        
        // 평점 슬라이더 조작
        composeTestRule.onNodeWithContentDescription("평점")
            .performGesture { swipeTo(0.8f) }
        
        composeTestRule.onNodeWithText("4.0").assertIsDisplayed()
    }
}
```

## 🚀 배포 전략

### 1. 단계적 배포
```
Week 1: 내부 테스트 (Alpha)
Week 2: 베타 테스터 (Beta)
Week 3: 제한적 출시 (10% 사용자)
Week 4: 전체 출시
```

### 2. 기능 플래그
```kotlin
@Singleton
class FeatureFlags @Inject constructor() {
    
    fun isSocialFeedEnabled(): Boolean {
        return BuildConfig.SOCIAL_FEED_ENABLED
    }
    
    fun isRealTimeEnabled(): Boolean {
        return BuildConfig.REAL_TIME_ENABLED
    }
    
    fun isLocationEnabled(): Boolean {
        return BuildConfig.LOCATION_ENABLED
    }
}
```

이 가이드를 따라 구현하면 SpiritScribe의 위스키 소셜 커뮤니티 기능을 성공적으로 구축할 수 있습니다.

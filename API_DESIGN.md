# SpiritScribe 위스키 소셜 API 설계 문서

## 📋 개요
SpiritScribe 위스키 소셜 커뮤니티 기능을 위한 REST API 설계 문서입니다.

## 🌐 Base URL
```
Production: https://api.spiritscribe.com/v1
Development: https://dev-api.spiritscribe.com/v1
```

## 🔐 인증
```http
Authorization: Bearer <JWT_TOKEN>
```

## 📊 API 엔드포인트

### 1. 사용자 관련 API

#### 사용자 프로필 조회
```http
GET /users/{userId}
GET /users/me (현재 사용자)
```

**응답 예시:**
```json
{
  "id": "user_123",
  "username": "whiskeylover",
  "displayName": "위스키러버",
  "email": "user@example.com",
  "profileImageUrl": "https://cdn.spiritscribe.com/profiles/user_123.jpg",
  "bio": "위스키를 사랑하는 사람입니다",
  "isVerified": false,
  "isPrivate": false,
  "location": "서울, 한국",
  "website": "https://whiskeyblog.com",
  "stats": {
    "checkInsCount": 156,
    "followersCount": 1240,
    "followingCount": 890,
    "reviewsCount": 98,
    "favoritesCount": 45,
    "averageRating": 4.2
  },
  "createdAt": "2024-01-15T10:30:00Z",
  "lastLoginAt": "2024-01-20T15:45:00Z"
}
```

#### 사용자 프로필 업데이트
```http
PUT /users/me
```

**요청 본문:**
```json
{
  "displayName": "새로운 이름",
  "bio": "업데이트된 자기소개",
  "location": "부산, 한국",
  "website": "https://newblog.com",
  "isPrivate": false
}
```

#### 사용자 검색
```http
GET /users/search?q={query}&limit=20&offset=0
```

### 2. 팔로우 관련 API

#### 사용자 팔로우
```http
POST /users/{userId}/follow
```

#### 사용자 언팔로우
```http
DELETE /users/{userId}/follow
```

#### 팔로워 목록 조회
```http
GET /users/{userId}/followers?limit=20&offset=0
```

#### 팔로잉 목록 조회
```http
GET /users/{userId}/following?limit=20&offset=0
```

#### 팔로우 상태 확인
```http
GET /users/{userId}/follow-status
```

### 3. 체크인 관련 API

#### 체크인 생성
```http
POST /check-ins
```

**요청 본문:**
```json
{
  "whiskeyId": "whiskey_456",
  "location": "강남 위스키바",
  "locationLat": 37.5665,
  "locationLng": 126.9780,
  "locationType": "WHISKEY_BAR",
  "rating": 4.5,
  "notes": "정말 부드럽고 복잡한 맛이에요. 오크 향이 특히 좋습니다!",
  "images": [
    "https://cdn.spiritscribe.com/checkins/image1.jpg",
    "https://cdn.spiritscribe.com/checkins/image2.jpg"
  ],
  "tags": ["싱글몰트", "스모키", "프리미엄"],
  "isPublic": true
}
```

#### 체크인 조회
```http
GET /check-ins/{checkInId}
GET /check-ins (피드 조회)
GET /check-ins/user/{userId} (사용자 체크인)
GET /check-ins/whiskey/{whiskeyId} (위스키 체크인)
```

**응답 예시:**
```json
{
  "id": "checkin_789",
  "userId": "user_123",
  "user": {
    "id": "user_123",
    "username": "whiskeylover",
    "displayName": "위스키러버",
    "profileImageUrl": "https://cdn.spiritscribe.com/profiles/user_123.jpg",
    "isVerified": false
  },
  "whiskeyId": "whiskey_456",
  "whiskey": {
    "id": "whiskey_456",
    "name": "Macallan 18",
    "distillery": "The Macallan",
    "imageUrl": "https://cdn.spiritscribe.com/whiskies/whiskey_456.jpg"
  },
  "location": "강남 위스키바",
  "locationLat": 37.5665,
  "locationLng": 126.9780,
  "locationType": "WHISKEY_BAR",
  "rating": 4.5,
  "notes": "정말 부드럽고 복잡한 맛이에요. 오크 향이 특히 좋습니다!",
  "images": [
    "https://cdn.spiritscribe.com/checkins/image1.jpg",
    "https://cdn.spiritscribe.com/checkins/image2.jpg"
  ],
  "tags": ["싱글몰트", "스모키", "프리미엄"],
  "likesCount": 24,
  "commentsCount": 8,
  "sharesCount": 3,
  "isLikedByMe": false,
  "isPublic": true,
  "isFeatured": false,
  "createdAt": "2024-01-20T18:30:00Z",
  "updatedAt": "2024-01-20T18:30:00Z"
}
```

#### 체크인 업데이트
```http
PUT /check-ins/{checkInId}
```

#### 체크인 삭제
```http
DELETE /check-ins/{checkInId}
```

### 4. 좋아요 관련 API

#### 체크인 좋아요
```http
POST /check-ins/{checkInId}/like
```

#### 체크인 좋아요 취소
```http
DELETE /check-ins/{checkInId}/like
```

#### 댓글 좋아요
```http
POST /comments/{commentId}/like
```

#### 댓글 좋아요 취소
```http
DELETE /comments/{commentId}/like
```

### 5. 댓글 관련 API

#### 댓글 추가
```http
POST /check-ins/{checkInId}/comments
```

**요청 본문:**
```json
{
  "content": "정말 좋은 위스키네요! 어디서 구매하셨나요?",
  "parentCommentId": null
}
```

#### 댓글 목록 조회
```http
GET /check-ins/{checkInId}/comments?limit=20&offset=0
```

#### 댓글 수정
```http
PUT /comments/{commentId}
```

#### 댓글 삭제
```http
DELETE /comments/{commentId}
```

### 6. 공유 관련 API

#### 체크인 공유
```http
POST /check-ins/{checkInId}/share
```

**요청 본문:**
```json
{
  "platform": "FACEBOOK"
}
```

### 7. 알림 관련 API

#### 알림 목록 조회
```http
GET /notifications?limit=50&offset=0&unreadOnly=false
```

#### 알림 읽음 처리
```http
PUT /notifications/{notificationId}/read
```

#### 모든 알림 읽음 처리
```http
PUT /notifications/read-all
```

#### 읽지 않은 알림 수 조회
```http
GET /notifications/unread-count
```

### 8. 해시태그 관련 API

#### 트렌딩 해시태그 조회
```http
GET /hashtags/trending?limit=20
```

#### 해시태그 검색
```http
GET /hashtags/search?q={query}&limit=10
```

#### 해시태그별 체크인 조회
```http
GET /hashtags/{tagName}/check-ins?limit=20&offset=0
```

### 9. 위치 관련 API

#### 위치별 체크인 조회
```http
GET /locations/check-ins?lat={latitude}&lng={longitude}&radius={radius}
```

#### 인기 장소 조회
```http
GET /locations/popular?type=WHISKEY_BAR&limit=20
```

### 10. 검색 관련 API

#### 통합 검색
```http
GET /search?q={query}&type=all&limit=20&offset=0
```

**type 옵션:**
- `all`: 모든 타입
- `users`: 사용자만
- `whiskies`: 위스키만
- `checkins`: 체크인만
- `hashtags`: 해시태그만

## 📱 Firebase Integration

### Firebase Cloud Messaging (FCM)
```json
{
  "to": "device_token",
  "notification": {
    "title": "새로운 좋아요",
    "body": "위스키러버가 당신의 체크인을 좋아합니다."
  },
  "data": {
    "type": "LIKE",
    "checkInId": "checkin_789",
    "userId": "user_123"
  }
}
```

### Firebase Storage
```
체크인 이미지: /check-ins/{checkInId}/images/{imageId}.jpg
프로필 이미지: /users/{userId}/profile.jpg
위스키 이미지: /whiskies/{whiskeyId}/image.jpg
```

### Firebase Firestore (실시간 데이터)
```javascript
// 실시간 피드 구독
db.collection('check_ins')
  .where('isPublic', '==', true)
  .orderBy('createdAt', 'desc')
  .limit(20)
  .onSnapshot((snapshot) => {
    // 실시간 업데이트 처리
  });

// 실시간 알림 구독
db.collection('notifications')
  .where('userId', '==', currentUserId)
  .where('isRead', '==', false)
  .onSnapshot((snapshot) => {
    // 새로운 알림 처리
  });
```

## 🔄 실시간 기능

### WebSocket 연결
```javascript
const ws = new WebSocket('wss://api.spiritscribe.com/ws');

// 좋아요 실시간 업데이트
ws.send(JSON.stringify({
  type: 'LIKE',
  checkInId: 'checkin_789',
  userId: 'user_123'
}));

// 새로운 댓글 실시간 알림
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.type === 'NEW_COMMENT') {
    // 댓글 UI 업데이트
  }
};
```

## 📊 캐싱 전략

### Redis 캐싱
```
사용자 프로필: user:{userId}
체크인 피드: feed:{userId}:page:{page}
트렌딩 해시태그: trending_hashtags
인기 체크인: featured_checkins
```

### CDN 활용
```
이미지 파일: CloudFlare CDN
정적 자산: AWS CloudFront
API 응답: Redis Cache
```

## 🔒 보안 고려사항

### Rate Limiting
```
일반 API: 1000 requests/hour
업로드 API: 100 requests/hour
검색 API: 500 requests/hour
```

### 데이터 검증
```json
{
  "rating": {
    "min": 1.0,
    "max": 5.0,
    "step": 0.1
  },
  "notes": {
    "maxLength": 1000
  },
  "images": {
    "maxCount": 5,
    "maxSize": "10MB",
    "allowedFormats": ["jpg", "jpeg", "png", "webp"]
  }
}
```

### 개인정보 보호
- 이메일 주소 마스킹
- 위치 정보 정밀도 조절
- 비공개 계정 데이터 보호
- GDPR 준수

## 📈 성능 최적화

### 데이터베이스 인덱싱
```sql
-- 복합 인덱스
CREATE INDEX idx_checkins_user_created ON check_ins(userId, createdAt DESC);
CREATE INDEX idx_checkins_public_created ON check_ins(isPublic, createdAt DESC);
CREATE INDEX idx_checkins_whiskey_created ON check_ins(whiskeyId, createdAt DESC);

-- 공간 인덱스 (위치 기반 검색)
CREATE SPATIAL INDEX idx_checkins_location ON check_ins(locationLat, locationLng);
```

### API 응답 최적화
```json
{
  "data": [...],
  "pagination": {
    "limit": 20,
    "offset": 0,
    "total": 1000,
    "hasMore": true
  },
  "metadata": {
    "responseTime": "45ms",
    "cached": true,
    "version": "1.0"
  }
}
```

이 API 설계는 SpiritScribe의 위스키 소셜 커뮤니티 기능을 위한 완전한 백엔드 시스템을 제공합니다.

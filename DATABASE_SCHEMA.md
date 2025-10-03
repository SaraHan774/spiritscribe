# SpiritScribe 위스키 소셜 데이터베이스 스키마

## 📋 개요
SpiritScribe 위스키 소셜 커뮤니티 기능을 위한 데이터베이스 스키마 설계 문서입니다.

## 🗄️ 데이터베이스 구조

### 1. 사용자 관련 테이블

#### `users` - 사용자 기본 정보
```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,                    -- UUID
    username VARCHAR(50) UNIQUE NOT NULL,          -- 고유 사용자명
    display_name VARCHAR(100) NOT NULL,            -- 표시명
    email VARCHAR(255) UNIQUE NOT NULL,            -- 이메일
    profile_image_url VARCHAR(500),                -- 프로필 이미지 URL
    bio TEXT,                                      -- 자기소개
    is_verified BOOLEAN DEFAULT FALSE,             -- 인증 사용자 여부
    is_private BOOLEAN DEFAULT FALSE,              -- 비공개 계정 여부
    location VARCHAR(100),                         -- 위치 정보
    website VARCHAR(255),                          -- 웹사이트
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);
```

#### `user_stats` - 사용자 통계
```sql
CREATE TABLE user_stats (
    user_id VARCHAR(36) PRIMARY KEY,
    check_ins_count INT DEFAULT 0,                 -- 체크인 수
    followers_count INT DEFAULT 0,                 -- 팔로워 수
    following_count INT DEFAULT 0,                 -- 팔로잉 수
    reviews_count INT DEFAULT 0,                   -- 리뷰 수
    favorites_count INT DEFAULT 0,                 -- 즐겨찾기 수
    total_ratings_count INT DEFAULT 0,             -- 총 평점 수
    average_rating DECIMAL(3,2) DEFAULT 0.00,      -- 평균 평점
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 2. 소셜 관계 테이블

#### `follows` - 팔로우 관계
```sql
CREATE TABLE follows (
    id VARCHAR(36) PRIMARY KEY,
    follower_id VARCHAR(36) NOT NULL,              -- 팔로우하는 사용자
    following_id VARCHAR(36) NOT NULL,             -- 팔로우받는 사용자
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id),
    INDEX idx_created_at (created_at)
);
```

#### `blocks` - 차단 관계
```sql
CREATE TABLE blocks (
    id VARCHAR(36) PRIMARY KEY,
    blocker_id VARCHAR(36) NOT NULL,               -- 차단하는 사용자
    blocked_id VARCHAR(36) NOT NULL,               -- 차단당하는 사용자
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_block (blocker_id, blocked_id),
    FOREIGN KEY (blocker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 3. 체크인 및 포스트 테이블

#### `check_ins` - 위스키 체크인
```sql
CREATE TABLE check_ins (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,                  -- 체크인한 사용자
    whiskey_id VARCHAR(36) NOT NULL,               -- 체크인한 위스키
    location VARCHAR(255),                         -- 체크인 위치
    location_lat DECIMAL(10,8),                    -- 위치 위도
    location_lng DECIMAL(11,8),                    -- 위치 경도
    location_type ENUM('WHISKEY_BAR', 'RESTAURANT', 'HOME', 'EVENT', 'OTHER') DEFAULT 'OTHER',
    rating DECIMAL(3,2),                           -- 평점 (1.0-5.0)
    notes TEXT,                                    -- 체크인 노트
    is_public BOOLEAN DEFAULT TRUE,                -- 공개 여부
    is_featured BOOLEAN DEFAULT FALSE,             -- 추천 체크인 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (whiskey_id) REFERENCES whiskeys(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_whiskey_created (whiskey_id, created_at),
    INDEX idx_public_created (is_public, created_at),
    INDEX idx_location (location_lat, location_lng)
);
```

#### `check_in_images` - 체크인 이미지
```sql
CREATE TABLE check_in_images (
    id VARCHAR(36) PRIMARY KEY,
    check_in_id VARCHAR(36) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_order INT DEFAULT 0,                     -- 이미지 순서
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    INDEX idx_check_in_order (check_in_id, image_order)
);
```

#### `check_in_tags` - 체크인 태그
```sql
CREATE TABLE check_in_tags (
    id VARCHAR(36) PRIMARY KEY,
    check_in_id VARCHAR(36) NOT NULL,
    tag_name VARCHAR(50) NOT NULL,                 -- 태그명
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_check_in_tag (check_in_id, tag_name),
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    INDEX idx_tag_name (tag_name)
);
```

### 4. 소셜 인터랙션 테이블

#### `likes` - 좋아요
```sql
CREATE TABLE likes (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,                  -- 좋아요한 사용자
    check_in_id VARCHAR(36),                       -- 체크인 ID (NULL 가능)
    comment_id VARCHAR(36),                        -- 댓글 ID (NULL 가능)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- check_in_id 또는 comment_id 중 하나는 반드시 있어야 함
    CONSTRAINT chk_like_target CHECK (
        (check_in_id IS NOT NULL AND comment_id IS NULL) OR 
        (check_in_id IS NULL AND comment_id IS NOT NULL)
    ),
    UNIQUE KEY unique_check_in_like (user_id, check_in_id),
    UNIQUE KEY unique_comment_like (user_id, comment_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at)
);
```

#### `comments` - 댓글
```sql
CREATE TABLE comments (
    id VARCHAR(36) PRIMARY KEY,
    check_in_id VARCHAR(36) NOT NULL,              -- 댓글이 달린 체크인
    user_id VARCHAR(36) NOT NULL,                  -- 댓글 작성자
    parent_comment_id VARCHAR(36),                 -- 대댓글인 경우 부모 댓글 ID
    content TEXT NOT NULL,                         -- 댓글 내용
    likes_count INT DEFAULT 0,                     -- 좋아요 수
    replies_count INT DEFAULT 0,                   -- 대댓글 수
    is_deleted BOOLEAN DEFAULT FALSE,              -- 삭제 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_check_in_created (check_in_id, created_at),
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_parent_comment (parent_comment_id)
);
```

#### `shares` - 공유
```sql
CREATE TABLE shares (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,                  -- 공유한 사용자
    check_in_id VARCHAR(36) NOT NULL,              -- 공유된 체크인
    platform ENUM('INTERNAL', 'FACEBOOK', 'TWITTER', 'INSTAGRAM', 'KAKAO') DEFAULT 'INTERNAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_check_in_created (check_in_id, created_at)
);
```

### 5. 알림 시스템 테이블

#### `notifications` - 알림
```sql
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,                  -- 알림 받을 사용자
    type ENUM('LIKE', 'COMMENT', 'FOLLOW', 'MENTION', 'SHARE', 'FEATURED') NOT NULL,
    title VARCHAR(255) NOT NULL,                   -- 알림 제목
    message TEXT NOT NULL,                         -- 알림 메시지
    related_user_id VARCHAR(36),                   -- 관련 사용자 ID
    related_check_in_id VARCHAR(36),               -- 관련 체크인 ID
    related_comment_id VARCHAR(36),                -- 관련 댓글 ID
    is_read BOOLEAN DEFAULT FALSE,                 -- 읽음 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (related_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (related_check_in_id) REFERENCES check_ins(id) ON DELETE SET NULL,
    FOREIGN KEY (related_comment_id) REFERENCES comments(id) ON DELETE SET NULL,
    INDEX idx_user_unread (user_id, is_read, created_at),
    INDEX idx_created_at (created_at)
);
```

### 6. 해시태그 및 검색 테이블

#### `hashtags` - 해시태그
```sql
CREATE TABLE hashtags (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,             -- 해시태그명 (# 제외)
    posts_count INT DEFAULT 0,                     -- 사용된 포스트 수
    trending_score DECIMAL(10,2) DEFAULT 0.00,    -- 트렌딩 점수
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_trending_score (trending_score),
    INDEX idx_posts_count (posts_count)
);
```

#### `check_in_hashtags` - 체크인-해시태그 연결
```sql
CREATE TABLE check_in_hashtags (
    id VARCHAR(36) PRIMARY KEY,
    check_in_id VARCHAR(36) NOT NULL,
    hashtag_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_check_in_hashtag (check_in_id, hashtag_id),
    FOREIGN KEY (check_in_id) REFERENCES check_ins(id) ON DELETE CASCADE,
    FOREIGN KEY (hashtag_id) REFERENCES hashtags(id) ON DELETE CASCADE
);
```

### 7. 위치 정보 테이블

#### `locations` - 위치 정보
```sql
CREATE TABLE locations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,                    -- 장소명
    address VARCHAR(500),                          -- 주소
    city VARCHAR(100),                             -- 도시
    country VARCHAR(100),                          -- 국가
    latitude DECIMAL(10,8),                        -- 위도
    longitude DECIMAL(11,8),                       -- 경도
    type ENUM('WHISKEY_BAR', 'RESTAURANT', 'HOME', 'EVENT', 'OTHER') DEFAULT 'OTHER',
    rating DECIMAL(3,2),                           -- 평균 평점
    check_ins_count INT DEFAULT 0,                 -- 체크인 수
    is_verified BOOLEAN DEFAULT FALSE,             -- 인증된 장소 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_location (latitude, longitude),
    INDEX idx_city_country (city, country),
    INDEX idx_type (type),
    INDEX idx_rating (rating)
);
```

### 8. 기존 테이블과의 연동

#### 기존 `whiskeys` 테이블 확장
```sql
-- 기존 whiskeys 테이블에 소셜 관련 컬럼 추가
ALTER TABLE whiskeys ADD COLUMN check_ins_count INT DEFAULT 0;
ALTER TABLE whiskeys ADD COLUMN average_rating DECIMAL(3,2) DEFAULT 0.00;
ALTER TABLE whiskeys ADD COLUMN trending_score DECIMAL(10,2) DEFAULT 0.00;
ALTER TABLE whiskeys ADD COLUMN is_featured BOOLEAN DEFAULT FALSE;
ALTER TABLE whiskeys ADD COLUMN last_check_in_at TIMESTAMP NULL;

-- 인덱스 추가
CREATE INDEX idx_whiskeys_check_ins_count ON whiskeys(check_ins_count);
CREATE INDEX idx_whiskeys_average_rating ON whiskeys(average_rating);
CREATE INDEX idx_whiskeys_trending_score ON whiskeys(trending_score);
```

## 📊 데이터베이스 관계도

```
users (1) -----> (N) check_ins
users (1) -----> (N) comments
users (1) -----> (N) likes
users (1) -----> (N) notifications
users (1) -----> (N) user_stats

whiskeys (1) -----> (N) check_ins
whiskeys (1) -----> (N) whiskey_notes (기존)

check_ins (1) -----> (N) check_in_images
check_ins (1) -----> (N) check_in_tags
check_ins (1) -----> (N) comments
check_ins (1) -----> (N) likes
check_ins (1) -----> (N) shares

follows (N) -----> (1) users (follower)
follows (N) -----> (1) users (following)

hashtags (1) -----> (N) check_in_hashtags
check_ins (1) -----> (N) check_in_hashtags
```

## 🔄 데이터 동기화 및 통계 업데이트

### 트리거 및 프로시저 예시

#### 체크인 생성 시 통계 업데이트
```sql
DELIMITER //
CREATE TRIGGER after_check_in_insert
AFTER INSERT ON check_ins
FOR EACH ROW
BEGIN
    -- 사용자 체크인 수 증가
    UPDATE user_stats 
    SET check_ins_count = check_ins_count + 1
    WHERE user_id = NEW.user_id;
    
    -- 위스키 체크인 수 증가 및 평점 업데이트
    UPDATE whiskeys 
    SET check_ins_count = check_ins_count + 1,
        last_check_in_at = NEW.created_at
    WHERE id = NEW.whiskey_id;
    
    -- 평점이 있는 경우 평균 업데이트
    IF NEW.rating IS NOT NULL THEN
        UPDATE whiskeys 
        SET average_rating = (
            SELECT AVG(rating) 
            FROM check_ins 
            WHERE whiskey_id = NEW.whiskey_id AND rating IS NOT NULL
        )
        WHERE id = NEW.whiskey_id;
    END IF;
END //
DELIMITER ;
```

#### 팔로우 시 통계 업데이트
```sql
DELIMITER //
CREATE TRIGGER after_follow_insert
AFTER INSERT ON follows
FOR EACH ROW
BEGIN
    -- 팔로워 수 증가
    UPDATE user_stats 
    SET followers_count = followers_count + 1
    WHERE user_id = NEW.following_id;
    
    -- 팔로잉 수 증가
    UPDATE user_stats 
    SET following_count = following_count + 1
    WHERE user_id = NEW.follower_id;
END //

CREATE TRIGGER after_follow_delete
AFTER DELETE ON follows
FOR EACH ROW
BEGIN
    -- 팔로워 수 감소
    UPDATE user_stats 
    SET followers_count = followers_count - 1
    WHERE user_id = OLD.following_id;
    
    -- 팔로잉 수 감소
    UPDATE user_stats 
    SET following_count = following_count - 1
    WHERE user_id = OLD.follower_id;
END //
DELIMITER ;
```

## 📈 성능 최적화

### 인덱스 전략
1. **복합 인덱스**: 자주 함께 조회되는 컬럼들
2. **시간 기반 인덱스**: 최신 데이터 조회 최적화
3. **공간 인덱스**: 위치 기반 검색 최적화

### 파티셔닝 전략
```sql
-- check_ins 테이블을 월별로 파티셔닝
ALTER TABLE check_ins PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- ... 계속 추가
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

## 🔐 보안 및 개인정보 보호

### 데이터 마스킹
- 이메일 주소 마스킹
- 개인정보 자동 삭제 (GDPR 준수)
- 비공개 계정 데이터 보호

### 접근 제어
- 팔로우 관계 기반 피드 필터링
- 차단된 사용자 데이터 제외
- 비공개 체크인 접근 제한

이 스키마는 SpiritScribe의 위스키 소셜 커뮤니티 기능을 위한 완전한 데이터베이스 구조를 제공합니다.

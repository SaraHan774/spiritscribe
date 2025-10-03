# 🥃 SpiritScribe

**Distill Your Taste** - "기록으로 취향을 증류하라. 단순한 노트가 아닌, 나만의 테이스트 프로파일을 완성하는 여정"

SpiritScribe는 위스키 애호가들을 위한 개인화된 테이스팅 노트 앱입니다. 위스키 정보를 기록하고, 테이스팅 노트를 작성하며, 나만의 테이스트 프로파일을 진화시켜보세요.

## ✨ 주요 기능

- **📝 위스키 컬렉션 관리**: 위스키 정보 등록 및 관리
- **🍯 테이스팅 노트**: 상세한 테이스팅 경험 기록
- **🎯 플레이버 프로파일**: 개인 취향 분석 및 시각화
- **📊 테이스트 진화**: 시간에 따른 취향 변화 추적
- **🔍 스마트 검색**: 다양한 필터와 정렬 옵션
- **📱 모던 UI**: Material3 디자인 시스템 기반

## 🛠️ 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose (Material3)
- **아키텍처**: MVVM + Repository Pattern
- **의존성 주입**: Hilt
- **로컬 데이터베이스**: Room
- **비동기 처리**: Kotlin Coroutines + Flow
- **이미지 로딩**: Coil3
- **데이터 직렬화**: Kotlinx Serialization
- **네비게이션**: Jetpack Compose Navigation
- **페이징**: Paging 3

## 🚀 빌드 및 실행

### 사전 요구사항

- Android Studio Arctic Fox 이상
- JDK 8 이상
- Android SDK 28 이상

### 로컬 빌드

```bash
# 프로젝트 클론
git clone <repository-url>
cd spiritscribe

# 의존성 설치 및 빌드
./gradlew build

# 디버그 APK 빌드
./gradlew assembleDebug

# 릴리즈 APK 빌드
./gradlew assembleRelease
```

### 개발 환경 실행

```bash
# 에뮬레이터 또는 실제 기기에서 실행
./gradlew installDebug

# 앱 실행
adb shell am start -n com.august.spiritscribe/.MainActivity
```

## 📦 Firebase App Distribution 배포

### 🔧 사전 설정

1. **Firebase CLI 설치**
   ```bash
   npm install -g firebase-tools
   ```

2. **Firebase 로그인**
   ```bash
   firebase login
   ```

3. **프로젝트 설정 확인**
   - Firebase 프로젝트: `spiritscribe`
   - 앱 ID: `1:481518662543:android:e182106b97b4f572e191fa`

### 📱 배포 방법

#### 방법 1: 자동화된 스크립트 사용 (권장)

```bash
# 디버그 빌드 배포
./deploy.sh debug "새로운 기능 테스트를 위한 빌드입니다!"

# 릴리즈 빌드 배포  
./deploy.sh release "정식 릴리즈 버전입니다!"

# 커스텀 릴리즈 노트와 함께
./deploy.sh debug "새로운 기능:
- 위스키 추가 화면 UI 개선
- 테이스팅 노트 검색 기능 추가
- 버그 수정 및 성능 최적화"
```

#### 방법 2: Gradle 태스크 사용

```bash
# 디버그 빌드 배포
./gradlew appDistributionUploadDebug

# 릴리즈 빌드 배포
./gradlew appDistributionUploadRelease
```

#### 방법 3: Firebase CLI 직접 사용

```bash
# APK 빌드
./gradlew assembleDebug

# Firebase App Distribution 업로드
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app 1:481518662543:android:e182106b97b4f572e191fa \
  --groups testers \
  --release-notes "SpiritScribe 앱의 새로운 테스트 빌드입니다!"
```

### 👥 테스터 관리

#### 테스터 그룹 설정

1. [Firebase Console](https://console.firebase.google.com/project/spiritscribe/appdistribution) 접속
2. "Tester groups" 탭 클릭
3. "Create group" 버튼 클릭
4. 그룹 이름: `testers`
5. 테스터 이메일 주소 추가

#### 테스터 초대 방법

테스터들은 다음 방법으로 앱을 받을 수 있습니다:

- **📧 이메일 초대**: Firebase에서 자동으로 보내는 이메일의 링크 클릭
- **🌐 Firebase Console**: 직접 APK 다운로드
- **📱 Firebase App Tester 앱**: Google Play Store에서 설치 후 초대 코드 사용

### 📊 배포 상태 확인

- **Firebase Console**: [App Distribution 페이지](https://console.firebase.google.com/project/spiritscribe/appdistribution)
- **배포 히스토리**: 모든 배포 기록 및 테스터 피드백 확인
- **크래시 리포트**: 실시간 크래시 및 오류 모니터링

## 🏗️ 프로젝트 구조

```
app/src/main/java/com/august/spiritscribe/
├── data/                    # 데이터 레이어
│   ├── local/              # Room 데이터베이스
│   ├── repository/         # Repository 구현
│   └── model/              # 데이터 모델
├── domain/                 # 도메인 레이어
│   ├── model/              # 비즈니스 모델
│   └── repository/         # Repository 인터페이스
├── ui/                     # 프레젠테이션 레이어
│   ├── feed/              # 피드 화면
│   ├── search/            # 검색 화면
│   ├── evolution/         # 테이스트 진화 화면
│   ├── whiskey/           # 위스키 관련 화면
│   ├── note/              # 노트 관련 화면
│   └── theme/             # 테마 설정
├── di/                     # 의존성 주입 (Hilt)
├── utils/                  # 유틸리티 클래스
└── Navigation.kt           # 네비게이션 설정
```

## 🔄 개발 워크플로우

### 1. 기능 개발
```bash
# 새로운 브랜치 생성
git checkout -b feature/new-feature

# 개발 및 테스트
./gradlew test
./gradlew assembleDebug
```

### 2. 테스트 빌드 배포
```bash
# 테스터들에게 배포
./deploy.sh debug "새로운 기능 테스트 버전"
```

### 3. 피드백 수집 및 수정
- Firebase Console에서 테스터 피드백 확인
- 크래시 리포트 분석
- 버그 수정 및 개선

### 4. 정식 릴리즈
```bash
# 릴리즈 빌드 배포
./deploy.sh release "정식 릴리즈 버전"
```

## 🐛 문제 해결

### 일반적인 문제

#### Firebase 로그인 오류
```bash
# Firebase 재로그인
firebase logout
firebase login
```

#### APK 빌드 실패
```bash
# Gradle 캐시 정리
./gradlew clean
./gradlew build
```

#### App Distribution 업로드 실패
```bash
# Firebase 프로젝트 재설정
firebase use spiritscribe
firebase projects:list
```

### 로그 확인

```bash
# Firebase CLI 로그
firebase --debug

# Android 로그
adb logcat | grep spiritscribe
```

## 📋 체크리스트

### 배포 전 확인사항

- [ ] 모든 테스트 통과
- [ ] 릴리즈 노트 작성
- [ ] 테스터 그룹 업데이트
- [ ] Firebase 로그인 상태 확인
- [ ] APK 빌드 성공

### 배포 후 확인사항

- [ ] Firebase Console에서 배포 상태 확인
- [ ] 테스터들에게 이메일 발송 확인
- [ ] 테스터 피드백 모니터링
- [ ] 크래시 리포트 확인

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 🔒 보안

### Firebase 보안 설정

Firebase 프로젝트에는 민감한 정보가 포함되어 있습니다. 보안을 유지하기 위해 다음을 확인하세요:

- **보안 가이드**: [`FIREBASE_SECURITY_GUIDE.md`](FIREBASE_SECURITY_GUIDE.md) 참조
- **환경 변수**: [`env.template`](env.template) 파일을 참조하여 로컬 환경 설정
- **API 키 제한**: Google Cloud Console에서 API 키 사용 제한 설정
- **서비스 계정**: 서비스 계정 키 파일은 절대 Git에 커밋하지 마세요

### 보안 체크리스트

- [ ] API 키 사용 제한 설정 완료
- [ ] 서비스 계정 키 파일이 Git에 없음
- [ ] Firebase 보안 규칙 설정 완료
- [ ] 정기적인 보안 감사 실시

## 📞 문의

- **프로젝트**: SpiritScribe
- **개발자**: August
- **이메일**: [your-email@example.com]
- **Firebase 프로젝트**: [spiritscribe](https://console.firebase.google.com/project/spiritscribe)

---

**Distill Your Taste** - 나만의 위스키 여정을 기록하고, 취향을 진화시키세요! 🥃✨

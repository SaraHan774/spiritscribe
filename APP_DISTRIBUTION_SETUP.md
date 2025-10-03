# Firebase App Distribution 설정 가이드

## 📱 SpiritScribe 앱의 Firebase App Distribution 설정

### 1. Firebase CLI 설치 및 로그인

```bash
# Firebase CLI 설치 (이미 완료됨)
npm install -g firebase-tools

# Firebase에 로그인
firebase login

# 프로젝트 선택
firebase use spiritscribe
```

### 2. 테스터 그룹 설정

Firebase Console에서 테스터 그룹을 생성하고 관리자로 설정:

1. [Firebase Console](https://console.firebase.google.com/) 접속
2. `spiritscribe` 프로젝트 선택
3. 왼쪽 메뉴에서 "App Distribution" 클릭
4. "Tester groups" 탭에서 "Create group" 클릭
5. 그룹 이름: `testers`
6. 테스터 이메일 추가

### 3. APK 빌드 및 배포

#### 방법 1: Gradle 명령어 사용
```bash
# 디버그 APK 빌드 및 배포
./gradlew appDistributionUploadDebug

# 릴리즈 APK 빌드 및 배포  
./gradlew appDistributionUploadRelease
```

#### 방법 2: 수동 업로드
```bash
# APK 빌드
./gradlew assembleDebug

# Firebase CLI로 업로드
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app 1:481518662543:android:e182106b97b4f572e191fa \
  --groups testers \
  --release-notes "SpiritScribe 앱의 새로운 테스트 빌드입니다!"
```

### 4. 테스터 초대

테스터들은 다음 방법으로 앱을 설치할 수 있습니다:

1. **이메일 초대**: Firebase에서 자동으로 보내는 이메일의 링크 클릭
2. **Firebase Console**: App Distribution 페이지에서 직접 APK 다운로드
3. **Firebase App Tester 앱**: Google Play Store에서 설치 후 초대 코드 사용

### 5. 설정된 구성

- **프로젝트 ID**: `spiritscribe`
- **앱 ID**: `1:481518662543:android:e182106b97b4f572e191fa`
- **패키지명**: `com.august.spiritscribe`
- **테스터 그룹**: `testers`
- **자동 알림**: 활성화됨

### 6. 고급 설정

#### 서비스 계정 키 사용 (선택사항)
```bash
# 서비스 계정 키 파일 다운로드 후
export GOOGLE_APPLICATION_CREDENTIALS="path/to/service-account-key.json"
```

#### 커스텀 릴리즈 노트
```bash
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app 1:481518662543:android:e182106b97b4f572e191fa \
  --groups testers \
  --release-notes "새로운 기능:
  - 위스키 추가 화면에 TopAppBar 추가
  - 테이스팅 노트 UI 개선
  - 검색 기능 강화"
```

### 7. 트러블슈팅

#### 권한 오류
- Firebase 프로젝트의 "App Distribution Admin" 권한 확인
- Google Cloud Console에서 서비스 계정 권한 확인

#### 빌드 오류
- `google-services.json` 파일이 올바른 위치에 있는지 확인
- Gradle 플러그인이 올바르게 적용되었는지 확인

### 8. 유용한 명령어

```bash
# Firebase 프로젝트 목록 확인
firebase projects:list

# 현재 프로젝트 확인
firebase use

# App Distribution 상태 확인
firebase appdistribution:distributions:list

# 테스터 그룹 목록 확인
firebase appdistribution:testers:list
```

---

## 🚀 빠른 시작

1. `firebase login` 실행
2. `firebase use spiritscribe` 실행  
3. `./gradlew appDistributionUploadDebug` 실행
4. Firebase Console에서 테스터 그룹 설정
5. 테스터들에게 초대 이메일 발송

이제 SpiritScribe 앱을 쉽게 테스터들에게 배포할 수 있습니다! 🎉

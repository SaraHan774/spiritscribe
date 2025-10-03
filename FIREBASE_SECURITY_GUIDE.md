# 🔒 Firebase 보안 가이드

## ⚠️ 중요: Firebase 민감한 정보 보호

Firebase 프로젝트에는 민감한 정보가 포함되어 있습니다. 다음 가이드를 따라 보안을 유지하세요.

## 📋 현재 추적 중인 민감한 파일

### `app/google-services.json`
- **상태**: Git에 추적됨 ⚠️
- **내용**: Firebase 프로젝트 설정, API 키, 프로젝트 ID
- **위험도**: 중간 (공개 API 키이지만 제한적 사용 권장)

## 🛡️ 보안 조치

### 1. google-services.json 파일 보안

현재 `google-services.json` 파일이 Git에 추적되고 있습니다. 보안을 강화하려면:

#### 옵션 A: Git에서 제거 (권장)
```bash
# Git에서 파일 제거 (로컬에는 유지)
git rm --cached app/google-services.json

# .gitignore에서 주석 해제
# google-services.json  # 이 줄의 주석을 제거

# 커밋
git add .gitignore
git commit -m "Remove google-services.json from Git tracking for security"
```

#### 옵션 B: API 키 제한 설정
Firebase Console에서 API 키 사용을 제한하세요:
1. [Google Cloud Console](https://console.cloud.google.com/apis/credentials) 접속
2. API 키 클릭
3. "애플리케이션 제한사항" 설정
4. "Android 앱" 선택하고 패키지명과 SHA-1 인증서 지문 추가

### 2. 서비스 계정 키 파일 보호

```bash
# 서비스 계정 키 파일이 있다면 Git에서 제거
git rm --cached *-service-account-key.json
git rm --cached service-account-key.json
git rm --cached firebase-service-account.json
```

### 3. Firebase CLI 설정 파일 보호

다음 파일들은 이미 .gitignore에 추가되었습니다:
- `.firebaserc`
- `.firebase/`
- `firebase.json`

## 🔧 보안 설정 방법

### Firebase API 키 제한

1. **Google Cloud Console**에서 API 키 제한:
   ```
   https://console.cloud.google.com/apis/credentials?project=spiritscribe
   ```

2. **애플리케이션 제한사항** 설정:
   - Android 앱: `com.august.spiritscribe`
   - SHA-1 인증서 지문 추가

3. **API 제한사항** 설정:
   - Firebase App Distribution API
   - Firebase Management API
   - 필요한 API만 선택

### Firebase 보안 규칙

Firebase 프로젝트에서 다음 보안 규칙을 설정하세요:

```javascript
// Firebase Storage 보안 규칙 예시
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🚨 보안 체크리스트

### 배포 전 확인사항
- [ ] API 키 사용 제한 설정 완료
- [ ] 서비스 계정 키 파일이 Git에 없음
- [ ] Firebase 보안 규칙 설정 완료
- [ ] 테스터 그룹 권한 확인

### 정기 보안 점검
- [ ] API 키 사용량 모니터링
- [ ] Firebase Console 로그인 활동 확인
- [ ] 테스터 그룹 멤버십 검토
- [ ] 사용하지 않는 API 키 비활성화

## 🔍 보안 모니터링

### Firebase Console에서 확인할 항목
1. **Authentication** → 사용자 활동
2. **App Distribution** → 배포 기록
3. **Crashlytics** → 의심스러운 크래시
4. **Analytics** → 비정상적인 사용 패턴

### Google Cloud Console에서 확인할 항목
1. **API 및 서비스** → API 키 사용량
2. **IAM 및 관리** → 서비스 계정 권한
3. **보안** → 감사 로그

## 📞 보안 사고 대응

### API 키 노출 시 대응
1. 즉시 Google Cloud Console에서 API 키 삭제/재생성
2. Firebase Console에서 새 API 키로 업데이트
3. 모든 기기에서 앱 재설치 필요

### 서비스 계정 키 노출 시 대응
1. 즉시 Google Cloud Console에서 서비스 계정 키 삭제
2. 새 서비스 계정 키 생성
3. CI/CD 파이프라인 업데이트

## 💡 보안 모범 사례

### 개발 환경
- 로컬 개발 시 개발용 Firebase 프로젝트 사용
- 프로덕션과 개발 환경 분리
- 환경별 API 키 분리

### 배포 환경
- CI/CD에서 환경 변수로 민감한 정보 관리
- 서비스 계정 키는 CI/CD 시스템에서만 사용
- 배포 스크립트에 최소 권한 부여

### 팀 협업
- 민감한 정보는 Slack/이메일로 공유 금지
- 비밀 관리 도구 사용 (예: 1Password, LastPass)
- 정기적인 액세스 권한 검토

---

## ⚠️ 중요 알림

**현재 `app/google-services.json` 파일이 Git에 추적되고 있습니다.**
이 파일을 제거하거나 API 키 제한을 설정하는 것을 강력히 권장합니다.

### 즉시 실행할 수 있는 보안 조치:
1. Google Cloud Console에서 API 키 제한 설정
2. Firebase Console에서 보안 규칙 검토
3. 정기적인 보안 감사 실시

**보안은 개발 과정에서 가장 중요한 요소입니다!** 🔒

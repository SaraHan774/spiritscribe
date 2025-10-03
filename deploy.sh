#!/bin/bash

# SpiritScribe App Distribution 배포 스크립트
# 사용법: ./deploy.sh [debug|release] [릴리즈 노트]

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수 정의
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  SpiritScribe App Distribution 배포${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# 매개변수 확인
BUILD_TYPE=${1:-debug}
RELEASE_NOTES=${2:-"SpiritScribe 앱의 새로운 테스트 빌드입니다!"}

# Git 정보 가져오기
GIT_HASH=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
BUILD_TIME=$(date '+%Y%m%d-%H%M')
VERSION_NAME="0.0.1_${GIT_HASH}"

# 동적 릴리즈 노트 생성 (사용자가 제공하지 않은 경우)
if [ "$2" = "" ]; then
    RELEASE_NOTES="🥃 SpiritScribe 앱 새로운 빌드

📦 버전: ${VERSION_NAME}
🔗 커밋: ${GIT_HASH}
🕒 빌드 시간: ${BUILD_TIME}

새로운 기능과 개선사항을 테스트해보세요!
피드백은 Firebase Console을 통해 제공해주세요."
fi

print_header
print_info "빌드 타입: $BUILD_TYPE"
print_info "Git 커밋: $GIT_HASH"
print_info "버전명: $VERSION_NAME"
print_info "빌드 시간: $BUILD_TIME"
print_info "릴리즈 노트: $RELEASE_NOTES"
echo

# Firebase CLI 설치 확인
if ! command -v firebase &> /dev/null; then
    print_error "Firebase CLI가 설치되지 않았습니다."
    print_info "설치 방법: npm install -g firebase-tools"
    exit 1
fi

# Firebase 로그인 확인
if ! firebase projects:list &> /dev/null; then
    print_error "Firebase에 로그인되지 않았습니다."
    print_info "로그인 방법: firebase login"
    exit 1
fi

# 보안 경고
print_warning "보안 확인: 민감한 Firebase 정보가 노출되지 않는지 확인하세요."
print_info "FIREBASE_SECURITY_GUIDE.md를 참조하여 보안 설정을 확인하세요."

# Firebase 프로젝트 설정 확인
if [ ! -f "firebase.json" ]; then
    print_error "firebase.json 파일을 찾을 수 없습니다."
    exit 1
fi

if [ ! -f "app/google-services.json" ]; then
    print_error "google-services.json 파일을 찾을 수 없습니다."
    print_info "Firebase Console에서 다운로드하여 app/ 디렉토리에 배치하세요."
    exit 1
fi

print_success "Firebase CLI 및 설정 파일 확인 완료"

# APK 빌드
print_info "APK 빌드 중..."
if [ "$BUILD_TYPE" = "debug" ]; then
    ./gradlew assembleDebug
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
else
    ./gradlew assembleRelease
    APK_PATH="app/build/outputs/apk/release/app-release.apk"
fi

if [ ! -f "$APK_PATH" ]; then
    print_error "APK 파일을 찾을 수 없습니다: $APK_PATH"
    exit 1
fi

print_success "APK 빌드 완료: $APK_PATH"

# Firebase App Distribution 업로드
print_info "Firebase App Distribution에 업로드 중..."

firebase appdistribution:distribute "$APK_PATH" \
  --app "1:481518662543:android:e182106b97b4f572e191fa" \
  --groups "testers" \
  --release-notes "$RELEASE_NOTES"

if [ $? -eq 0 ]; then
    print_success "배포 완료!"
    print_info "테스터들에게 이메일 알림이 발송되었습니다."
    print_info "Firebase Console에서 배포 상태를 확인할 수 있습니다:"
    print_info "https://console.firebase.google.com/project/spiritscribe/appdistribution"
else
    print_error "배포 실패"
    exit 1
fi

echo
print_header
print_success "SpiritScribe 앱 배포가 완료되었습니다! 🎉"

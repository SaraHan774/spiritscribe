package com.august.spiritscribe.feature.lab.domain

/**
 * 스월 감지 관련 데이터 클래스들
 */
data class SwirlDetectionResult(
    val swirlScore: Float,
    val isActive: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class SwirlCaptureConfig(
    val threshold: Float = 1.8f,
    val durationMs: Long = 500L,
    val captureDurationMs: Long = 3000L,
    val roiSize: Float = 0.6f // 화면 중앙 원형 ROI 크기 비율
)

/**
 * 스월 감지 상태
 */
enum class SwirlDetectionState {
    IDLE,           // 대기 중
    DETECTING,      // 스월 감지 중 (임계치 미달)
    CAPTURING,      // 캡처 중 (임계치 초과)
    PROCESSING      // 후처리 중
}

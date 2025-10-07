package com.august.spiritscribe.feature.lab.domain

/**
 * 실험실에서 제공하는 기능들을 정의하는 열거형
 */
enum class LabFeature(
    val displayName: String,
    val description: String,
    val emoji: String,
    val isAvailable: Boolean = true
) {
    /**
     * 스월 아트 - 위스키 잔의 소용돌이를 촬영하여 아트로 변환
     */
    SWIRL_ART(
        displayName = "스월 아트",
        description = "위스키 잔의 소용돌이를 촬영하여 아름다운 아트로 변환",
        emoji = "🌪️",
        isAvailable = true
    ),

    /**
     * 스타일 전이 - 촬영한 영상을 다양한 아트 스타일로 변환
     */
    STYLE_TRANSFER(
        displayName = "스타일 전이",
        description = "네온, 유화 등 다양한 스타일로 영상 변환",
        emoji = "🎨",
        isAvailable = true
    ),

    /**
     * AI 분석 - 위스키의 색상과 질감을 AI로 분석
     */
    AI_ANALYSIS(
        displayName = "AI 분석",
        description = "위스키의 색상과 질감을 AI로 분석하여 정보 제공",
        emoji = "🤖",
        isAvailable = false // TODO: 구현 예정
    ),

    /**
     * 시각 효과 - 다양한 시각적 효과 적용
     */
    VISUAL_EFFECTS(
        displayName = "시각 효과",
        description = "필터, 조명 효과 등 다양한 시각적 효과 적용",
        emoji = "✨",
        isAvailable = false // TODO: 구현 예정
    );

    /**
     * 기능의 상태를 나타내는 데이터 클래스
     */
    data class FeatureStatus(
        val isEnabled: Boolean,
        val progress: Float = 0f, // 0.0 ~ 1.0
        val estimatedTime: String? = null
    )
}

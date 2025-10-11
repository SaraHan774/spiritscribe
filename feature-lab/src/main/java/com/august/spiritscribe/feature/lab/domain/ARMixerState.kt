package com.august.spiritscribe.feature.lab.domain

/**
 * AR 하이볼 믹서의 상태를 나타내는 데이터 클래스
 */
data class ARMixerState(
    val isTracking: Boolean = false,
    val detectedGlass: Boolean = false,
    val currentRatio: MixingRatio = MixingRatio.RATIO_1_3,
    val liquidLevel: Float = 0f, // 0.0 ~ 1.0
    val isOcclusionEnabled: Boolean = true,
    val depthAvailable: Boolean = false,
    val error: String? = null
)

/**
 * 믹싱 비율 열거형
 */
enum class MixingRatio(
    val displayName: String,
    val ratio: Float,
    val description: String
) {
    RATIO_1_3(
        displayName = "1:3",
        ratio = 1f / 3f,
        description = "위스키 1 : 토닉워터 3"
    ),
    RATIO_1_4(
        displayName = "1:4", 
        ratio = 1f / 4f,
        description = "위스키 1 : 토닉워터 4"
    ),
    RATIO_1_5(
        displayName = "1:5",
        ratio = 1f / 5f,
        description = "위스키 1 : 토닉워터 5"
    ),
    RATIO_2_3(
        displayName = "2:3",
        ratio = 2f / 3f,
        description = "위스키 2 : 토닉워터 3"
    )
}

/**
 * AR 라인 정보
 */
data class ARLine(
    val ratio: MixingRatio,
    val position: Float, // 0.0 ~ 1.0 (컵 높이 기준)
    val color: Int,
    val isVisible: Boolean = true,
    val isOccluded: Boolean = false
)

package com.august.spiritscribe.feature.lab.domain

/**
 * AR 하이볼 믹서 설정
 */
data class ARMixerConfig(
    val enableDepthOcclusion: Boolean = true,
    val enableAugmentedImages: Boolean = true,
    val lineThickness: Float = 3f,
    val lineAlpha: Float = 0.8f,
    val animationDuration: Long = 300L,
    val trackingTimeout: Long = 5000L
)

/**
 * AR 라인 색상 설정
 */
object ARLineColors {
    const val WHISKEY_LINE = 0xFF8B4513 // 갈색
    const val TONIC_LINE = 0xFF00CED1 // 시안색
    const val MIXED_LINE = 0xFFDAA520 // 골든로드
    const val BACKGROUND = 0x80000000 // 반투명 검정
}

/**
 * AR 추적 상태
 */
enum class ARTrackingState {
    IDLE,
    TRACKING,
    LOST,
    ERROR
}

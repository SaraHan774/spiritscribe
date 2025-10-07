package com.august.spiritscribe.feature.lab.domain

/**
 * 실험실 화면의 상태를 나타내는 데이터 클래스
 */
data class LabState(
    val availableFeatures: List<LabFeature> = emptyList(),
    val currentFeature: LabFeature? = null,
    val isRecording: Boolean = false,
    val recordingProgress: Float = 0f,
    val lastGeneratedArt: String? = null
)

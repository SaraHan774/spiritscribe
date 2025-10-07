package com.august.spiritscribe.feature.lab.domain

/**
 * 스월 아트 화면의 상태를 나타내는 데이터 클래스
 */
data class SwirlArtState(
    val hasCameraPermission: Boolean = false,
    val isRecording: Boolean = false,
    val isGenerating: Boolean = false,
    val recordingStartTime: Long? = null,
    val recordingEndTime: Long? = null,
    val recordedVideoPath: String? = null,
    val generatedArtPath: String? = null,
    val error: String? = null
)

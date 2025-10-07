package com.august.spiritscribe.feature.lab.domain

/**
 * 스월 아트에서 사용할 수 있는 스타일 전이 옵션들
 */
enum class SwirlArtStyle(
    val displayName: String,
    val description: String,
    val emoji: String,
    val modelPath: String? = null // TensorFlow Lite 모델 파일 경로
) {
    /**
     * 네온 스타일 - 밝고 화려한 네온 효과
     */
    NEON(
        displayName = "네온",
        description = "밝고 화려한 네온 효과",
        emoji = "💡",
        modelPath = "models/neon_style.tflite"
    ),

    /**
     * 유화 스타일 - 클래식한 유화 느낌
     */
    OIL_PAINTING(
        displayName = "유화",
        description = "클래식한 유화 느낌",
        emoji = "🎨",
        modelPath = "models/oil_painting_style.tflite"
    ),

    /**
     * 수채화 스타일 - 부드럽고 투명한 수채화 효과
     */
    WATERCOLOR(
        displayName = "수채화",
        description = "부드럽고 투명한 수채화 효과",
        emoji = "🖌️",
        modelPath = "models/watercolor_style.tflite"
    ),

    /**
     * 만화 스타일 - 애니메이션/만화 느낌
     */
    CARTOON(
        displayName = "만화",
        description = "애니메이션/만화 느낌",
        emoji = "📺",
        modelPath = "models/cartoon_style.tflite"
    ),

    /**
     * 고전 스타일 - 클래식한 예술 작품 느낌
     */
    CLASSICAL(
        displayName = "고전",
        description = "클래식한 예술 작품 느낌",
        emoji = "🏛️",
        modelPath = "models/classical_style.tflite"
    ),

    /**
     * 사이버펑크 스타일 - 미래적이고 사이버펑크 느낌
     */
    CYBERPUNK(
        displayName = "사이버펑크",
        description = "미래적이고 사이버펑크 느낌",
        emoji = "🤖",
        modelPath = "models/cyberpunk_style.tflite"
    );

    /**
     * 스타일의 강도 설정 (0.0 ~ 1.0)
     */
    data class StyleIntensity(
        val value: Float,
        val displayName: String
    ) {
        companion object {
            val LOW = StyleIntensity(0.3f, "약함")
            val MEDIUM = StyleIntensity(0.6f, "보통")
            val HIGH = StyleIntensity(0.9f, "강함")
        }
    }
}

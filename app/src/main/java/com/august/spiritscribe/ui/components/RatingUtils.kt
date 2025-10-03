package com.august.spiritscribe.ui.components

import androidx.compose.ui.graphics.Color

/**
 * 평점 관련 유틸리티 함수들
 */

data class RatingDisplay(
    val stars: String,
    val text: String,
    val color: Color,
    val percentage: Int
)

/**
 * 1-5점 평점을 시각적으로 매력적인 형태로 변환
 */
fun getRatingDisplay(rating: Int): RatingDisplay {
    return when (rating) {
        1 -> RatingDisplay(
            stars = "⭐",
            text = "기본",
            color = Color(0xFFE57373), // Light Red
            percentage = 20
        )
        2 -> RatingDisplay(
            stars = "⭐⭐",
            text = "좋음",
            color = Color(0xFFFFB74D), // Orange
            percentage = 40
        )
        3 -> RatingDisplay(
            stars = "⭐⭐⭐",
            text = "매우좋음",
            color = Color(0xFF64B5F6), // Light Blue
            percentage = 60
        )
        4 -> RatingDisplay(
            stars = "⭐⭐⭐⭐",
            text = "훌륭함",
            color = Color(0xFF81C784), // Light Green
            percentage = 80
        )
        5 -> RatingDisplay(
            stars = "⭐⭐⭐⭐⭐",
            text = "최고",
            color = Color(0xFF9575CD), // Purple
            percentage = 100
        )
        else -> RatingDisplay(
            stars = "⭐",
            text = "기본",
            color = Color(0xFFE57373), // Light Red
            percentage = 20
        )
    }
}

/**
 * 100점 만점 점수를 1-5점으로 변환 (역변환)
 */
fun convertFrom100Point(score: Int): Int {
    return when {
        score <= 20 -> 1
        score <= 40 -> 2
        score <= 60 -> 3
        score <= 80 -> 4
        else -> 5
    }
}

/**
 * 1-5점을 100점 만점으로 변환 (기존 방식 유지)
 */
fun convertTo100Point(rating: Int): Int {
    return rating * 20
}

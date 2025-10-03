package com.august.spiritscribe.domain.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 테이스트 진화를 추적하는 도메인 모델
 */
@Serializable
data class TasteEvolution(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val evolutionStage: EvolutionStage,
    val dnaStrands: List<DNAStrand>,
    val evolutionPoints: List<EvolutionPoint>,
    @Contextual val currentFlavorProfile: FlavorProfile,
    val totalNotes: Int,
    val uniqueFlavors: Int,
    val evolutionScore: Double,
    val lastEvolutionDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class EvolutionStage(val displayName: String, val emoji: String, val description: String) {
    EGG("알", "🥚", "테이스트 여정의 시작점"),
    LARVA("애벌레", "🐛", "기본 취향이 형성되는 단계"),
    PUPA("번데기", "🦋", "취향이 변화하고 성숙해지는 단계"),
    BUTTERFLY("나비", "🦋", "완성된 테이스트 마스터");
    
    val nextStage: EvolutionStage?
        get() = when (this) {
            EGG -> LARVA
            LARVA -> PUPA
            PUPA -> BUTTERFLY
            BUTTERFLY -> null
        }
    
    val requiredNotes: Int
        get() = when (this) {
            EGG -> 0
            LARVA -> 5
            PUPA -> 15
            BUTTERFLY -> 30
        }
}

@Serializable
data class DNAStrand(
    val id: String = UUID.randomUUID().toString(),
    val primaryFlavor: Flavor,
    val secondaryFlavor: Flavor? = null,
    val intensity: Int, // 0-5 scale
    val frequency: Int, // 얼마나 자주 선택되었는지
    val firstDiscoveredDate: Long,
    val lastUsedDate: Long,
    val evolutionLevel: Int = 1 // 1-5, 얼마나 진화했는지
)

@Serializable
data class EvolutionPoint(
    val id: String = UUID.randomUUID().toString(),
    val type: EvolutionPointType,
    val title: String,
    val description: String,
    val achievedDate: Long,
    val flavor: Flavor? = null,
    val milestone: Int? = null
)

@Serializable
enum class EvolutionPointType {
    FIRST_NOTE,           // 첫 번째 노트 작성
    FLAVOR_DISCOVERY,     // 새로운 플레이버 발견
    STAGE_EVOLUTION,      // 진화 단계 달성
    MILESTONE_NOTES,      // 노트 개수 마일스톤
    FLAVOR_MASTERY,       // 특정 플레이버 마스터리
    COMBINATION_BREAKTHROUGH // 새로운 플레이버 조합 발견
}

/**
 * 테이스트 진화 분석 결과
 */
data class EvolutionAnalysis(
    val currentStage: EvolutionStage,
    val progressToNextStage: Double, // 0.0 - 1.0
    val dominantFlavors: List<Flavor>,
    val emergingFlavors: List<Flavor>,
    val decliningFlavors: List<Flavor>,
    val personalityType: TastePersonality,
    val evolutionTrend: EvolutionTrend,
    val nextMilestones: List<EvolutionPoint>
)

@Serializable
enum class TastePersonality(val displayName: String, val description: String) {
    CLASSIC_CONNOISSEUR("클래식 감식가", "전통적이고 안정적인 취향을 선호합니다"),
    ADVENTUROUS_EXPLORER("모험적 탐험가", "새로운 플레이버를 적극적으로 탐구합니다"),
    SWEET_DREAMER("스위트 드리머", "달콤하고 부드러운 플레이버를 좋아합니다"),
    SMOKY_WARRIOR("스모키 워리어", "강렬하고 스모키한 플레이버를 선호합니다"),
    BALANCED_MASTER("균형잡힌 마스터", "다양한 플레이버의 조화를 추구합니다"),
    MINIMALIST_PURIST("미니멀 퓨리스트", "깔끔하고 단순한 플레이버를 선호합니다")
}

@Serializable
enum class EvolutionTrend(val displayName: String, val description: String) {
    RAPID_GROWTH("급성장", "취향이 빠르게 발전하고 있습니다"),
    STEADY_PROGRESS("꾸준한 발전", "안정적으로 취향이 발전하고 있습니다"),
    EXPLORATION_PHASE("탐험 단계", "새로운 영역을 적극 탐구하고 있습니다"),
    MASTERY_PHASE("마스터리 단계", "특정 영역에서 깊이를 쌓고 있습니다"),
    STAGNATION("정체", "최근 취향 발전이 둔화되고 있습니다")
}

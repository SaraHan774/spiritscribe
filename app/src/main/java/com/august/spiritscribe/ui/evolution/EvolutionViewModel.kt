package com.august.spiritscribe.ui.evolution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class EvolutionViewModel @Inject constructor(
    private val whiskeyNoteDao: WhiskeyNoteDao
) : ViewModel() {
    
    private val _evolutionState = MutableStateFlow(EvolutionState())
    val evolutionState: StateFlow<EvolutionState> = _evolutionState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadEvolutionData()
    }
    
    /**
     * 데이터베이스에서 진화 데이터를 로드하고 분석
     */
    private fun loadEvolutionData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Flow를 한 번만 collect하여 데이터 로딩
                whiskeyNoteDao.getNotesByCreatedDesc()
                    .catch { e ->
                        _error.value = "진화 데이터를 불러오는 중 오류가 발생했습니다: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { noteEntities ->
                        val evolutionData = analyzeTasteEvolution(noteEntities)
                        val analysis = performEvolutionAnalysis(evolutionData)
                        
                        _evolutionState.value = EvolutionState(
                            evolution = evolutionData,
                            analysis = analysis,
                            isLoading = false
                        )
                        _isLoading.value = false
                    }
                
            } catch (e: Exception) {
                _error.value = "진화 분석 중 오류가 발생했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 위스키 노트들을 분석하여 TasteEvolution 객체 생성
     */
    private fun analyzeTasteEvolution(notes: List<com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity>): TasteEvolution {
        val allFlavors = mutableListOf<FlavorIntensity>()
        val flavorFrequency = mutableMapOf<Flavor, Int>()
        val flavorFirstDiscovery = mutableMapOf<Flavor, Long>()
        val flavorLastUsed = mutableMapOf<Flavor, Long>()
        
        // 모든 노트에서 플레이버 데이터 추출
        notes.forEach { noteEntity ->
            try {
                val note = noteEntity.toDomain()
                allFlavors.addAll(note.flavors)
                
                note.flavors.forEach { flavorIntensity ->
                    val flavor = flavorIntensity.flavor
                    flavorFrequency[flavor] = flavorFrequency.getOrDefault(flavor, 0) + 1
                    flavorLastUsed[flavor] = note.createdAt
                    
                    if (flavor !in flavorFirstDiscovery) {
                        flavorFirstDiscovery[flavor] = note.createdAt
                    }
                }
            } catch (e: Exception) {
                // 개별 노트 파싱 오류는 무시하고 계속 진행
            }
        }
        
        // 데이터가 없는 경우 기본값 반환
        if (notes.isEmpty()) {
            return TasteEvolution(
                userId = "current_user",
                evolutionStage = EvolutionStage.EGG,
                dnaStrands = emptyList(),
                evolutionPoints = emptyList(),
                currentFlavorProfile = FlavorProfile(),
                totalNotes = 0,
                uniqueFlavors = 0,
                evolutionScore = 0.0
            )
        }
        
        // DNA 스트랜드 생성
        val dnaStrands = flavorFrequency.map { (flavor, frequency) ->
            val intensities = allFlavors.filter { it.flavor == flavor }
            val avgIntensity = if (intensities.isNotEmpty()) {
                intensities.map { it.intensity }.average().toInt()
            } else 0
            
            DNAStrand(
                primaryFlavor = flavor,
                intensity = avgIntensity,
                frequency = frequency,
                firstDiscoveredDate = flavorFirstDiscovery[flavor] ?: System.currentTimeMillis(),
                lastUsedDate = flavorLastUsed[flavor] ?: System.currentTimeMillis(),
                evolutionLevel = calculateEvolutionLevel(frequency, avgIntensity)
            )
        }
        
        // 진화 포인트 생성
        val evolutionPoints = generateEvolutionPoints(notes, flavorFrequency)
        
        // 현재 진화 단계 결정
        val currentStage = determineEvolutionStage(notes.size, flavorFrequency.size)
        
        // 플레이버 프로파일 생성
        val flavorProfile = createFlavorProfile(allFlavors)
        
        // 진화 점수 계산
        val evolutionScore = calculateEvolutionScore(notes.size, flavorFrequency.size, dnaStrands)
        
        return TasteEvolution(
            userId = "current_user", // 실제로는 사용자 ID 사용
            evolutionStage = currentStage,
            dnaStrands = dnaStrands.sortedByDescending { it.evolutionLevel },
            evolutionPoints = evolutionPoints,
            currentFlavorProfile = flavorProfile,
            totalNotes = notes.size,
            uniqueFlavors = flavorFrequency.size,
            evolutionScore = evolutionScore
        )
    }
    
    /**
     * 진화 레벨 계산 (1-5)
     */
    private fun calculateEvolutionLevel(frequency: Int, intensity: Int): Int {
        val frequencyScore = min(frequency / 3, 5) // 3번마다 1점씩, 최대 5점
        val intensityScore = intensity / 2 // 강도 점수
        return min(frequencyScore + intensityScore, 5)
    }
    
    /**
     * 진화 단계 결정
     */
    private fun determineEvolutionStage(totalNotes: Int, uniqueFlavors: Int): EvolutionStage {
        return when {
            totalNotes >= 30 && uniqueFlavors >= 10 -> EvolutionStage.BUTTERFLY
            totalNotes >= 15 && uniqueFlavors >= 6 -> EvolutionStage.PUPA
            totalNotes >= 5 && uniqueFlavors >= 3 -> EvolutionStage.LARVA
            else -> EvolutionStage.EGG
        }
    }
    
    /**
     * 진화 포인트 생성
     */
    private fun generateEvolutionPoints(
        notes: List<com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity>,
        flavorFrequency: Map<Flavor, Int>
    ): List<EvolutionPoint> {
        val points = mutableListOf<EvolutionPoint>()
        
        // 첫 번째 노트
        if (notes.isNotEmpty()) {
            points.add(
                EvolutionPoint(
                    type = EvolutionPointType.FIRST_NOTE,
                    title = "첫 걸음",
                    description = "테이스트 여정을 시작했습니다",
                    achievedDate = notes.minByOrNull { it.createdAt }?.createdAt ?: System.currentTimeMillis()
                )
            )
        }
        
        // 노트 개수 마일스톤
        val milestones = listOf(5, 10, 15, 20, 30, 50)
        milestones.forEach { milestone ->
            if (notes.size >= milestone) {
                points.add(
                    EvolutionPoint(
                        type = EvolutionPointType.MILESTONE_NOTES,
                        title = "${milestone}번째 노트",
                        description = "${milestone}개의 위스키를 기록했습니다",
                        achievedDate = notes.sortedBy { it.createdAt }.getOrNull(milestone - 1)?.createdAt ?: System.currentTimeMillis(),
                        milestone = milestone
                    )
                )
            }
        }
        
        // 플레이버 발견
        flavorFrequency.forEach { (flavor, frequency) ->
            if (frequency >= 3) { // 3번 이상 선택된 플레이버
                points.add(
                    EvolutionPoint(
                        type = EvolutionPointType.FLAVOR_MASTERY,
                        title = "${flavor.displayName} 마스터",
                        description = "${flavor.displayName}를 ${frequency}번 선택했습니다",
                        achievedDate = System.currentTimeMillis(),
                        flavor = flavor
                    )
                )
            }
        }
        
        return points.sortedByDescending { it.achievedDate }
    }
    
    /**
     * 플레이버 프로파일 생성
     */
    private fun createFlavorProfile(allFlavors: List<FlavorIntensity>): FlavorProfile {
        val flavorAverages = allFlavors.groupBy { it.flavor }
            .mapValues { (_, intensities) ->
                intensities.map { it.intensity }.average()
            }
        
        val aromaFlavors = mutableListOf<String>()
        val palateFlavors = mutableListOf<String>()
        val finishFlavors = mutableListOf<String>()
        
        flavorAverages.forEach { (flavor, avgIntensity) ->
            val intensity = avgIntensity.toInt()
            val flavorName = flavor.displayName
            
            when {
                flavor in listOf(Flavor.FLORAL, Flavor.CITRUS, Flavor.HERB, Flavor.PEAT) -> {
                    aromaFlavors.add("$flavorName (강도: $intensity/5)")
                }
                flavor in listOf(Flavor.MALT, Flavor.FRUIT, Flavor.DRIED, Flavor.SPICE, 
                               Flavor.WOOD, Flavor.NUTS, Flavor.TOFFEE, Flavor.VANILLA, Flavor.HONEY, Flavor.CHAR) -> {
                    palateFlavors.add("$flavorName (강도: $intensity/5)")
                }
                else -> {
                    palateFlavors.add("$flavorName (강도: $intensity/5)")
                }
            }
        }
        
        return FlavorProfile(
            aroma = aromaFlavors.distinct().sorted(),
            palate = palateFlavors.distinct().sorted(),
            finish = finishFlavors.distinct().sorted()
        )
    }
    
    /**
     * 진화 점수 계산 (0.0 - 100.0)
     */
    private fun calculateEvolutionScore(totalNotes: Int, uniqueFlavors: Int, dnaStrands: List<DNAStrand>): Double {
        val notesScore = min(totalNotes * 2.0, 40.0) // 노트 개수 점수 (최대 40점)
        val flavorScore = min(uniqueFlavors * 3.0, 30.0) // 플레이버 다양성 점수 (최대 30점)
        val evolutionScore = dnaStrands.sumOf { it.evolutionLevel * 2.0 } // 진화 레벨 점수 (최대 30점)
        
        return min(notesScore + flavorScore + evolutionScore, 100.0)
    }
    
    /**
     * 진화 분석 수행
     */
    private fun performEvolutionAnalysis(evolution: TasteEvolution): EvolutionAnalysis {
        val dominantFlavors = evolution.dnaStrands
            .sortedByDescending { it.frequency * it.intensity }
            .take(3)
            .map { it.primaryFlavor }
        
        val emergingFlavors = evolution.dnaStrands
            .filter { it.evolutionLevel >= 3 && it.frequency >= 2 }
            .map { it.primaryFlavor }
        
        val decliningFlavors = evolution.dnaStrands
            .filter { it.evolutionLevel <= 2 && it.frequency <= 1 }
            .map { it.primaryFlavor }
        
        val personalityType = determinePersonalityType(evolution)
        val evolutionTrend = determineEvolutionTrend(evolution)
        
        val progressToNextStage = calculateProgressToNextStage(evolution)
        
        val nextMilestones = generateNextMilestones(evolution)
        
        return EvolutionAnalysis(
            currentStage = evolution.evolutionStage,
            progressToNextStage = progressToNextStage,
            dominantFlavors = dominantFlavors,
            emergingFlavors = emergingFlavors,
            decliningFlavors = decliningFlavors,
            personalityType = personalityType,
            evolutionTrend = evolutionTrend,
            nextMilestones = nextMilestones
        )
    }
    
    /**
     * 테이스트 성격 유형 결정
     */
    private fun determinePersonalityType(evolution: TasteEvolution): TastePersonality {
        val dominantFlavors = evolution.dnaStrands.take(3).map { it.primaryFlavor }
        
        return when {
            dominantFlavors.contains(Flavor.PEAT) && dominantFlavors.contains(Flavor.CHAR) -> 
                TastePersonality.SMOKY_WARRIOR
            dominantFlavors.contains(Flavor.HONEY) && dominantFlavors.contains(Flavor.VANILLA) -> 
                TastePersonality.SWEET_DREAMER
            dominantFlavors.contains(Flavor.MALT) && dominantFlavors.contains(Flavor.WOOD) -> 
                TastePersonality.CLASSIC_CONNOISSEUR
            evolution.uniqueFlavors >= 8 -> 
                TastePersonality.ADVENTUROUS_EXPLORER
            evolution.uniqueFlavors <= 4 -> 
                TastePersonality.MINIMALIST_PURIST
            else -> 
                TastePersonality.BALANCED_MASTER
        }
    }
    
    /**
     * 진화 트렌드 결정
     */
    private fun determineEvolutionTrend(evolution: TasteEvolution): EvolutionTrend {
        val recentEvolutionPoints = evolution.evolutionPoints.filter { 
            System.currentTimeMillis() - it.achievedDate < 30L * 24 * 60 * 60 * 1000 // 최근 30일
        }
        
        return when {
            recentEvolutionPoints.size >= 5 -> EvolutionTrend.RAPID_GROWTH
            recentEvolutionPoints.size >= 2 -> EvolutionTrend.STEADY_PROGRESS
            recentEvolutionPoints.any { it.type == EvolutionPointType.FLAVOR_DISCOVERY } -> EvolutionTrend.EXPLORATION_PHASE
            evolution.evolutionStage == EvolutionStage.BUTTERFLY -> EvolutionTrend.MASTERY_PHASE
            else -> EvolutionTrend.STAGNATION
        }
    }
    
    /**
     * 다음 단계까지의 진행률 계산
     */
    private fun calculateProgressToNextStage(evolution: TasteEvolution): Double {
        val nextStage = evolution.evolutionStage.nextStage ?: return 1.0
        val currentProgress = evolution.totalNotes.toDouble()
        val requiredProgress = nextStage.requiredNotes.toDouble()
        
        return (currentProgress / requiredProgress).coerceAtMost(1.0)
    }
    
    /**
     * 다음 마일스톤 생성
     */
    private fun generateNextMilestones(evolution: TasteEvolution): List<EvolutionPoint> {
        val milestones = mutableListOf<EvolutionPoint>()
        
        // 다음 진화 단계
        val nextStage = evolution.evolutionStage.nextStage
        if (nextStage != null) {
            val remainingNotes = nextStage.requiredNotes - evolution.totalNotes
            if (remainingNotes > 0) {
                milestones.add(
                    EvolutionPoint(
                        type = EvolutionPointType.STAGE_EVOLUTION,
                        title = "${nextStage.displayName}로 진화",
                        description = "${remainingNotes}개의 노트가 더 필요합니다",
                        achievedDate = 0
                    )
                )
            }
        }
        
        // 플레이버 마스터리 목표
        val lowFrequencyFlavors = evolution.dnaStrands.filter { it.frequency < 3 }
        lowFrequencyFlavors.forEach { strand ->
            milestones.add(
                EvolutionPoint(
                    type = EvolutionPointType.FLAVOR_MASTERY,
                    title = "${strand.primaryFlavor.displayName} 마스터",
                    description = "${3 - strand.frequency}번 더 선택하세요",
                    achievedDate = 0,
                    flavor = strand.primaryFlavor
                )
            )
        }
        
        return milestones.take(3) // 최대 3개까지만 표시
    }
    
    /**
     * 수동으로 진화 데이터 새로고침
     */
    fun refreshEvolutionData() {
        loadEvolutionData()
    }
}

/**
 * 진화 화면의 UI 상태
 */
data class EvolutionState(
    val evolution: TasteEvolution? = null,
    val analysis: EvolutionAnalysis? = null,
    val isLoading: Boolean = true
)

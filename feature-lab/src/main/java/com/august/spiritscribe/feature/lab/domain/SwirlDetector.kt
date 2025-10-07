package com.august.spiritscribe.feature.lab.domain

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Size
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.video.Video
import kotlin.math.sqrt

/**
 * OpenCV Farneback 광류를 사용한 스월 감지기
 * 
 * 참고: https://docs.opencv.org/4.x/d4/dee/tutorial_optical_flow.html
 * Farneback 알고리즘으로 조밀한 광류를 계산하여 스월 강도를 측정
 */
class SwirlDetector(
    private val config: SwirlCaptureConfig = SwirlCaptureConfig()
) {
    private var prevGray: Mat? = null
    private var isInitialized = false
    
    // 스월 감지 상태 관리
    private val _detectionState = MutableStateFlow(SwirlDetectionState.IDLE)
    val detectionState: StateFlow<SwirlDetectionState> = _detectionState.asStateFlow()
    
    // 스월 스코어 스트림
    private val _swirlScore = MutableStateFlow(0f)
    val swirlScore: StateFlow<Float> = _swirlScore.asStateFlow()
    
    // 스월 감지 결과 스트림
    private val _detectionResult = MutableStateFlow<SwirlDetectionResult?>(null)
    val detectionResult: StateFlow<SwirlDetectionResult?> = _detectionResult.asStateFlow()
    
    // 상태 추적 변수들
    private var aboveThresholdStartTime = 0L
    private var isAboveThreshold = false
    private var captureStartTime = 0L
    
    // ROI (관심 영역) 계산
    private var currentRoi: Rect? = null
    
    /**
     * 이미지 프록시에서 프레임을 분석
     */
    fun analyzeFrame(imageProxy: ImageProxy, frameSize: Size) {
        try {
            val grayMat = convertImageProxyToGrayMat(imageProxy)
            val roi = calculateRoi(frameSize)
            currentRoi = roi
            
            if (!isInitialized) {
                prevGray = grayMat.clone()
                isInitialized = true
                return
            }
            
            // ROI 영역만 추출
            val prevRoi = extractRoi(prevGray!!, roi)
            val currRoi = extractRoi(grayMat, roi)
            
            // Farneback 광류 계산
            val flow = Mat()
            Video.calcOpticalFlowFarneback(
                prevRoi, currRoi, flow,
                0.5,    // pyramid scale
                3,      // pyramid levels
                15,     // window size
                3,      // iterations
                5,      // neighborhood size
                1.2,    // polynomial expansion
                0       // flags
            )
            
            // 광류 크기 계산
            val swirlScore = calculateFlowMagnitude(flow)
            updateSwirlScore(swirlScore)
            
            // 메모리 정리
            prevRoi.release()
            currRoi.release()
            flow.release()
            prevGray?.release()
            
            prevGray = grayMat.clone()
            
        } catch (e: Exception) {
            // 오류 처리
            _detectionState.value = SwirlDetectionState.IDLE
        }
    }
    
    /**
     * ImageProxy를 Grayscale Mat으로 변환
     */
    private fun convertImageProxyToGrayMat(imageProxy: ImageProxy): Mat {
        val yBuffer = imageProxy.planes[0].buffer
        val ySize = yBuffer.remaining()
        val yArray = ByteArray(ySize)
        yBuffer.get(yArray)
        
        val mat = Mat(imageProxy.height, imageProxy.width, CvType.CV_8UC1)
        mat.put(0, 0, yArray)
        
        return mat
    }
    
    /**
     * 화면 중앙에 원형 ROI 계산
     */
    private fun calculateRoi(frameSize: Size): Rect {
        val centerX = frameSize.width / 2
        val centerY = frameSize.height / 2
        val roiRadius = (minOf(frameSize.width, frameSize.height) * config.roiSize / 2).toInt()
        
        return Rect(
            centerX - roiRadius,
            centerY - roiRadius,
            roiRadius * 2,
            roiRadius * 2
        )
    }
    
    /**
     * Mat에서 ROI 영역 추출
     */
    private fun extractRoi(mat: Mat, roi: Rect): Mat {
        val roiRect = org.opencv.core.Rect(roi.left, roi.top, roi.width, roi.height)
        return Mat(mat, roiRect)
    }
    
    /**
     * 광류 벡터의 크기 평균 계산
     */
    private fun calculateFlowMagnitude(flow: Mat): Float {
        val flowChannels = mutableListOf<Mat>()
        Core.split(flow, flowChannels)
        
        val magnitude = Mat()
        Core.magnitude(flowChannels[0], flowChannels[1], magnitude)
        
        val meanScalar = Core.mean(magnitude)
        val meanMagnitude = meanScalar.`val`[0].toFloat()
        
        // 메모리 정리
        flowChannels.forEach { it.release() }
        magnitude.release()
        
        return meanMagnitude
    }
    
    /**
     * 스월 스코어 업데이트 및 상태 관리
     */
    private fun updateSwirlScore(score: Float) {
        _swirlScore.value = score
        
        val currentTime = System.currentTimeMillis()
        val isCurrentlyAboveThreshold = score > config.threshold
        
        when {
            // 임계치를 처음 초과한 경우
            !isAboveThreshold && isCurrentlyAboveThreshold -> {
                isAboveThreshold = true
                aboveThresholdStartTime = currentTime
                _detectionState.value = SwirlDetectionState.DETECTING
            }
            
            // 임계치를 계속 초과하고 있는 경우
            isAboveThreshold && isCurrentlyAboveThreshold -> {
                val duration = currentTime - aboveThresholdStartTime
                
                // 지속 시간이 충분하면 캡처 시작
                if (duration >= config.durationMs && _detectionState.value != SwirlDetectionState.CAPTURING) {
                    _detectionState.value = SwirlDetectionState.CAPTURING
                    captureStartTime = currentTime
                }
            }
            
            // 임계치를 벗어난 경우
            isAboveThreshold && !isCurrentlyAboveThreshold -> {
                isAboveThreshold = false
                _detectionState.value = SwirlDetectionState.IDLE
            }
        }
        
        // 캡처 중인 경우
        if (_detectionState.value == SwirlDetectionState.CAPTURING) {
            val captureDuration = currentTime - captureStartTime
            
            // 캡처 완료
            if (captureDuration >= config.captureDurationMs) {
                _detectionState.value = SwirlDetectionState.PROCESSING
                _detectionResult.value = SwirlDetectionResult(
                    swirlScore = score,
                    isActive = true,
                    timestamp = currentTime
                )
            }
        }
        
        // 일반적인 감지 결과 업데이트
        _detectionResult.value = SwirlDetectionResult(
            swirlScore = score,
            isActive = isCurrentlyAboveThreshold,
            timestamp = currentTime
        )
    }
    
    /**
     * ROI 정보 반환
     */
    fun getCurrentRoi(): Rect? = currentRoi
    
    /**
     * 감지기 리셋
     */
    fun reset() {
        prevGray?.release()
        prevGray = null
        isInitialized = false
        isAboveThreshold = false
        aboveThresholdStartTime = 0L
        captureStartTime = 0L
        currentRoi = null
        _detectionState.value = SwirlDetectionState.IDLE
        _swirlScore.value = 0f
        _detectionResult.value = null
    }
    
    /**
     * 리소스 정리
     */
    fun release() {
        reset()
    }
}

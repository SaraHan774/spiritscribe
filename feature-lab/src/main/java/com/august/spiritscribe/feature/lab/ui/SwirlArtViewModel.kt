package com.august.spiritscribe.feature.lab.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.video.VideoCapture
import androidx.camera.video.Recorder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.feature.lab.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoaderCallback
import org.opencv.android.BaseLoaderCallback
import javax.inject.Inject

/**
 * 스월 아트 화면의 ViewModel
 * CameraX + OpenCV + TensorFlow Lite를 통합한 스월 감지 및 스타일 전이 관리
 */
@HiltViewModel
class SwirlArtViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    // 기본 상태 관리
    private val _swirlArtState = MutableStateFlow(SwirlArtState())
    val swirlArtState: StateFlow<SwirlArtState> = _swirlArtState.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _selectedStyle = MutableStateFlow(SwirlArtStyle.NEON)
    val selectedStyle: StateFlow<SwirlArtStyle> = _selectedStyle.asStateFlow()

    // 스월 감지 관련
    private val _detectionState = MutableStateFlow(SwirlDetectionState.IDLE)
    val detectionState: StateFlow<SwirlDetectionState> = _detectionState.asStateFlow()

    private val _swirlScore = MutableStateFlow(0f)
    val swirlScore: StateFlow<Float> = _swirlScore.asStateFlow()

    private val _currentRoi = MutableStateFlow<Rect?>(null)
    val currentRoi: StateFlow<Rect?> = _currentRoi.asStateFlow()

    // 컴포넌트들
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var swirlDetector: SwirlDetector? = null
    private var styleTransferEngine: StyleTransferEngine? = null
    private var boomerangEncoder: BoomerangEncoder? = null

    // OpenCV 초기화 콜백
    private val openCVLoaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                org.opencv.android.LoaderCallbackInterface.SUCCESS -> {
                    initializeComponents()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    init {
        initializeOpenCV()
    }

    /**
     * OpenCV 초기화
     */
    private fun initializeOpenCV() {
        if (!org.opencv.android.OpenCVLoader.initDebug()) {
            org.opencv.android.OpenCVLoader.initAsync(
                org.opencv.android.OpenCVLoader.OPENCV_VERSION,
                context,
                openCVLoaderCallback
            )
        } else {
            openCVLoaderCallback.onManagerConnected(org.opencv.android.LoaderCallbackInterface.SUCCESS)
        }
    }

    /**
     * 컴포넌트들 초기화
     */
    private fun initializeComponents() {
        viewModelScope.launch {
            try {
                // 스월 감지기 초기화
                swirlDetector = SwirlDetector()
                
                // 스타일 전이 엔진 초기화
                styleTransferEngine = StyleTransferEngine(context)
                styleTransferEngine?.initialize()
                
                // 보머랭 인코더 초기화
                boomerangEncoder = BoomerangEncoder()
                
                // 스월 감지 상태 관찰
                observeSwirlDetection()
                
            } catch (e: Exception) {
                _swirlArtState.value = _swirlArtState.value.copy(
                    error = "컴포넌트 초기화 실패: ${e.message}"
                )
            }
        }
    }

    /**
     * 스월 감지 상태 관찰
     */
    private fun observeSwirlDetection() {
        viewModelScope.launch {
            swirlDetector?.detectionState?.collect { state ->
                _detectionState.value = state
                
                when (state) {
                    SwirlDetectionState.CAPTURING -> {
                        startVideoCapture()
                    }
                    SwirlDetectionState.PROCESSING -> {
                        stopVideoCapture()
                        processCapturedVideo()
                    }
                    else -> {
                        // 다른 상태 처리
                    }
                }
            }
        }
        
        viewModelScope.launch {
            swirlDetector?.swirlScore?.collect { score ->
                _swirlScore.value = score
            }
        }
        
        viewModelScope.launch {
            swirlDetector?.detectionResult?.collect { result ->
                result?.let {
                    _currentRoi.value = swirlDetector?.getCurrentRoi()
                }
            }
        }
    }

    /**
     * 카메라 권한이 허용되었을 때 호출
     */
    fun onPermissionGranted() {
        _swirlArtState.value = _swirlArtState.value.copy(
            hasCameraPermission = true
        )
    }

    /**
     * 카메라 권한이 거부되었을 때 호출
     */
    fun onPermissionDenied() {
        _swirlArtState.value = _swirlArtState.value.copy(
            hasCameraPermission = false,
            error = "카메라 권한이 필요합니다"
        )
    }

    /**
     * 카메라 컴포넌트 설정
     */
    fun setCameraComponents(
        camera: Camera,
        preview: Preview,
        videoCapture: VideoCapture<Recorder>
    ) {
        this.camera = camera
        this.preview = preview
        this.videoCapture = videoCapture
    }

    /**
     * 이미지 분석 (CameraX ImageAnalysis에서 호출)
     */
    fun analyzeFrame(imageProxy: ImageProxy, frameSize: Size) {
        swirlDetector?.analyzeFrame(imageProxy, frameSize)
    }

    /**
     * 스타일 선택
     */
    fun selectStyle(style: SwirlArtStyle) {
        _selectedStyle.value = style
    }

    /**
     * 수동 녹화 시작
     */
    fun startRecording() {
        viewModelScope.launch {
            try {
                _isRecording.value = true
                _swirlArtState.value = _swirlArtState.value.copy(
                    isRecording = true,
                    recordingStartTime = System.currentTimeMillis()
                )
                
                // 수동 녹화 시작 로직
                startVideoCapture()
                
            } catch (e: Exception) {
                _swirlArtState.value = _swirlArtState.value.copy(
                    error = "녹화를 시작할 수 없습니다: ${e.message}"
                )
                _isRecording.value = false
            }
        }
    }

    /**
     * 수동 녹화 중지
     */
    fun stopRecording() {
        viewModelScope.launch {
            try {
                _isRecording.value = false
                _swirlArtState.value = _swirlArtState.value.copy(
                    isRecording = false,
                    recordingEndTime = System.currentTimeMillis()
                )
                
                // 수동 녹화 중지 로직
                stopVideoCapture()
                
            } catch (e: Exception) {
                _swirlArtState.value = _swirlArtState.value.copy(
                    error = "녹화를 중지할 수 없습니다: ${e.message}"
                )
            }
        }
    }

    /**
     * 비디오 캡처 시작
     */
    private fun startVideoCapture() {
        // TODO: 실제 비디오 캡처 로직 구현
        // videoCapture?.startRecording(...)
    }

    /**
     * 비디오 캡처 중지
     */
    private fun stopVideoCapture() {
        // TODO: 실제 비디오 캡처 중지 로직 구현
        // videoCapture?.stopRecording()
    }

    /**
     * 캡처된 비디오 처리
     */
    private suspend fun processCapturedVideo() {
        try {
            _swirlArtState.value = _swirlArtState.value.copy(
                isGenerating = true
            )
            
            // TODO: 실제 비디오 처리 로직 구현
            // 1. 비디오에서 프레임 추출
            // 2. 스타일 전이 적용
            // 3. 보머랭 비디오 생성
            
            val generatedArt = performStyleTransferAndBoomerang()
            
            _swirlArtState.value = _swirlArtState.value.copy(
                isGenerating = false,
                generatedArtPath = generatedArt
            )
            
        } catch (e: Exception) {
            _swirlArtState.value = _swirlArtState.value.copy(
                isGenerating = false,
                error = "비디오 처리에 실패했습니다: ${e.message}"
            )
        }
    }

    /**
     * 스타일 전이 및 보머랭 처리
     */
    private suspend fun performStyleTransferAndBoomerang(): String {
        // TODO: 실제 구현
        // 1. 캡처된 비디오에서 프레임 추출
        // 2. 스타일 전이 적용
        // 3. 보머랭 비디오 생성
        
        return "generated_art_${System.currentTimeMillis()}.mp4"
    }

    /**
     * 스월 아트 생성 (수동)
     */
    fun generateSwirlArt() {
        viewModelScope.launch {
            try {
                _swirlArtState.value = _swirlArtState.value.copy(
                    isGenerating = true
                )
                
                // 현재 캡처된 비디오가 있다면 처리
                val recordedPath = _swirlArtState.value.recordedVideoPath
                if (recordedPath != null) {
                    val generatedArt = performStyleTransferAndBoomerang()
                    
                    _swirlArtState.value = _swirlArtState.value.copy(
                        isGenerating = false,
                        generatedArtPath = generatedArt
                    )
                } else {
                    _swirlArtState.value = _swirlArtState.value.copy(
                        isGenerating = false,
                        error = "처리할 비디오가 없습니다"
                    )
                }
                
            } catch (e: Exception) {
                _swirlArtState.value = _swirlArtState.value.copy(
                    isGenerating = false,
                    error = "아트 생성에 실패했습니다: ${e.message}"
                )
            }
        }
    }

    /**
     * 스월 감지기 리셋
     */
    fun resetSwirlDetection() {
        swirlDetector?.reset()
        _detectionState.value = SwirlDetectionState.IDLE
        _swirlScore.value = 0f
        _currentRoi.value = null
    }

    /**
     * 에러 상태 초기화
     */
    fun clearError() {
        _swirlArtState.value = _swirlArtState.value.copy(
            error = null
        )
    }

    /**
     * 리소스 정리
     */
    override fun onCleared() {
        super.onCleared()
        
        // 컴포넌트들 정리
        swirlDetector?.release()
        styleTransferEngine?.release()
        
        // 카메라 정리
        camera = null
        preview = null
        videoCapture = null
        swirlDetector = null
        styleTransferEngine = null
        boomerangEncoder = null
    }
}

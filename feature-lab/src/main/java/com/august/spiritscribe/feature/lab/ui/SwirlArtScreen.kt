package com.august.spiritscribe.feature.lab.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.feature.lab.ui.components.StyleSelector
import com.august.spiritscribe.feature.lab.ui.components.RecordingIndicator
import com.august.spiritscribe.feature.lab.ui.components.SwirlDetectionOverlay
import com.august.spiritscribe.feature.lab.ui.components.SwirlScoreIndicator
import com.august.spiritscribe.feature.lab.domain.SwirlArtStyle
import com.august.spiritscribe.feature.lab.domain.SwirlArtState
import com.august.spiritscribe.feature.lab.domain.SwirlDetectionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 스월 아트 촬영 화면
 * 위스키 잔의 소용돌이를 촬영하고 스타일 전이를 적용하는 화면
 */
@Composable
fun SwirlArtScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onArtGenerated: (String) -> Unit,
    viewModel: SwirlArtViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val swirlArtState by viewModel.swirlArtState.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val detectionState by viewModel.detectionState.collectAsState()
    val swirlScore by viewModel.swirlScore.collectAsState()
    val currentRoi by viewModel.currentRoi.collectAsState()

    // 카메라 권한 요청
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    // 카메라 권한 확인
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.onPermissionGranted()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 카메라 프리뷰
        if (swirlArtState.hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreview(
                    onCameraReady = { camera, preview, videoCapture ->
                        viewModel.setCameraComponents(camera, preview, videoCapture)
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // 스월 감지 오버레이
                SwirlDetectionOverlay(
                    detectionState = detectionState,
                    swirlScore = swirlScore,
                    roi = currentRoi,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // 권한이 없을 때 표시할 화면
            PermissionDeniedScreen(
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }

        // 상단 컨트롤
        SwirlArtTopControls(
            onBack = onBack,
            onStyleChange = { style ->
                viewModel.selectStyle(style)
            },
            selectedStyle = selectedStyle,
            swirlScore = swirlScore,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        // 하단 컨트롤
        SwirlArtBottomControls(
            isRecording = isRecording,
            onStartRecording = {
                viewModel.startRecording()
            },
            onStopRecording = {
                viewModel.stopRecording()
            },
            onGenerateArt = {
                viewModel.generateSwirlArt()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        // 녹화 인디케이터
        if (isRecording) {
            RecordingIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun CameraPreview(
    onCameraReady: (Camera, Preview, VideoCapture<Recorder>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build()
                val videoCapture = VideoCapture.Builder(
                    Recorder.Builder().build()
                ).build()

                // 이미지 분석 설정 (스월 감지용)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx)
                        ) { imageProxy ->
                            // 스월 감지 분석
                            viewModel.analyzeFrame(imageProxy, android.util.Size(imageProxy.width, imageProxy.height))
                            imageProxy.close()
                        }
                    }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        videoCapture,
                        imageAnalysis
                    )
                    
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    onCameraReady(camera, preview, videoCapture)
                    
                } catch (e: Exception) {
                    // 카메라 초기화 실패 처리
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = modifier
    )
}

@Composable
private fun PermissionDeniedScreen(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "카메라",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "카메라 권한이 필요합니다",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "스월 아트를 촬영하기 위해\n카메라 권한을 허용해주세요",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFB0BEC5)
            )
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7)
                )
            ) {
                Text("권한 허용")
            }
        }
    }
}

@Composable
private fun SwirlArtTopControls(
    onBack: () -> Unit,
    onStyleChange: (SwirlArtStyle) -> Unit,
    selectedStyle: SwirlArtStyle,
    swirlScore: Float = 0f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기 버튼
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color.White
            )
        }

        // 스월 강도 표시기 (중앙)
        SwirlScoreIndicator(
            swirlScore = swirlScore,
            modifier = Modifier.background(
                Color.Black.copy(alpha = 0.5f),
                CircleShape
            )
        )

        // 스타일 선택기
        StyleSelector(
            selectedStyle = selectedStyle,
            onStyleChange = onStyleChange
        )
    }
}

@Composable
private fun SwirlArtBottomControls(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onGenerateArt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아트 생성 버튼
        Button(
            onClick = onGenerateArt,
            enabled = !isRecording,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FC3F7)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "아트 생성",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("아트 생성")
        }

        // 녹화 버튼
        Button(
            onClick = if (isRecording) onStopRecording else onStartRecording,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color(0xFFE57373) else Color(0xFF4CAF50)
            ),
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                contentDescription = if (isRecording) "녹화 중지" else "녹화 시작",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // 갤러리 버튼
        IconButton(
            onClick = { /* TODO: 갤러리 열기 */ },
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "갤러리",
                tint = Color.White
            )
        }
    }
}

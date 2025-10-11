package com.august.spiritscribe.feature.lab.ui

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.feature.lab.domain.ARMixerState
import com.august.spiritscribe.feature.lab.domain.MixingRatio
import com.august.spiritscribe.feature.lab.ui.components.ARMixerControls
import com.august.spiritscribe.feature.lab.ui.components.ARMixerOverlay
import com.august.spiritscribe.feature.lab.ui.components.RatioSelector
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import io.github.sceneview.ar.ArSceneView

/**
 * AR 하이볼 믹서 화면
 * ARCore Depth API를 사용한 정확한 비율 라인과 액체 오클루전
 */
@Composable
fun ARMixerScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ARMixerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val arState by viewModel.arState.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val currentRatio by viewModel.currentRatio.collectAsState()
    val liquidLevel by viewModel.liquidLevel.collectAsState()
    val error by viewModel.error.collectAsState()

    // AR 세션 초기화
    LaunchedEffect(Unit) {
        viewModel.initializeAR(context)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // AR Scene View
        ARSceneView(
            modifier = Modifier.fillMaxSize(),
            onSessionCreated = { session ->
                viewModel.onSessionCreated(session)
            },
            onFrameUpdated = { frame ->
                viewModel.onFrameUpdated(frame)
            }
        )

        // AR 오버레이 (비율 라인, 상태 표시)
        ARMixerOverlay(
            isTracking = isTracking,
            currentRatio = currentRatio,
            liquidLevel = liquidLevel,
            error = error,
            modifier = Modifier.fillMaxSize()
        )

        // 상단 컨트롤
        ARMixerControls(
            currentRatio = currentRatio,
            onRatioChanged = { ratio ->
                viewModel.setMixingRatio(ratio)
            },
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // 하단 비율 선택기
        RatioSelector(
            selectedRatio = currentRatio,
            onRatioSelected = { ratio ->
                viewModel.setMixingRatio(ratio)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun ARSceneView(
    modifier: Modifier = Modifier,
    onSessionCreated: (Session) -> Unit,
    onFrameUpdated: (com.google.ar.core.Frame) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            val sceneView = ArSceneView(ctx)
            // AR 초기화는 ViewModel에서 처리
            sceneView
        },
        modifier = modifier
    )
}

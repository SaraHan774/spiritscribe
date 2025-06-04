package com.august.spiritscribe.ui.note

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.FlavorIntensity
import com.august.spiritscribe.ui.theme.SpiritScribeTheme
import com.august.spiritscribe.ui.theme.whiskey_amber
import com.august.spiritscribe.ui.theme.whiskey_gold
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun FlavorProfileGraph(
    modifier: Modifier = Modifier,
    profiles: List<FlavorIntensity> = Flavor.entries.map { FlavorIntensity(it, 0) },
    onProfileChange: (List<FlavorIntensity>) -> Unit = {}
) {
    var flavorProfiles by remember { mutableStateOf(profiles) }
    var useTouchInput by remember { mutableStateOf(true) }
    val textMeasurer = rememberTextMeasurer()
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompactScreen = screenWidth < 600.dp

    var canvasSize by remember { mutableFloatStateOf(0f) }
    var centerPoint by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableFloatStateOf(0f) }

    // Update local state when profiles change
    LaunchedEffect(profiles) {
        flavorProfiles = profiles
    }

    // Animation state
    val rotation by animateFloatAsState(
        targetValue = if (useTouchInput) 0f else 180f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "flip"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (isCompactScreen) 8.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (useTouchInput) "Touch Mode" else "Slider Mode",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (useTouchInput) Icons.Filled.TouchApp else Icons.Outlined.Tune,
                    contentDescription = if (useTouchInput) "Touch input mode" else "Slider input mode",
                    tint = colorScheme.primary
                )
                Switch(
                    checked = useTouchInput,
                    onCheckedChange = { useTouchInput = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.primary,
                        checkedTrackColor = colorScheme.primaryContainer,
                        uncheckedThumbColor = colorScheme.secondary,
                        uncheckedTrackColor = colorScheme.secondaryContainer
                    )
                )
            }
        }

        // Flip card container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            // Spider Graph (Front)
            if (rotation < 90f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 8 * density
                        }
                ) {
                    SpiderGraph(
                        flavorIntensities = flavorProfiles,
                        onProfileChange = { newProfiles ->
                            flavorProfiles = newProfiles
                            onProfileChange(newProfiles)
                        },
                        isCompactScreen = isCompactScreen,
                        useTouchInput = useTouchInput,
                        textMeasurer = textMeasurer,
                        colorScheme = colorScheme,
                        canvasSize = canvasSize,
                        centerPoint = centerPoint,
                        radius = radius,
                        onSizeChange = { size, center, rad ->
                            canvasSize = size
                            centerPoint = center
                            radius = rad
                        }
                    )
                }
            }

            // Sliders (Back)
            if (rotation >= 90f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = rotation - 180f
                            cameraDistance = 8 * density
                        }
                ) {
                    SliderPanel(
                        flavorIntensities = flavorProfiles,
                        onProfileChange = { newProfiles ->
                            flavorProfiles = newProfiles
                            onProfileChange(newProfiles)
                        },
                        isCompactScreen = isCompactScreen,
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
private fun SpiderGraph(
    flavorIntensities: List<FlavorIntensity>,
    onProfileChange: (List<FlavorIntensity>) -> Unit,
    isCompactScreen: Boolean,
    useTouchInput: Boolean,
    textMeasurer: TextMeasurer,
    colorScheme: ColorScheme,
    canvasSize: Float,
    centerPoint: Offset,
    radius: Float,
    onSizeChange: (Float, Offset, Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val newCanvasSize = size.width.toFloat()
                val newCenterPoint = Offset(size.width / 2f, size.height / 2f)
                val newRadius = min(newCenterPoint.x, newCenterPoint.y) * 0.8f
                onSizeChange(newCanvasSize, newCenterPoint, newRadius)
            }
            .then(
                if (useTouchInput) {
                    Modifier.pointerInput(flavorIntensities) {
                        detectTapGestures { offset ->
                            if (radius <= 0f) return@detectTapGestures
                            
                            val numberOfPoints = flavorIntensities.size
                            val angleStep = (2 * PI / numberOfPoints).toFloat()
                            
                            // Calculate angle from center to touch point
                            val touchAngle = atan2(
                                offset.y - centerPoint.y,
                                offset.x - centerPoint.x
                            )
                            
                            // Normalize angle to match our graph's orientation (top = 0)
                            var normalizedAngle = (touchAngle + PI / 2) % (2 * PI)
                            if (normalizedAngle < 0) normalizedAngle += 2 * PI
                            
                            // Calculate which spoke was touched
                            val spokeIndex = ((normalizedAngle / (2 * PI)) * numberOfPoints).toInt()
                            
                            // Calculate distance from center as a percentage of radius
                            val distanceFromCenter = sqrt(
                                (offset.x - centerPoint.x).pow(2) +
                                (offset.y - centerPoint.y).pow(2)
                            )
                            
                            // Convert distance to intensity level (0-5)
                            val newIntensity = ((distanceFromCenter / radius) * 5)
                                .coerceIn(0f, 5f)
                                .roundToInt()
                            
                            // Update the profile while preserving other values
                            if (spokeIndex in flavorIntensities.indices) {
                                val newProfiles = flavorIntensities.toMutableList()
                                newProfiles[spokeIndex] = newProfiles[spokeIndex].copy(
                                    intensity = newIntensity
                                )
                                onProfileChange(newProfiles)
                            }
                        }
                    }
                } else {
                    Modifier
                }
            )
    ) {
        val newCanvasSize = size.width
        val newCenterPoint = Offset(size.width / 2, size.height / 2)
        val newRadius = min(newCenterPoint.x, newCenterPoint.y) * 0.8f
        
        if (newCanvasSize != canvasSize || newCenterPoint != centerPoint || newRadius != radius) {
            onSizeChange(newCanvasSize, newCenterPoint, newRadius)
        }
        
        val numberOfPoints = flavorIntensities.size
        val angleStep = (2 * PI / numberOfPoints).toFloat()

        // Draw background web lines
        for (level in 1..5) {
            val currentRadius = radius * level / 5
            val path = Path()
            for (i in 0..numberOfPoints) {
                val angle = -PI.toFloat() / 2 + angleStep * i
                val x = centerPoint.x + currentRadius * cos(angle)
                val y = centerPoint.y + currentRadius * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = colorScheme.outline.copy(alpha = 0.2f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw spokes and labels
        for (i in 0 until numberOfPoints) {
            val angle = -PI.toFloat() / 2 + angleStep * i
            val endX = centerPoint.x + radius * cos(angle)
            val endY = centerPoint.y + radius * sin(angle)
            
            drawLine(
                color = colorScheme.outline.copy(alpha = 0.2f),
                start = centerPoint,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )

            val labelRadius = radius + (if (isCompactScreen) 15.dp else 20.dp).toPx()
            val labelX = centerPoint.x + labelRadius * cos(angle)
            val labelY = centerPoint.y + labelRadius * sin(angle)
            
            drawText(
                textMeasurer = textMeasurer,
                text = flavorIntensities[i].flavor.displayName,
                style = TextStyle(
                    fontSize = if (isCompactScreen) 10.sp else 12.sp,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface
                ),
                topLeft = Offset(
                    labelX - (if (isCompactScreen) 40.dp else 50.dp).toPx(),
                    labelY - (if (isCompactScreen) 6.dp else 8.dp).toPx()
                )
            )
        }

        // Draw flavor profile
        val path = Path()
        val points = flavorIntensities.mapIndexed { index, profile ->
            val angle = -PI.toFloat() / 2 + angleStep * index
            val currentRadius = radius * profile.intensity / 5
            val x = centerPoint.x + currentRadius * cos(angle)
            val y = centerPoint.y + currentRadius * sin(angle)
            Offset(x, y)
        }

        points.forEachIndexed { index, offset ->
            if (index == 0) path.moveTo(offset.x, offset.y)
            else path.lineTo(offset.x, offset.y)
        }
        path.close()

        // Only draw the gradient fill if we have a valid radius
        if (radius > 0f) {
            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(
                        whiskey_amber.copy(alpha = 0.3f),
                        whiskey_gold.copy(alpha = 0.1f)
                    ),
                    center = centerPoint,
                    radius = radius
                )
            )
        }

        drawPath(
            path = path,
            color = colorScheme.primary,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        points.forEach { point ->
            drawCircle(
                color = colorScheme.primary,
                radius = (if (isCompactScreen) 3.dp else 4.dp).toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun SliderPanel(
    flavorIntensities: List<FlavorIntensity>,
    onProfileChange: (List<FlavorIntensity>) -> Unit,
    isCompactScreen: Boolean,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        if (isCompactScreen) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flavorIntensities) { profile ->
                    FlavorSliderItem(
                        profile = profile,
                        onIntensityChange = { newIntensity ->
                            val index = flavorIntensities.indexOf(profile)
                            val newProfiles = flavorIntensities.toMutableList()
                            newProfiles[index] = profile.copy(intensity = newIntensity)
                            onProfileChange(newProfiles)
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                flavorIntensities.chunked(2).forEach { rowProfiles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowProfiles.forEach { profile ->
                            Box(modifier = Modifier.weight(1f)) {
                                FlavorSliderItem(
                                    profile = profile,
                                    onIntensityChange = { newIntensity ->
                                        val index = flavorIntensities.indexOf(profile)
                                        val newProfiles = flavorIntensities.toMutableList()
                                        newProfiles[index] = profile.copy(intensity = newIntensity)
                                        onProfileChange(newProfiles)
                                    }
                                )
                            }
                        }
                        if (rowProfiles.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlavorSliderItem(
    profile: FlavorIntensity,
    onIntensityChange: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = profile.flavor.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = profile.intensity.toFloat(),
                onValueChange = { value -> onIntensityChange(value.roundToInt()) },
                valueRange = 0f..5f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = colorScheme.primary,
                    activeTrackColor = colorScheme.primary,
                    inactiveTrackColor = colorScheme.primary.copy(alpha = 0.2f)
                )
            )
            Text(
                text = "${profile.intensity}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.width(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlavorProfileGraphPreview() {
    SpiritScribeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FlavorProfileGraph(
                profiles = Flavor.entries.map { 
                    FlavorIntensity(it, (0..5).random())
                }
            )
        }
    }
}

@Composable
private fun FlavorProfilePreview(
    modifier: Modifier = Modifier,
    profiles: List<FlavorIntensity>
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(profiles.filter { it.intensity > 0 }) { profile ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = "${profile.flavor.emoji} ${profile.intensity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
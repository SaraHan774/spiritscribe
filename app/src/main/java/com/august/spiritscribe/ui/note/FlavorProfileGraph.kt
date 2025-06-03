package com.august.spiritscribe.ui.note

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.FlavorProfile
import com.august.spiritscribe.ui.theme.SpiritScribeTheme
import com.august.spiritscribe.ui.theme.whiskey_amber
import com.august.spiritscribe.ui.theme.whiskey_gold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FlavorProfileGraph(
    modifier: Modifier = Modifier,
    profiles: List<FlavorProfile> = Flavor.entries.map { FlavorProfile(it, 0) },
    onProfileChange: (List<FlavorProfile>) -> Unit = {}
) {
    var flavorProfiles by remember { mutableStateOf(profiles) }
    val textMeasurer = rememberTextMeasurer()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Radar Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(24.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = minOf(centerX, centerY) * 0.8f
                val numberOfPoints = flavorProfiles.size
                val angleStep = (2 * PI / numberOfPoints).toFloat()

                // Draw background web lines
                for (level in 1..5) {
                    val currentRadius = radius * level / 5
                    val path = Path()
                    for (i in 0..numberOfPoints) {
                        val angle = -PI.toFloat() / 2 + angleStep * i
                        val x = centerX + currentRadius * cos(angle)
                        val y = centerY + currentRadius * sin(angle)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        color = colorScheme.outline.copy(alpha = 0.2f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw spokes
                for (i in 0 until numberOfPoints) {
                    val angle = -PI.toFloat() / 2 + angleStep * i
                    val endX = centerX + radius * cos(angle)
                    val endY = centerY + radius * sin(angle)
                    drawLine(
                        color = colorScheme.outline.copy(alpha = 0.2f),
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw labels
                    val labelRadius = radius + 20.dp.toPx()
                    val labelX = centerX + labelRadius * cos(angle)
                    val labelY = centerY + labelRadius * sin(angle)
                    
                    drawText(
                        textMeasurer = textMeasurer,
                        text = flavorProfiles[i].flavor.name,
                        style = TextStyle(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = colorScheme.onSurface
                        ),
                        topLeft = Offset(
                            labelX - 40.dp.toPx(),
                            labelY - 8.dp.toPx()
                        )
                    )
                }

                // Draw flavor profile
                val path = Path()
                val points = flavorProfiles.mapIndexed { index, profile ->
                    val angle = -PI.toFloat() / 2 + angleStep * index
                    val currentRadius = radius * profile.intensity / 5
                    val x = centerX + currentRadius * cos(angle)
                    val y = centerY + currentRadius * sin(angle)
                    Offset(x, y)
                }

                points.forEachIndexed { index, offset ->
                    if (index == 0) path.moveTo(offset.x, offset.y)
                    else path.lineTo(offset.x, offset.y)
                }
                path.close()

                // Draw filled area with gradient
                drawPath(
                    path = path,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            whiskey_amber.copy(alpha = 0.3f),
                            whiskey_gold.copy(alpha = 0.1f)
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius
                    )
                )

                // Draw outline
                drawPath(
                    path = path,
                    color = colorScheme.primary,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Draw points
                points.forEach { point ->
                    drawCircle(
                        color = colorScheme.primary,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        // Sliders
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Flavor Intensity",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            
            flavorProfiles.forEachIndexed { index, profile ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.flavor.name,
                        modifier = Modifier.width(100.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = profile.intensity.toFloat(),
                        onValueChange = { value ->
                            val newProfiles = flavorProfiles.toMutableList()
                            newProfiles[index] = profile.copy(intensity = value.toInt())
                            flavorProfiles = newProfiles
                            onProfileChange(newProfiles)
                        },
                        valueRange = 0f..5f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.primary,
                            activeTrackColor = colorScheme.primary,
                            inactiveTrackColor = colorScheme.primary.copy(alpha = 0.2f)
                        )
                    )
                    Text(
                        text = "${profile.intensity}",
                        modifier = Modifier.width(24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                FlavorProfileGraph(
                    profiles = Flavor.entries.map { 
                        FlavorProfile(it, (0..5).random()) 
                    }
                )
            }
        }
    }
}
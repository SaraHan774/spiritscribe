package com.august.spiritscribe.ui.note

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.august.spiritscribe.model.Flavor
import com.august.spiritscribe.model.FlavorProfile
import com.august.spiritscribe.ui.theme.AppTheme

// FIXME - 코드 재확인
@Composable
fun FlavorProfileGraph() {
    // Initialize flavor profiles with default intensities
    val flavors = Flavor.entries.map { FlavorProfile(it, 0) }
    val flavorProfiles = remember { mutableStateListOf(*flavors.toTypedArray()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Set Flavor Intensities", style = MaterialTheme.typography.headlineSmall)

        // Create sliders for each flavor
        flavorProfiles.forEach { profile ->
            FlavorIntensityInput(
                profile = profile,
                onInputChange = { value ->
                    flavorProfiles[profile.flavor.ordinal] = profile.copy(intensity = value)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Draw the graph
        Text("Flavor Intensity Graph", style = MaterialTheme.typography.headlineSmall)
        FlavorIntensityGraph(flavorProfiles)
    }
}

@Composable
fun FlavorIntensityInput(
    profile: FlavorProfile,
    onInputChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            "${profile.flavor}: ${profile.intensity}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = profile.intensity.toFloat(),
            onValueChange = { value: Float ->
                onInputChange(value.toInt())
            },
            valueRange = 0f..5f,
            steps = 4 // 5 steps for the intensity range 0 to 5
        )
    }
}

@Composable
fun FlavorIntensityGraph(profiles: List<FlavorProfile>) {
    val maxIntensity = 5 // Maximum intensity for scaling

    // Create a row to display bars
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        profiles.forEach { profile ->
            val barHeight = (profile.intensity / maxIntensity.toFloat()) * 150.dp.value

            // Draw each bar
            Canvas(
                modifier = Modifier
                    .width(20.dp)
                    .height(150.dp)
            ) {
                drawRect(
                    color = Color.Blue,
                    size = Size(
                        width = size.width,
                        height = barHeight
                    )
                )
            }
        }
    }

    // Add labels below the bars
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        profiles.forEach { profile ->
            Text(
                text = profile.flavor.name.take(3), // Shorten flavor names to 3 letters
                fontSize = 12.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FlavorProfileGraphPreview() {
    AppTheme {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            FlavorProfileGraph()
        }
    }
}
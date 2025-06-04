package com.august.spiritscribe.ui.whiskey

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.domain.model.WhiskeyNote
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WhiskeyDetailRoute(
    whiskeyId: String,
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WhiskeyDetailViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val whiskey by viewModel.whiskey.collectAsState()

    Log.d("WhiskeyDetailRoute", "whiskeyId = $whiskeyId, whiskey = $whiskey, notes = ${notes.size}")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
        ) {
            // Whiskey Header
            whiskey?.let { whiskey ->
                item {
                    WhiskeyHeader(
                        name = whiskey.name,
                        distillery = whiskey.distillery,
                        type = whiskey.type.name,
                        age = whiskey.age?.toString()?.plus(" Years") ?: "NAS",
                        abv = "${whiskey.abv}%",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Timeline Header
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "Your Tasting Journey",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            // Tasting Notes Timeline
            items(notes, key = { it.id }) { note ->
                WhiskeyNoteTimelineItem(
                    note = note,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateItem(
                            placementSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                )
            }
        }

        // Add Note FAB with extended label
        ExtendedFloatingActionButton(
            onClick = onAddNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            icon = { Icon(Icons.Default.Edit, "Add Note") },
            text = { Text("Add Tasting Note") }
        )
    }
}

@Composable
private fun WhiskeyHeader(
    name: String,
    distillery: String,
    type: String,
    age: String,
    abv: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = distillery,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WhiskeyInfoChip(text = type)
            WhiskeyInfoChip(text = age)
            WhiskeyInfoChip(text = abv)
        }
    }
}

@Composable
private fun WhiskeyNoteTimelineItem(
    note: WhiskeyNote,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Timeline connector
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(2.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .align(Alignment.CenterHorizontally)
        )

        // Note card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Date and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DateTimeFormatter.ofPattern("MMM d, yyyy").format(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(note.createdAt),
                                ZoneId.systemDefault()
                            )
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${note.finalRating.overall}/100",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Note content
                Text(
                    text = note.additionalNotes,
                    style = MaterialTheme.typography.bodyLarge
                )

                // Flavor tags
                if (note.flavors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        note.flavors.forEach { flavor ->
                            WhiskeyInfoChip(text = "${flavor.flavor.displayName} ${flavor.intensity}★")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhiskeyInfoChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
} 
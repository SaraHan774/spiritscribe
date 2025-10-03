package com.august.spiritscribe.ui.feed

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onWhiskeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedItems by viewModel.feedItems.collectAsState()

    when {
            viewModel.isLoading -> {
                LoadingContent()
            }
            viewModel.error != null -> {
                ErrorContent(
                    error = viewModel.error!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            feedItems.isEmpty() -> {
                EmptyFeedContent()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(feedItems.size) { index ->
                        val item = feedItems[index]
                        WhiskeyFeedItem(
                            item = item,
                            onWhiskeyClick = onWhiskeyClick
                        )
                    }
                }
            }
        }
}

@Composable
private fun WhiskeyFeedItem(
    item: WhiskeyFeedItem,
    onWhiskeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "card_scale"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onWhiskeyClick(item.id)
            },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                getWhiskeyTypeColor(item.type).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Whiskey Type Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = getWhiskeyTypeColor(item.type),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = item.type,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Whiskey name
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Distillery with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.distillery,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Region (if available)
                        item.region?.let { region ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = region,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Rating badge (if available)
                    item.rating?.let { rating ->
                        Surface(
                            shape = CircleShape,
                            color = getRatingColor(rating),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "$rating",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Content section
            Column(modifier = Modifier.padding(20.dp)) {
                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Details chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ABV
                    InfoChip(
                        label = "${item.abv}%",
                        icon = Icons.Default.Science,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    
                    // Age (if available)
                    item.age?.let { age ->
                        InfoChip(
                            label = "${age}년",
                            icon = Icons.Default.Schedule,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    
                    // Year (if available)
                    item.year?.let { year ->
                        InfoChip(
                            label = "$year",
                            icon = Icons.Default.CalendarToday,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bottom section with price and notes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price (if available)
                    item.price?.let { price ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = String.format("%.0f", price),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Note count with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${item.noteCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "노트",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun EmptyFeedContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalBar,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "위스키가 없습니다",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "위스키 데이터가 로드되지 않았습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun getWhiskeyTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "bourbon" -> Color(0xFFD2691E) // Chocolate
        "scotch", "singlemalt" -> Color(0xFF8B4513) // SaddleBrown
        "japanese" -> Color(0xFF228B22) // ForestGreen
        "irish", "irishpotstill" -> Color(0xFF32CD32) // LimeGreen
        "rye" -> Color(0xFF8B0000) // DarkRed
        "blended", "blendedmalt" -> Color(0xFF4169E1) // RoyalBlue
        "taiwanese" -> Color(0xFF9932CC) // DarkOrchid
        "singlegrain" -> Color(0xFFDAA520) // GoldenRod
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun getRatingColor(rating: Int): Color {
    return when {
        rating >= 90 -> Color(0xFF4CAF50) // Green
        rating >= 80 -> Color(0xFF2196F3) // Blue
        rating >= 70 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m"
        diff < 86400_000 -> "${diff / 3600_000}h"
        diff < 604800_000 -> "${diff / 86400_000}d"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
} 
package com.august.spiritscribe.ui.whiskey

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.ui.theme.SpiritScribeTheme
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyType
import com.august.spiritscribe.domain.model.FinalRating
import com.august.spiritscribe.domain.model.FlavorIntensity
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.ColorMeter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WhiskeyDetailRoute(
    whiskeyId: String,
    onAddNote: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WhiskeyDetailViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val whiskey by viewModel.whiskey.collectAsState()
    
    Log.d("WhiskeyDetailRoute", "whiskeyId = $whiskeyId, whiskey = $whiskey, notes = ${notes.size}")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    // Empty title for clean look
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNote,
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                text = { Text("테이스팅 노트 추가") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 80.dp
            )
        ) {
            // Hero Section with Whiskey Details
            whiskey?.let { whiskey ->
                item {
                    WhiskeyHeroSection(
                        whiskey = whiskey,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Stats Section
            whiskey?.let { whiskey ->
                item {
                    WhiskeyStatsSection(
                        whiskey = whiskey,
                        noteCount = notes.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp) // Reduced padding
                    )
                }
            }

            // Twitter-style Timeline Header
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "테이스팅 여정",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "${notes.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Tasting Notes Timeline or Empty State
            if (notes.isEmpty()) {
                item {
                    EmptyNotesMessage(
                        onAddNote = onAddNote,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            } else {
                itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
                    WhiskeyNoteTimelineItem(
                        note = note,
                        isFirst = index == 0,
                        isLast = index == notes.lastIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp) // Natural spacing between items
                            .animateItem(
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun WhiskeyHeroSection(
    whiskey: com.august.spiritscribe.domain.model.Whiskey,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(240.dp) // Reduced from 320.dp to 240.dp
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        getWhiskeyTypeColor(whiskey.type.name).copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Background Pattern (optional)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), // Reduced from 24.dp to 20.dp
            verticalArrangement = Arrangement.Center
        ) {
            // Whiskey Type Badge
            Surface(
                shape = RoundedCornerShape(16.dp), // Reduced from 20.dp to 16.dp
                color = getWhiskeyTypeColor(whiskey.type.name),
                modifier = Modifier.padding(bottom = 12.dp) // Reduced from 16.dp to 12.dp
            ) {
                Text(
                    text = whiskey.type.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), // Reduced padding
                    style = MaterialTheme.typography.labelMedium, // Reduced from labelLarge
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Whiskey Name
            Text(
                text = whiskey.name,
                style = MaterialTheme.typography.headlineMedium, // Reduced from displaySmall
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 32.sp // Reduced from 40.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp)) // Reduced from 8.dp
            
            // Distillery with icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp), // Reduced from 20.dp
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp)) // Reduced from 8.dp
                Text(
                    text = whiskey.distillery,
                    style = MaterialTheme.typography.titleMedium, // Reduced from titleLarge
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Region (if available)
            whiskey.region?.let { region ->
                Spacer(modifier = Modifier.height(3.dp)) // Reduced from 4.dp
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp), // Reduced from 18.dp
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp)) // Reduced from 8.dp
                    Text(
                        text = region,
                        style = MaterialTheme.typography.bodyMedium, // Reduced from bodyLarge
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp)) // Reduced from 20.dp
            
            // Description
            Text(
                text = whiskey.description,
                style = MaterialTheme.typography.bodyMedium, // Reduced from bodyLarge
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp, // Reduced from 24.sp
                maxLines = 2, // Added line limit
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WhiskeyStatsSection(
    whiskey: com.august.spiritscribe.domain.model.Whiskey,
    noteCount: Int,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp), // Reduced from 8.dp
        shape = RoundedCornerShape(16.dp) // Reduced from 20.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Reduced from 20.dp
        ) {
            Text(
                text = "위스키 정보",
                style = MaterialTheme.typography.titleMedium, // Reduced from titleLarge
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp) // Reduced from 16.dp
            )
            
            // Single row with all stats - more compact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Reduced from 12.dp
            ) {
                // ABV
                StatItem(
                    icon = Icons.Default.Science,
                    label = "ABV",
                    value = "${whiskey.abv}%",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    compact = true
                )
                
                // Age
                StatItem(
                    icon = Icons.Default.Schedule,
                    label = "연령",
                    value = whiskey.age?.let { "${it}년" } ?: "NAS",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    compact = true
                )
                
                // Year or Rating
                whiskey.rating?.let { rating ->
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "평점",
                        value = "$rating",
                        color = getRatingColor(rating),
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                } ?: whiskey.year?.let { year ->
                    StatItem(
                        icon = Icons.Default.CalendarToday,
                        label = "연도",
                        value = "$year",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                }
                
                // Note Count
                StatItem(
                    icon = Icons.Default.SpeakerNotes,
                    label = "노트",
                    value = "$noteCount",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f),
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 12.dp else 16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .padding(if (compact) 10.dp else 16.dp) // Reduced padding for compact mode
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(if (compact) 18.dp else 24.dp), // Smaller icon for compact
                tint = color
            )
            Spacer(modifier = Modifier.height(if (compact) 4.dp else 8.dp)) // Reduced spacing
            Text(
                text = value,
                style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WhiskeyNoteTimelineItem(
    note: WhiskeyNote,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // Natural history timeline line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp) // Reduced width for cleaner look
        ) {
            // Top connecting line (hidden for first item)
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Timeline marker - simple and clean
            Surface(
                shape = CircleShape,
                color = getRatingColor(note.finalRating.overall),
                modifier = Modifier.size(12.dp)
            ) {}
            
            // Bottom connecting line (hidden for last item)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp)) // Reduced spacing due to wider timeline
        
        // Twitter-style timeline card
        Box(modifier = Modifier.fillMaxWidth()) {
            // Twitter-style card with minimal border
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Clean header with date and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DateTimeFormatter.ofPattern("M월 d일").format(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(note.createdAt),
                                ZoneId.systemDefault()
                            )
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Rating badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = getRatingColor(note.finalRating.overall)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${note.finalRating.overall}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Twitter-style note content
                Text(
                    text = note.additionalNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Compact flavor tags (Twitter-style)
                if (note.flavors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        note.flavors.take(4).forEach { flavor -> // Limit to 4 flavors for clean look
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = flavor.flavor.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun ScoreChip(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun FlavorChip(
    flavor: String,
    intensity: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = getFlavorIntensityColor(intensity)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = flavor,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            repeat(intensity) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = Color.White
                )
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

@Composable
private fun EmptyNotesMessage(
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(26.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "첫 번째 테이스팅 노트를 작성해보세요",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "이 위스키에 대한 당신의 경험과 감상을\n기록해서 테이스팅 여정을 시작해보세요!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
            
            Button(
                onClick = onAddNote,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "테이스팅 노트 작성하기",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helper functions
@Composable
private fun getWhiskeyTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "bourbon" -> Color(0xFFD2691E)
        "scotch", "singlemalt" -> Color(0xFF8B4513)
        "japanese" -> Color(0xFF228B22)
        "irish", "irishpotstill" -> Color(0xFF32CD32)
        "rye" -> Color(0xFF8B0000)
        "blended", "blendedmalt" -> Color(0xFF4169E1)
        "taiwanese" -> Color(0xFF9932CC)
        "singlegrain" -> Color(0xFFDAA520)
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

@Composable
private fun getFlavorIntensityColor(intensity: Int): Color {
    return when (intensity) {
        1 -> Color(0xFF81C784) // Light Green
        2 -> Color(0xFF4CAF50) // Green
        3 -> Color(0xFF2196F3) // Blue
        4 -> Color(0xFF9C27B0) // Purple
        5 -> Color(0xFFE91E63) // Pink
        else -> MaterialTheme.colorScheme.secondary
    }
}

// Preview Mock Data
private val mockWhiskey = Whiskey(
    id = "preview-whiskey-1",
    name = "Macallan 18 년산",
    distillery = "The Macallan Distillery",
    type = WhiskeyType.SINGLEMALT,
    age = 18,
    year = 2005,
    abv = 43.0,
    price = 650000.0,
    region = "Speyside, Scotland",
    description = "풍부한 셰리 캐스크의 영향으로 달콤한 건포도, 계피, 생강의 복합적인 향이 특징인 프리미엄 싱글몰트 위스키입니다.",
    rating = 92,
    imageUris = listOf("https://example.com/macallan18.jpg"),
    createdAt = LocalDateTime.now().minusDays(30),
    updatedAt = LocalDateTime.now().minusDays(5)
)

private val mockWhiskeyNotes = listOf(
    WhiskeyNote(
        id = "note-1",
        name = "Macallan 18 년산",
        distillery = "The Macallan Distillery",
        origin = "Scotland (Speyside)",
        type = "SingleMalt",
        age = 18,
        year = 2005,
        abv = 43.0,
        price = 650000.0,
        sampled = true,
        color = ColorMeter("Deep Amber", 4),
        additionalNotes = "셰리 캐스크의 영향이 강하게 느껴지는 복합적인 맛. 첫 모금에서는 달콤한 건포도와 무화과의 풍미가 입안 가득 퍼지며, 이어서 계피와 넛메그 같은 따뜻한 스파이스가 조화롭게 어우러집니다.",
        finalRating = FinalRating(
            appearance = 92,
            nose = 88,
            taste = 90,
            finish = 85,
            overall = 89
        ),
        flavors = listOf(
            FlavorIntensity(flavor = Flavor.DRIED, intensity = 4),
            FlavorIntensity(flavor = Flavor.TOFFEE, intensity = 5),
            FlavorIntensity(flavor = Flavor.SPICE, intensity = 3),
            FlavorIntensity(flavor = Flavor.VANILLA, intensity = 2)
        ),
        createdAt = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000), // 1 week ago
        updatedAt = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
    ),
    WhiskeyNote(
        id = "note-2",
        name = "Macallan 18 년산",
        distillery = "The Macallan Distillery",
        origin = "Scotland (Speyside)",
        type = "SingleMalt",
        age = 18,
        year = 2005,
        abv = 43.0,
        price = 650000.0,
        sampled = true,
        color = ColorMeter("Mahogany", 5),
        additionalNotes = "두 번째 테이스팅에서는 첫 번째보다 더 깊은 맛의 층위를 발견할 수 있었습니다. 오크의 탄닌이 더 두드러지게 느껴지며, 긴 여운에서 다크 초콜릿과 커피의 쌉쌀한 맛이 인상적이었습니다.",
        finalRating = FinalRating(
            appearance = 90,
            nose = 91,
            taste = 92,
            finish = 88,
            overall = 91
        ),
        flavors = listOf(
            FlavorIntensity(flavor = Flavor.CHAR, intensity = 3),
            FlavorIntensity(flavor = Flavor.TOFFEE, intensity = 2),
            FlavorIntensity(flavor = Flavor.WOOD, intensity = 4),
            FlavorIntensity(flavor = Flavor.NUTS, intensity = 2)
        ),
        createdAt = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000), // 3 days ago
        updatedAt = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000)
    )
)

@Preview(
    name = "WhiskeyDetailRoute with Notes",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun WhiskeyDetailRoutePreview() {
    SpiritScribeTheme {
        WhiskeyDetailRouteContent(
            whiskey = mockWhiskey,
            notes = mockWhiskeyNotes,
            onAddNote = { },
            onNavigateBack = { }
        )
    }
}

@Preview(
    name = "WhiskeyDetailRoute Empty",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5
)
@Composable
private fun WhiskeyDetailRouteEmptyPreview() {
    SpiritScribeTheme {
        WhiskeyDetailRouteContent(
            whiskey = mockWhiskey,
            notes = emptyList(),
            onAddNote = { },
            onNavigateBack = { }
        )
    }
}

@Preview(
    name = "Hero Section",
    showBackground = true
)
@Composable
private fun WhiskeyHeroSectionPreview() {
    SpiritScribeTheme {
        WhiskeyHeroSection(
            whiskey = mockWhiskey
        )
    }
}

@Preview(
    name = "Stats Section",
    showBackground = true
)
@Composable
private fun WhiskeyStatsSectionPreview() {
    SpiritScribeTheme {
        WhiskeyStatsSection(
            whiskey = mockWhiskey,
            noteCount = 2,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Timeline Item",
    showBackground = true
)
@Composable
private fun WhiskeyNoteTimelineItemPreview() {
    SpiritScribeTheme {
        WhiskeyNoteTimelineItem(
            note = mockWhiskeyNotes.first(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Empty Notes Message",
    showBackground = true
)
@Composable
private fun EmptyNotesMessagePreview() {
    SpiritScribeTheme {
        EmptyNotesMessage(
            onAddNote = { },
            modifier = Modifier.padding(32.dp)
        )
    }
}

// Content composable for preview (without ViewModel dependency)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun WhiskeyDetailRouteContent(
    whiskey: Whiskey,
    notes: List<WhiskeyNote>,
    onAddNote: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    // Empty title for clean look
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNote,
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                text = { Text("테이스팅 노트 추가") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 80.dp
            )
        ) {
            // Hero Section with Whiskey Details
            item {
                WhiskeyHeroSection(
                    whiskey = whiskey,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Stats Section
            item {
                WhiskeyStatsSection(
                    whiskey = whiskey,
                    noteCount = notes.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Timeline Header
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "테이스팅 여정",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${notes.size}개 노트",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            // Tasting Notes Timeline or Empty State
            if (notes.isEmpty()) {
                item {
                    EmptyNotesMessage(
                        onAddNote = onAddNote,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            } else {
                itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
                    WhiskeyNoteTimelineItem(
                        note = note,
                        isFirst = index == 0,
                        isLast = index == notes.lastIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .animateItem(
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                    )
                }
            }
        }
    }
}
package com.august.spiritscribe.ui.note

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.august.spiritscribe.R
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.FlavorProfile
import com.august.spiritscribe.ui.theme.*
import com.august.spiritscribe.utils.collectAsMutableState
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import android.Manifest
import com.august.spiritscribe.utils.PermissionHandler
import android.widget.Toast
import java.io.File

@Composable
fun NewThreadScreen(viewModel: NewThreadViewModel = viewModel()) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val playbackProgress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.startRecording()
        } else {
            showPermissionDialog = true
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.initializeAudioServices(context)
    }

    val pickMedia = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(10)
    ) { uriList: List<Uri> ->
        if (uriList.isNotEmpty()) {
            viewModel.onSelectImagesFromGallery(uriList.map { it.toString() })
        }
    }

    val pickGif = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            viewModel.onSelectGif(it.toString())
        }
    }

    val openCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.onTakePicturePreview(it.toImageUri(context).toString()) }
    }

    val uim by viewModel.uim.collectAsStateWithLifecycle()
    val (input, setInput) = viewModel.inputStateFlow.collectAsMutableState()

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Audio recording permission is required to record voice notes. Please grant the permission in app settings.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NewThreadItem(
            uim = uim,
            input = input,
            isRecording = isRecording,
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            onClickGallery = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
            onClickPhoto = { openCamera.launch() },
            onClickGIF = { pickGif.launch("image/gif") },
            onClickMic = { 
                if (!isRecording) {
                    if (PermissionHandler.hasRecordAudioPermission(context)) {
                        viewModel.startRecording()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    viewModel.stopRecording()
                }
            },
            onInputChanged = remember { setInput },
            onPlayPause = { viewModel.togglePlayback() },
            onSeek = { viewModel.seekTo(it) }
        )
    }
}

fun Bitmap.toImageUri(context: Context): Uri? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        this,
        "spirit_${LocalDateTime.now()}",
        null
    )
    return Uri.parse(path)
}

@Composable
fun NewThreadItem(
    uim: NewThreadUIM,
    input: String,
    isRecording: Boolean,
    isPlaying: Boolean,
    currentPosition: Int,
    duration: Int,
    onClickGallery: () -> Unit,
    onClickPhoto: () -> Unit,
    onClickGIF: () -> Unit,
    onClickMic: () -> Unit,
    onInputChanged: (String) -> Unit,
    onClickHashTag: () -> Unit = {},
    onClickLocation: () -> Unit = {},
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var flavorProfiles by remember { 
        mutableStateOf(Flavor.entries.map { FlavorProfile(it, 0) })
    }
    var isFlavorProfileExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uim.profileImageUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .build(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = uim.nickName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = {
                        Text(
                            text = "Share your whiskey tasting experience...",
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface
                    )
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Flavor Profile",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(
                                onClick = { isFlavorProfileExpanded = !isFlavorProfileExpanded }
                            ) {
                                Icon(
                                    imageVector = if (isFlavorProfileExpanded) 
                                        Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = if (isFlavorProfileExpanded) 
                                        "Show less" else "Show more",
                                    tint = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = isFlavorProfileExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    FlavorProfileGraph(
                                        modifier = Modifier.fillMaxSize(),
                                        profiles = flavorProfiles,
                                        onProfileChange = { newProfiles ->
                                            flavorProfiles = newProfiles
                                        }
                                    )
                                }
                            }
                        }

                        if (!isFlavorProfileExpanded) {
                            FlavorProfilePreview(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                profiles = flavorProfiles
                            )
                        }
                    }
                }

                if (uim.threadContent is ThreadContent.Image) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ImagePreviewRow(uim.threadContent)
                }

                if (uim.threadContent is ThreadContent.Mic) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AudioPreviewCard(
                        isRecording = isRecording,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        filePath = uim.threadContent.uri,
                        onPlayPause = onPlayPause,
                        onSeek = onSeek
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                ActionButtonRow(
                    onClickGallery = onClickGallery,
                    onClickPhoto = onClickPhoto,
                    onClickGIF = onClickGIF,
                    onClickMic = onClickMic,
                    onClickHashTag = onClickHashTag,
                    onClickLocation = onClickLocation
                )
            }
        }
    }
}

@Composable
private fun ImagePreviewRow(image: ThreadContent.Image) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(image.uris) { uri ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                if (uri.endsWith(".gif", ignoreCase = true)) {
                    // GIF preview
                    val context = LocalContext.current
                    val drawable = remember(uri) {
                        try {
                            val source = ImageDecoder.createSource(context.contentResolver, Uri.parse(uri))
                            val drawable = ImageDecoder.decodeDrawable(source)
                            if (drawable is AnimatedImageDrawable) {
                                drawable.start()
                            }
                            drawable
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (drawable != null) {
                        Image(
                            painter = rememberDrawablePainter(drawable = drawable),
                            contentDescription = "GIF Preview",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Regular image preview
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uri)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .build(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonRow(
    onClickGallery: () -> Unit,
    onClickPhoto: () -> Unit,
    onClickGIF: () -> Unit,
    onClickMic: () -> Unit,
    onClickHashTag: () -> Unit,
    onClickLocation: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            icon = Icons.Outlined.Image,
            contentDescription = "Add Image",
            onClick = onClickGallery
        )
        ActionButton(
            icon = Icons.Outlined.PhotoCamera,
            contentDescription = "Take Photo",
            onClick = onClickPhoto
        )
        ActionButton(
            icon = Icons.Outlined.Gif,
            contentDescription = "Add GIF",
            onClick = onClickGIF
        )
        ActionButton(
            icon = Icons.Outlined.Mic,
            contentDescription = "Record Audio",
            onClick = onClickMic
        )
        ActionButton(
            icon = Icons.Outlined.Tag,
            contentDescription = "Add Tag",
            onClick = onClickHashTag
        )
        ActionButton(
            icon = Icons.Outlined.LocationOn,
            contentDescription = "Add Location",
            onClick = onClickLocation
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FlavorProfilePreview(
    modifier: Modifier = Modifier,
    profiles: List<FlavorProfile>
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
                        text = "${profile.flavor.name}: ${profile.intensity}",
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

@Composable
private fun AudioPreviewCard(
    isRecording: Boolean,
    isPlaying: Boolean,
    currentPosition: Int,
    duration: Int,
    filePath: String,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit
) {
    val file = remember(filePath) { File(filePath) }
    val fileExists = remember(file) { file.exists() }
    val progress = remember(currentPosition, duration) {
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isRecording) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Recording",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Recording...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = CircleShape
                                )
                        )
                    } else if (!fileExists) {
                        Text(
                            text = "Audio file not found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        IconButton(
                            onClick = onPlayPause,
                            enabled = fileExists
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = if (fileExists) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                        if (duration > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = formatDuration(currentPosition),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "/",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = formatDuration(duration),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            if (!isRecording && duration > 0 && fileExists) {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { onSeek(it.toInt()) },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

private fun formatDuration(durationMs: Int): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs.toLong())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs.toLong()) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun NewThreadScreenPreview() {
    SpiritScribeTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            NewThreadItem(
                uim = NewThreadUIM(
                    profileImageUrl = "",
                    nickName = "Whiskey Enthusiast",
                    threadContent = ThreadContent.Image(listOf())
                ),
                input = "",
                isRecording = false,
                isPlaying = false,
                currentPosition = 0,
                duration = 0,
                onClickGallery = {},
                onClickPhoto = {},
                onClickGIF = {},
                onClickMic = { },
                onInputChanged = {},
                onPlayPause = { },
                onSeek = { _ -> }
            )
        }
    }
}

data class NewThreadUIM(
    val nickName: String,
    val profileImageUrl: String,
    val threadContent: ThreadContent,
)

@Stable
sealed interface ThreadContent {
    class None : ThreadContent
    data class Image(val uris: List<String>) : ThreadContent
    data class GIF(val uris: List<String>) : ThreadContent
    data class Mic(val uri: String) : ThreadContent
    data class HashTag(val tag: String) : ThreadContent
    data class Location(val location: String) : ThreadContent
}
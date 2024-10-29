package com.august.spiritscribe.ui.poc

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.august.spiritscribe.R
import com.august.spiritscribe.ui.theme.AppTheme
import com.august.spiritscribe.ui.theme.primaryDark
import com.august.spiritscribe.utils.collectAsMutableState
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime


@Composable
fun NewThreadScreen(viewModel: NewThreadViewModel = viewModel()) {
    // new thread screen view model

    val context = LocalContext.current
    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(10)
    ) { uriList: List<Uri> ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uriList.isNotEmpty()) {
            viewModel.onSelectImagesFromGallery(uriList.map { it.toString() })
        }
    }

    val openCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.onTakePicturePreview(it.toImageUri(context).toString()) }
    }

    val uim by viewModel.uim.collectAsStateWithLifecycle()
    val (input, setInput) = viewModel.input.collectAsMutableState()

    NewThreadItem(
        uim = uim,
        input = input,
        onClickGallery = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
        onClickPhoto = { openCamera.launch() },
        onInputChanged = remember { setInput }
    )
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
    onClickGallery: () -> Unit,
    onClickPhoto: () -> Unit,
    onInputChanged: (String) -> Unit,
    onClickGIF: () -> Unit = {},
    onClickMic: () -> Unit = {},
    onClickHashTag: () -> Unit = {},
    onClickLocation: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row {
            Column(Modifier.height(IntrinsicSize.Max), // FIXME - vertical divider 가 보여야 한다.
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uim.profileImageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .background(shape = CircleShape, color = Color.Unspecified)
                        .size(40.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp),
                    color = Color.LightGray
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = uim.nickName)
                ThreadTextField(input, onInputChanged)
                // image loading
                // coil : https://github.com/coil-kt/coil#jetpack-compose
                if (uim.threadContent is ThreadContent.Image) {
                    ImageRow(uim.threadContent)
                }
                IconRow(
                    onClickGallery,
                    onClickPhoto,
                    onClickGIF,
                    onClickMic,
                    onClickHashTag,
                    onClickLocation,
                )
            }
        }
    }
}

@Composable
private fun ThreadTextField(input: String, onInputChanged: (String) -> Unit) {
    TextField(
        value = input,
        onValueChange = onInputChanged,
        label = { Text(text = "Start a thread ...", color = Color.LightGray) },
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = primaryDark,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun ImageRow(image: ThreadContent.Image) {
    LazyRow {
        if (image.uris.isNotEmpty()) {
            items(image.uris) { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun IconRow(
    onClickGallery: () -> Unit,
    onClickPhoto: () -> Unit,
    onClickGIF: () -> Unit,
    onClickMic: () -> Unit,
    onClickHashTag: () -> Unit,
    onClickLocation: () -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(36.dp)
                .clickable(onClick = onClickGallery),
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(36.dp)
                .clickable(onClick = onClickPhoto),
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Default.Gif,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(36.dp)
                .clickable(onClick = onClickGIF),
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(36.dp)
                .clickable(onClick = onClickMic),
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Default.AlternateEmail,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(36.dp)
                .clickable(onClick = onClickHashTag),
            tint = Color.LightGray
        )
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(36.dp)
                .clickable(onClick = onClickLocation),
            tint = Color.LightGray
        )
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

@Preview(heightDp = 300)
@Composable
fun NewThreadItemPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {

        }
    }
}
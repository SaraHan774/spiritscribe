package com.august.spiritscribe.ui.poc

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.august.spiritscribe.R
import com.august.spiritscribe.ui.theme.AppTheme
import com.august.spiritscribe.ui.theme.primaryDark


@Composable
fun NewThreadScreen(viewModel: NewThreadViewModel = viewModel()) {
    // new thread screen view model

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(10)
    ) { uriList: List<Uri> ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uriList.isNotEmpty()) {
            viewModel.onSelectImagesFromGallery(uriList)
        }
    }

    val openCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            Log.d("===", "bitmap $bitmap")
            viewModel.onTakePicturePreview(bitmap)
        }
    }

    val images by viewModel.imageUriList.collectAsState()
    val bitmap by viewModel.bitmap.collectAsState()

    Column {
        Text("Nickname")
        TextField(value = "Start a Thread", onValueChange = { })
        // image loading
        // coil : https://github.com/coil-kt/coil#jetpack-compose
        LazyRow {
            if (images.isNotEmpty()) {
                items(images) { uri ->
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

            if (bitmap != null) {
                item {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(bitmap)
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

        Row {
            Button(onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }) {
                Text(text = "Gallery")
            }
            Button(onClick = { openCamera.launch() }) {
                Text(text = "Camera")
            }
        }
    }
}



// FIXME - delete later ...
@Composable
fun NewThreadItem() {
    var input: String by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        Row {
            Column {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .background(shape = CircleShape, color = Color.Unspecified)
                        .size(40.dp)
                )
                Spacer(Modifier.height(16.dp))
                VerticalDivider(Modifier.width(4.dp), color = Color.LightGray)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = "kmodi21")
                TextField(
                    value = input,
                    onValueChange = { input = it },
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
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp),
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}


@Preview(heightDp = 300)
@Composable
fun NewThreadItemPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NewThreadItem()
        }
    }
}
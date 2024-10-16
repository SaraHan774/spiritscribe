package com.august.spiritscribe.ui.poc

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewThreadViewModel : ViewModel() {
    // two - way binding
    // https://proandroiddev.com/two-way-data-binding-in-jetpack-compose-1be55c402ec6
    val input = MutableStateFlow("")

    // TODO - gallery, camera 이미지 하나의 리스트에 추가해서 뷰가 구독하도록 수정 필요
    private val _imageUriList = MutableStateFlow<List<Uri>>(emptyList())
    val imageUriList : StateFlow<List<Uri>> = _imageUriList.asStateFlow()

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap : StateFlow<Bitmap?> = _bitmap.asStateFlow()

    fun onSelectImagesFromGallery(uriList: List<Uri>) {
        // for each uri
        _imageUriList.update { uriList }
    }

    fun onTakePicturePreview(bitmap: Bitmap?) {
        _bitmap.update { bitmap }
    }
}

package com.august.spiritscribe.ui.poc

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewThreadViewModel : ViewModel() {
    // two - way binding
    // https://proandroiddev.com/two-way-data-binding-in-jetpack-compose-1be55c402ec6
    val input = MutableStateFlow("")

    private val _uim = MutableStateFlow<NewThreadUIM>(
        NewThreadUIM(
            nickName = "Sara",
            profileImageUrl = "https://fastly.picsum.photos/id/258/200/200.jpg?hmac=SRxBTuyYSeHtVooeEMwmQPB0yIF3fqnvrOBR7DJnOlM",
            threadContent = ThreadContent.None()
        )
    )
    val uim: StateFlow<NewThreadUIM> = _uim.asStateFlow()

    fun onSelectImagesFromGallery(uriList: List<String>) {
        _uim.update {
            it.copy(threadContent = ThreadContent.Image(uriList))
        }
    }

    fun onTakePicturePreview(uri: String) {
        _uim.update {
            it.copy(threadContent = ThreadContent.Image(uris = listOf(uri)))
        }
    }
}

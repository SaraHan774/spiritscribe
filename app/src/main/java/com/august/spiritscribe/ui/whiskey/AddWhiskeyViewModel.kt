package com.august.spiritscribe.ui.whiskey

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyEntity
import com.august.spiritscribe.domain.model.WhiskeyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@Parcelize
data class AddWhiskeyState(
    val name: String = "",
    val distillery: String = "",
    val type: WhiskeyType = WhiskeyType.BOURBON,
    val age: String = "",
    val year: String = "",
    val abv: String = "",
    val price: String = "",
    val region: String = "",
    val description: String = "",
    val rating: String = "",
    val imageUris: List<Uri> = emptyList(),
    val sweetness: Int = 1,
    val smokiness: Int = 1,
    val spiciness: Int = 1,
    val fruitiness: Int = 1,
    val woodiness: Int = 1,
    val flavorNotes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
) : Parcelable

sealed class AddWhiskeyEvent {
    data class UpdateName(val name: String) : AddWhiskeyEvent()
    data class UpdateDistillery(val distillery: String) : AddWhiskeyEvent()
    data class UpdateType(val type: WhiskeyType) : AddWhiskeyEvent()
    data class UpdateAge(val age: String) : AddWhiskeyEvent()
    data class UpdateYear(val year: String) : AddWhiskeyEvent()
    data class UpdateAbv(val abv: String) : AddWhiskeyEvent()
    data class UpdatePrice(val price: String) : AddWhiskeyEvent()
    data class UpdateRegion(val region: String) : AddWhiskeyEvent()
    data class UpdateDescription(val description: String) : AddWhiskeyEvent()
    data class UpdateRating(val rating: String) : AddWhiskeyEvent()
    data class AddImage(val uri: Uri) : AddWhiskeyEvent()
    data class RemoveImage(val uri: Uri) : AddWhiskeyEvent()
    data class UpdateSweetness(val value: Int) : AddWhiskeyEvent()
    data class UpdateSmokiness(val value: Int) : AddWhiskeyEvent()
    data class UpdateSpiciness(val value: Int) : AddWhiskeyEvent()
    data class UpdateFruitiness(val value: Int) : AddWhiskeyEvent()
    data class UpdateWoodiness(val value: Int) : AddWhiskeyEvent()
    data class AddFlavorNote(val note: String) : AddWhiskeyEvent()
    data class RemoveFlavorNote(val note: String) : AddWhiskeyEvent()
    object SaveWhiskey : AddWhiskeyEvent()
}

@HiltViewModel
class AddWhiskeyViewModel @Inject constructor(
    private val whiskeyDao: WhiskeyDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        savedStateHandle.get<AddWhiskeyState>(STATE_KEY) ?: AddWhiskeyState()
    )
    val state: StateFlow<AddWhiskeyState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.collect { state ->
                savedStateHandle[STATE_KEY] = state
            }
        }
    }

    fun onEvent(event: AddWhiskeyEvent) {
        when (event) {
            is AddWhiskeyEvent.UpdateName -> updateState { it.copy(name = event.name) }
            is AddWhiskeyEvent.UpdateDistillery -> updateState { it.copy(distillery = event.distillery) }
            is AddWhiskeyEvent.UpdateType -> updateState { it.copy(type = event.type) }
            is AddWhiskeyEvent.UpdateAge -> updateState { it.copy(age = event.age) }
            is AddWhiskeyEvent.UpdateYear -> updateState { it.copy(year = event.year) }
            is AddWhiskeyEvent.UpdateAbv -> updateState { it.copy(abv = event.abv) }
            is AddWhiskeyEvent.UpdatePrice -> updateState { it.copy(price = event.price) }
            is AddWhiskeyEvent.UpdateRegion -> updateState { it.copy(region = event.region) }
            is AddWhiskeyEvent.UpdateDescription -> updateState { it.copy(description = event.description) }
            is AddWhiskeyEvent.UpdateRating -> updateState { it.copy(rating = event.rating) }
            is AddWhiskeyEvent.AddImage -> {
                if (_state.value.imageUris.size < 10) {
                    updateState { it.copy(imageUris = it.imageUris + event.uri) }
                }
            }
            is AddWhiskeyEvent.RemoveImage -> {
                updateState { it.copy(imageUris = it.imageUris - event.uri) }
            }
            is AddWhiskeyEvent.UpdateSweetness -> updateState { it.copy(sweetness = event.value) }
            is AddWhiskeyEvent.UpdateSmokiness -> updateState { it.copy(smokiness = event.value) }
            is AddWhiskeyEvent.UpdateSpiciness -> updateState { it.copy(spiciness = event.value) }
            is AddWhiskeyEvent.UpdateFruitiness -> updateState { it.copy(fruitiness = event.value) }
            is AddWhiskeyEvent.UpdateWoodiness -> updateState { it.copy(woodiness = event.value) }
            is AddWhiskeyEvent.AddFlavorNote -> {
                updateState { it.copy(flavorNotes = it.flavorNotes + event.note) }
            }
            is AddWhiskeyEvent.RemoveFlavorNote -> {
                updateState { it.copy(flavorNotes = it.flavorNotes - event.note) }
            }
            AddWhiskeyEvent.SaveWhiskey -> saveWhiskey()
        }
    }

    private fun saveWhiskey() {
        val state = _state.value
        
        // Validate required fields
        if (state.name.isBlank() || state.distillery.isBlank() || state.abv.isBlank()) {
            updateState { it.copy(error = "Please fill in all required fields") }
            return
        }

        viewModelScope.launch {
            try {
                updateState { it.copy(isLoading = true, error = null) }
                
                val whiskeyId = UUID.randomUUID().toString()
                val now = LocalDateTime.now()
                
                val whiskey = WhiskeyEntity(
                    id = whiskeyId,
                    name = state.name,
                    distillery = state.distillery,
                    type = state.type,
                    age = state.age.toIntOrNull(),
                    year = state.year.toIntOrNull(),
                    abv = state.abv.toDoubleOrNull() ?: 0.0,
                    price = state.price.toDoubleOrNull(),
                    region = state.region.takeIf { it.isNotBlank() },
                    description = state.description,
                    rating = state.rating.toIntOrNull(),
                    imageUris = state.imageUris.map { it.toString() },
                    createdAt = now,
                    updatedAt = now
                )

                val flavorProfile = FlavorProfileEntity(
                    id = UUID.randomUUID().toString(),
                    whiskeyId = whiskeyId,
                    sweetness = state.sweetness,
                    smokiness = state.smokiness,
                    spiciness = state.spiciness,
                    fruitiness = state.fruitiness,
                    woodiness = state.woodiness,
                    notes = state.flavorNotes.joinToString(" / ")
                )

                whiskeyDao.insertWhiskeyWithProfile(whiskey, flavorProfile)
                updateState { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateState(update: (AddWhiskeyState) -> AddWhiskeyState) {
        _state.update(update)
    }

    companion object {
        private const val STATE_KEY = "add_whiskey_state"
    }
} 
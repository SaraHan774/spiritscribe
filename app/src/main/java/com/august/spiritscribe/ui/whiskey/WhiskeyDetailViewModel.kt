package com.august.spiritscribe.ui.whiskey

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WhiskeyDetailViewModel @Inject constructor(
    private val whiskeyDao: WhiskeyDao,
    private val whiskeyNoteDao: WhiskeyNoteDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val whiskeyId: String = requireNotNull(savedStateHandle.get<String>("id")) {
        "whiskeyId parameter is missing"
    }

    init {
        Log.d("WhiskeyDetailViewModel", "whiskeyId = $whiskeyId")
    }

    val whiskey: StateFlow<Whiskey?> = whiskeyDao.getWhiskeyById(whiskeyId)
        .onEach { entity -> 
            Log.d("WhiskeyDetailViewModel", "Received whiskey entity: $entity")
        }
        .map { entity -> 
            entity?.let { 
                Whiskey(
                    id = it.id,
                    name = it.name,
                    distillery = it.distillery,
                    type = it.type,
                    age = it.age,
                    year = it.year,
                    abv = it.abv,
                    price = it.price,
                    region = it.region,
                    description = it.description,
                    rating = it.rating,
                    imageUris = it.imageUris,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val notes: StateFlow<List<WhiskeyNote>> = whiskeyNoteDao.getNotesByCreatedDesc()
        .onEach { entities ->
            Log.d("WhiskeyDetailViewModel", "Received ${entities.size} notes")
        }
        .map { entities -> 
            entities
                .filter { it.name == whiskey.value?.name }
                .map { it.toDomain() }
                .sortedByDescending { it.createdAt }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
} 
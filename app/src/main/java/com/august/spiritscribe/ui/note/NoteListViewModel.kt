package com.august.spiritscribe.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.domain.model.Whiskey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val whiskeyDao: WhiskeyDao,
    private val whiskeyNoteDao: WhiskeyNoteDao
) : ViewModel() {

    init {
        android.util.Log.d("NoteListViewModel", "Initializing ViewModel")
    }

    val whiskeys: StateFlow<List<Whiskey>> = whiskeyDao.getAllWhiskeys()
        .onEach { entities -> 
            android.util.Log.d("NoteListViewModel", "Received ${entities.size} whiskeys from database")
            entities.forEach { entity ->
                android.util.Log.d("NoteListViewModel", "Whiskey: ${entity.name}")
            }
        }
        .map { entities ->
            android.util.Log.d("NoteListViewModel", "Mapping ${entities.size} entities to domain models")
            entities.map { entity ->
                Whiskey(
                    id = entity.id,
                    name = entity.name,
                    distillery = entity.distillery,
                    type = entity.type,
                    age = entity.age,
                    year = entity.year,
                    abv = entity.abv,
                    price = entity.price,
                    region = entity.region,
                    description = entity.description,
                    rating = entity.rating,
                    imageUris = entity.imageUris,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
} 
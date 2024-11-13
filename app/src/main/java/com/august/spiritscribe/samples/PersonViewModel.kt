package com.august.spiritscribe.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

// https://www.youtube.com/watch?app=desktop&v=-0PFpQIRyH4&ab_channel=CharfaouiYounes
class PersonViewModel(
    private val personsAPI: PersonAPI,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher,
)  : ViewModel() {
    private val _uiState = MutableStateFlow<PersonScreenState>(PersonScreenState.Loading())
    val uiState : StateFlow<PersonScreenState> = _uiState.asStateFlow()

    init {
        fetchPersons()
    }

    private fun fetchPersons() {
        viewModelScope.launch(mainDispatcher) {
            _uiState.value = PersonScreenState.Loading()

            personsAPI.getAllPersons()
                .flowOn(ioDispatcher)
                .catch { throwable ->
                    _uiState.value = PersonScreenState.Failure(throwable)
                }.collect { personList ->
                    _uiState.value = PersonScreenState.Success(personList)
                }
        }
    }
}

sealed class PersonScreenState {
    class Success(val persons: List<Person>) : PersonScreenState()
    class Loading: PersonScreenState()
    class Failure(val throwable: Throwable) : PersonScreenState()
}

interface PersonAPI {
    fun getAllPersons() : Flow<List<Person>>
}

data class Person(
    val name: String
)
package com.august.spiritscribe.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    init {
        val childScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + CoroutineExceptionHandler { coroutineContext, throwable ->
                Log.d("===",  "error $throwable")
            }
        ) {
            childScope.launch { getSomethingAgain() }
            childScope.launch(CoroutineExceptionHandler { context ,throwable ->
                Log.d("===", "error of child $throwable")
            }) { getSomething() }
        }
    }

    fun test() {}

    private suspend fun getSomething() {
        delay(1000)
        throw IllegalStateException("some error")
    }

    private suspend fun getSomethingAgain() {
        delay(2000)
        println("=== I GOT THIS ===")
    }
}
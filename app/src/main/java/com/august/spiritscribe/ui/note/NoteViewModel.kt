package com.august.spiritscribe.ui.note

import androidx.lifecycle.ViewModel
import com.august.spiritscribe.R
import com.august.spiritscribe.utils.ResourceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor() : ViewModel() {
    // TODO hilt viewmodel 사용하기
    // https://developer.android.com/training/dependency-injection/hilt-android#kts
    fun getSomeString(): String {
        return ResourceUtils.getString(R.array.whiskey_origin_list)
    }
}
package com.tuk.shdelivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tuk.shdelivery.Data.MatchDao

class MyPageViewModelFactory(private val matchDao: MatchDao, private val userId: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(matchDao, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
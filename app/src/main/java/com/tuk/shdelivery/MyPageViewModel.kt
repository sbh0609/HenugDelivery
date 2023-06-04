package com.tuk.shdelivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData

class MyPageViewModel(private val matchDao: MatchDao, private val userId: String): ViewModel() {
//    val participatingMatch: LiveData<MatchRoomData> = matchDao.getParticipatingMatch(userId)
}

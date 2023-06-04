package com.tuk.shdelivery

import android.provider.ContactsContract.CommonDataKinds.Phone
import java.io.Serializable
import java.sql.Time
import java.util.Calendar
import java.util.Timer
data class MatchRoomData(
    var id: String,
    var menu: String,
    var deliveryTime: Calendar = Calendar.getInstance(),
    var description: String = "",
    var count: Int,
    var createTime: Calendar = Calendar.getInstance(),
    var storeName: String,
) : Serializable

data class User(
    var userId: String = "",
    var userName: String = "",
    var participateMatchId : String = "",
    var userPoint: Long = 0L,
    var matchPoint : Long = 0L
) : Serializable

data class ChatRoom(
    var participatePeopleId: List<String> = ArrayList<String>(),
    var orderAcceptNum: Int = 0,
    var orderPoint : Int = 0,
) : Serializable

data class Chat(
    var userId: String = "",
    var userName: String = "",
    var chat: String = "",
    var chatTime: Calendar = Calendar.getInstance(),
) : Serializable

data class Fdatabase(
    var id: String,
    var menu: String,
    var deliveryTime: Calendar = Calendar.getInstance(),
    var description: String = "",
    var count: Int,
    var createTime: Calendar = Calendar.getInstance(),
    var storeName: String,
    var participatePeopleId: List<String> = ArrayList<String>(),
    var orderAcceptNum: Int = 0,
    var orderPoint : Int = 0,
) : Serializable
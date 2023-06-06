package com.tuk.shdelivery.Data

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class MatchRoomData(
    var id: String= "",
    var menu: String = "",
    var deliveryTime: Long = Calendar.getInstance().timeInMillis,
    var description: String = "",
    var count: Int = 0,
    var createTime: Long = Calendar.getInstance().timeInMillis,
    var storeName: String = "",
) : Serializable

data class User(
    var userId: String = "",
    var userName: String = "",
    var participateMatchId : String = "",
    var userPoint: Long = 0L,
    var matchPoint : Long = 0L
) : Serializable

data class ChatRoom(
    var participatePeopleId: ArrayList<String> = ArrayList<String>(),
    var orderAcceptPeopleId : ArrayList<String> = ArrayList<String>(),
    var orderAcceptNum: Int = 0,
    var orderPoint : Int = 0,
) : Serializable

data class Chat(
    var userId: String = "",
    var userName: String = "",
    var chat: String = "",
    var chatTime: Long = Calendar.getInstance().timeInMillis,
) : Serializable


package com.tuk.shdelivery.Data

import java.io.Serializable
import java.util.*

data class MatchRoomData(
    var id: Long,
    var menu: String,
    var deliveryTime: Calendar,
    var description: String,
    var count: Int,
    var createTime: Calendar,
    var storeName: String,
) : Serializable

data class User(
    var userId: String,
    var userName: String,
    var participateMatchId : String,
    var userPoint: Long,
) : Serializable

data class ChatRoom(
    var chatId: String,
    var participatePeopleId: List<Int>,
    var participatePeopleNum: Int,
    var orderAcceptNum: Int,
) : Serializable

data class Chat(
    var userId: String,
    var userName: String,
    var chatId: Long,
    var chat: String,
    var chatTime: Calendar,
) : Serializable



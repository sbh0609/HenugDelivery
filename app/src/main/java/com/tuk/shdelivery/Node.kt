package com.tuk.shdelivery

import android.provider.ContactsContract.CommonDataKinds.Phone
import java.sql.Time
import java.util.Calendar
import java.util.Timer

data class User(
    var userId: String,
    var userName: String,
    var participateMatchId: Long,
    var userPoint: Long,
){
    constructor(): this("","",0,0)
}
data class MatchRoom(
    var matchRoomId: Long,
    var participatePeopleNum: Int,
    var menu: String,
    var orderTime: java.util.Calendar,
    var matchCreateTime: java.util.Calendar,
    var describe: String,
    var storeName: String
)
data class ChatRoom(
    var chatId: Long,
    var participatePeopleId: List<Int>,
    var participatePeopleNum: Int,
    var orderAcceptNum: Int
)
data class Chat( //실시간으로 add 기능
    var userId: Long,
    var userName: String,
    var chatId: Long,
    var chat: String,
    var chatTime: java.util.Calendar
)
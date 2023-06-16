package com.tuk.shdelivery.Data

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
//data class ChargeRequest(
//    var userID: String = "",
//    var chargeAmount: Long = 0
//)
data class ChargePoint(
    var userId: String = "",
    var chargeRequest: Long = 0,
    var chargeAllow: Int = 0
)

//매치룸에 대한 정보를 포함한다.
//id, 메뉴 이름, 배달 시간, description, 참여자 수, 생성 시간, 가게 이름을 포함
data class MatchRoomData(
    var id: String= "",
    var menu: String = "",
    var deliveryTime: Long = Calendar.getInstance().timeInMillis,
    var description: String = "",
    var count: Int = 0,
    var createTime: Long = Calendar.getInstance().timeInMillis,
    var storeName: String = "",
) : Serializable
//사용자에 대한 정보를 나타낸다
// 사용자의 아이디, 이름, 참여 중인 매치룸의 아이디, 사용자의 점수, 매치 점수를 포함
data class User(
    var userId: String = "",
    var userName: String = "",
    var participateMatchId : String = "",
    var userPoint: Long = 0L,
    var matchPoint : Long = 0L
) : Serializable

//채팅방에 대한 정보를 나타냅니다.
//참여하는 사람들의 아이디 목록, 주문을 수락한 사람들의 아이디 목록, 주문을 수락한 사람의 수, 주문 점수를 포함한다
data class ChatRoom(
    var participatePeopleId: ArrayList<String> = ArrayList<String>(),
    var orderAcceptPeopleId : ArrayList<String> = ArrayList<String>(),
    var orderAcceptNum: Int = 0,
    var orderPoint : Int = 0,
) : Serializable

//채팅 메시지에 대한 정보를 나타낸다
//메시지를 보낸 사용자의 아이디와 이름, 메시지 내용, 메시지를 보낸 시간을 포함한다.
data class Chat(
    var userId: String = "",
    var userName: String = "",
    var chat: String = "",
    var chatTime: Long = Calendar.getInstance().timeInMillis,
) : Serializable


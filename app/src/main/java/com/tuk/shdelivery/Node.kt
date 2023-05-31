package com.tuk.shdelivery

data class Room(
    var id: String? = null,
    var name: String? = null,
    var foodType: String? = null,
    var storeName: String? = null,
    var time: String? = null
)

data class Message(
    var content: String="",
    var userId: String="",
    var timestamp: Long=0
)
//data class Message(
//    var userId: String? = null,
//    var content: String? = null,
//    var timestamp: Long = System.currentTimeMillis()
//) 질문
package com.tuk.shdelivery


data class User(
    var userKey: String,
    var userName: String,
    var userAge: String
){
    constructor(): this("","","")
}


data class Store(
    var storeKey: String,
    var storeName: String,
    var storeNum: String,
    var storeLocRoad: String,
){
    constructor(): this("","","", "")
}

data class ChatRoom(
    var chatKey: String,
){
    constructor(): this("")
}

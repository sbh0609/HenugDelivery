package com.tuk.shdelivery.Data

import com.tuk.shdelivery.custom.DeliverTime
import java.io.Serializable
import java.util.*

data class MatchRoomData(var category : String, var deliveryTime : Calendar, var description : String,
                    var count : Int, var createTime : Calendar, var storeName : String) :
    Serializable {

}
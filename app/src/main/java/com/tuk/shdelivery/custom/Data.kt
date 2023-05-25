package com.tuk.shdelivery.custom

import android.graphics.drawable.Icon
import com.tuk.shdelivery.Data.IconData
import com.tuk.shdelivery.R
import kotlin.collections.Map
import androidx.fragment.app.Fragment
import java.util.SortedMap

class Data {
   companion object{
      public fun category() : SortedMap<String, Int>{
         val map = sortedMapOf<String, Int>(
            "족발/보쌈" to R.drawable.zokval,
            "찜/탕/찌개" to R.drawable.icon_tang,
            "돈까스/일식" to R.drawable.icon_dongas1sick,
            "피자" to  R.drawable.icon_pizza,
            "고기/구기" to  R.drawable.icon_goki,
            "양식" to  R.drawable.icon_pasta,
            "치킨" to  R.drawable.icon_chicken,
            "중식" to  R.drawable.icon_joongsick,
            "백반/죽/국수" to R.drawable.icon_backbangooksoo,
            "도시락" to  R.drawable.icon_dosirock,
            "분식" to R.drawable.icon_boonsick,
            "카페/디저트" to R.drawable.icon_cafedisert,
            "패스트푸드" to  R.drawable.icon_fastfood,
         )
         return map
      }
   }
}
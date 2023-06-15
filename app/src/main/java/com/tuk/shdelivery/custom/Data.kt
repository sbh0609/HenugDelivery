package com.tuk.shdelivery.custom

import com.tuk.shdelivery.R
import java.util.SortedMap

// 이 클래스는 Singleton 패턴을 사용하여 카테고리와 관련된 데이터를 관리합니다.
class Data {
   companion object{
      // 카테고리 이름과 해당 카테고리의 이미지 리소스 ID를 매핑하는 함수입니다.
      public fun category() : SortedMap<String, Int>{
         // 카테고리 이름을 키로, 해당 카테고리의 이미지 리소스 ID를 값으로 하는 SortedMap을 생성합니다.
         val map = sortedMapOf<String, Int>(
            "족발/보쌈" to R.drawable.icon_zokval,   // "족발/보쌈" 카테고리의 이미지 리소스 ID입니다.
            "찜/탕/찌개" to R.drawable.icon_tang,
            "돈까스/일식" to R.drawable.icon_dongas1sick,
            "피자" to  R.drawable.icon_pizza,
            "고기/구이" to  R.drawable.icon_goki,
            "양식" to  R.drawable.icon_pasta,
            "치킨" to  R.drawable.icon_chicken,
            "중식" to  R.drawable.icon_joongsick,
            "백반/죽/국수" to R.drawable.icon_backbangooksoo,
            "도시락" to  R.drawable.icon_dosirock,
            "분식" to R.drawable.icon_boonsick,
            "카페/디저트" to R.drawable.icon_cafedisert,
            "패스트 푸드" to  R.drawable.icon_fastfood,
         )
         // 위에서 생성한 SortedMap을 반환합니다.
         return map
      }
   }
}

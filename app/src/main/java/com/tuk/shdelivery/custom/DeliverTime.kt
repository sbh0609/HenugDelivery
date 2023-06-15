package com.tuk.shdelivery.custom

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

// Calendar 객체를 받아서 일반적인 시간, 날짜 형식과 함께 특별한 형식으로 시간을 출력하는 기능을 가진 클래스입니다.
class DeliverTime(var data: Calendar) {

    // 현재 객체의 시간을 "오전 00:00" 형식으로 반환합니다.
    fun getTime(): String {
        val calendar = data
        return getTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    // 현재 객체의 날짜를 "오늘 오전 04:30" 또는 "MM/dd" 형식으로 반환합니다. 오늘 날짜일 경우 앞의 형식을, 아닐 경우 뒤의 형식을 사용합니다.
    fun getCreateTime(): String {
        val dateFormat =
            if (isToday(data)) {
                SimpleDateFormat("오늘 a hh:mm", Locale.KOREA)
            } else {
                SimpleDateFormat("MM/dd", Locale.KOREA)
            }
        dateFormat.calendar = data
        return dateFormat.format(data.time)
    }

    // 현재 객체의 날짜를 "YY/MM/dd (E)" 형식으로 반환합니다. E는 요일을 나타냅니다.
    fun getDay(): String {
        val dateFormat = SimpleDateFormat("YY/MM/dd (E)", Locale.KOREAN)
        return dateFormat.format(data.time)
    }

    // 현재 객체의 날짜가 오늘인지 확인하는 메소드입니다.
    fun isToday(calendar: Calendar): Boolean {
        val currentCalendar = Calendar.getInstance()

        return calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
    }

    // static 메소드들이 정의된 companion object 입니다.
    companion object {
        // 시간과 분을 받아 "오전 00:00" 형식으로 반환합니다.
        fun getTime(hour: Int, minute: Int): String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val dateFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)
            return dateFormat.format(calendar.time)
        }

        // 시간 차를 받아 "00시간 00분 후 주문" 형식으로 반환합니다.
        fun getHourMinute(diffMillis: Long): String {
            val diff = diffMillis / (60 * 1000)

            val hours = diff / 60
            val minutes = diff % 60

            var diffString =
                if (hours == 0L) {
                    minutes.toString() + "분"
                } else {
                    hours.toString() + "시간 " + minutes + "분"
                }

            return String.format("%s 후 주문", diffString)
        }

        // "yy/MM/dd/HH/mm" 형식의 문자열을 Calendar 객체로 반환합니다.
        fun getCalendar(input: String): Calendar {
            val format = SimpleDateFormat("yy/MM/dd/HH/mm", Locale.getDefault())
            var a = Calendar.getInstance().apply {
                time = format.parse(input)
            }
            return a
        }

        // Calendar 객체를 "yy/MM/dd/HH/mm" 형식의 문자열로 반환합니다.
        fun setCalendar(calendar: Calendar): String {
            val format = SimpleDateFormat("yy/MM/dd/HH/mm")
            return format.format(calendar.time)
        }
    }
}
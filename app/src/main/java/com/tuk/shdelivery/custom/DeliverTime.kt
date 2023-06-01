package com.tuk.shdelivery.custom

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DeliverTime(var data: Calendar) {
    fun getTime(): String {
        val calendar = data
        return getTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    fun getCreateTime(): String {
        // "MM/dd a h:mm" 패턴으로 날짜를 문자열로 변환
        //오늘인지 확인후 오늘이면 "오늘 오전 04:30"출력
        //아니면 MM/dd 만 출력
        val dateFormat =
            if (isToday(data)) {
                SimpleDateFormat("오늘 a hh:mm", Locale.KOREA)
            } else {
                SimpleDateFormat("MM/dd", Locale.KOREA)
            }
        dateFormat.calendar = data
        return dateFormat.format(data.time)
    }

    fun getDay(): String {
        val dateFormat = SimpleDateFormat("YY/MM/dd (E)", Locale.KOREAN)
        return dateFormat.format(data.time)
    }

    fun isToday(calendar: Calendar): Boolean {
        val currentCalendar = Calendar.getInstance()

        return calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
    }

    companion object {
        fun getTime(hour: Int, minute: Int): String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val dateFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)
            return dateFormat.format(calendar.time)
        }

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

        /**
         * "yy/MM/dd/HH/mm" => Calendar
         * */
        fun getCalendar(input: String): Calendar {
            val format = SimpleDateFormat("yy/MM/dd/HH/mm", Locale.getDefault())
            var a = Calendar.getInstance().apply {
                time = format.parse(input)
            }
            return a
        }

        fun setCalendar(calendar: Calendar): String {
            val format = SimpleDateFormat("yy/MM/dd/HH/mm")
            return format.format(calendar.time)
        }
    }


}
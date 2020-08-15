package com.acacia.simpletodo.utils

import java.text.SimpleDateFormat
import java.util.*

fun getDate(pos: Int): String {
    val today = Calendar.getInstance()
    today.add(Calendar.DATE, pos)
    val week = today.get(Calendar.DAY_OF_WEEK)
    val day = today.get(Calendar.DATE)
    val hour = today.get(Calendar.HOUR_OF_DAY) // 24
    val min = today.get(Calendar.MINUTE)

    return "${getWeek(week)}\n${day}"
}

fun getAddDate(pos: Int): Calendar {
    val today = Calendar.getInstance()
    today.add(Calendar.DATE, pos)
    val week = today.get(Calendar.DAY_OF_WEEK)
    val day = today.get(Calendar.DATE)
    val hour = today.get(Calendar.HOUR_OF_DAY) // 24
    val min = today.get(Calendar.MINUTE)

    return today
}

fun getDisplayDate(cal: Calendar): String {
    val week = cal.get(Calendar.DAY_OF_WEEK)
    val day = cal.get(Calendar.DATE)
    return "${getWeek(week)}\n${day}"
}

fun getWeek(week: Int): String {
    return when(week) {
        1 -> "일"
        2 -> "월"
        3 -> "화"
        4 -> "수"
        5 -> "목"
        6 -> "금"
        7 -> "토"
        else -> ""
    }
}

fun getCalendarList() = listOf<Calendar>(getAddDate(0),
                                         getAddDate(1),
                                         getAddDate(2),
                                         getAddDate(3),
                                         getAddDate(4),
                                         getAddDate(5),
                                         getAddDate(6))


fun getTimeList(size: Int): ArrayList<String> {
    val list = arrayListOf<String>()
    list.add("")
    for (i in 0 until size) {
        if (i < 10) {
            list.add("0$i")
        } else {
            list.add("$i")
        }
    }
    list.add("")
    return list
}

fun getStringDate(index: Int): String {
    val time = getCalendarList()[index].time
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
    return sdf.format(time)
}

fun getStringNotiTime(cal: Calendar): String {
    val time = cal.time
    val sdf = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)
    return sdf.format(time)
}

fun getDatePosition(savedDate: String): Int {
    for (i in 0 until 7) {
        if (savedDate == getStringDate(i)) {
            return i
        }
    }
    return 0
}
package com.acacia.seventodo.utils

import com.acacia.seventodo.todo.detail.TodoDay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getDisplayDayWeek(pos: Int): String {
    val today = Calendar.getInstance()
    today.add(Calendar.DATE, pos)
    val month = today.get(Calendar.MONTH)
    val week = today.get(Calendar.DAY_OF_WEEK)
    val day = today.get(Calendar.DATE)
    return "${addZero(month + 1)}.${addZero(day)} (${getWeek(week)})"
}

fun addZero(day: Int): String {
    if (day < 10) {
        return "0${day}"
    }
    return day.toString()
}

fun getAddDate(pos: Int): Calendar {
    val today = Calendar.getInstance()
    today.add(Calendar.DATE, pos)
    return today
}

fun getDisplayDate(cal: Calendar): String {
    val week = cal.get(Calendar.DAY_OF_WEEK)
    val day = cal.get(Calendar.DATE)
    return "${getWeek(week)}\n${day}"
}

fun getDisplayNotiDate(str: String): String {
    val cal = getStringToCalendar(str)
    cal?.let {
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val hour = cal.get(Calendar.HOUR_OF_DAY) // 24
        val min = cal.get(Calendar.MINUTE)
        var stmin = min.toString()
        if (min < 10) {
            stmin = "0$min"
        }
        return "${hour} : ${stmin}"
    }
    return ""
}


fun getTodoDay(): List<TodoDay> {
    val list = getCalendarList()
    val dayList = arrayListOf<TodoDay>()
    for (cal in list) {
        val week = cal.get(Calendar.DAY_OF_WEEK)
        val day = cal.get(Calendar.DATE)
        dayList.add(TodoDay(day.toString(), getWeek(week) + "요일"))
    }
    return dayList
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

fun getCalendarToString(cal: Calendar): String {
    val time = cal.time
    val sdf = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)
    return sdf.format(time)
}

fun getStringToCalendar(str: String): Calendar? {
    val sdf = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)
    val date = sdf.parse(str)
    date?.let {
        val cal = Calendar.getInstance()
        cal.time = it
        return cal
    } ?: run {
        return null
    }
}

fun getDatePosition(savedDate: String): Int {
    for (i in 0 until 7) {
        if (savedDate == getStringDate(i)) {
            return i
        }
    }
    return 0
}

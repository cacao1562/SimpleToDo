package com.acacia.simpletodo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acacia.simpletodo.utils.getCalendarList
import java.util.*

class TodoMainViewModel: ViewModel() {

    // Month bar view layout_weight
    val month01 = MutableLiveData<Float>(0f)
    val month02 = MutableLiveData<Float>(0f)

    // Month bar view textView text
    val monthTitle01 = MutableLiveData<String>()
    val monthTitle02 = MutableLiveData<String>()

    fun initMonthBar() {

        val months = getCalendarList().groupBy { it.get(Calendar.MONTH) }

        for ((index, value) in months.keys.withIndex()) {
            when (index) {
                0 -> {
                    val first = months[value]
                    month01.value = first?.size?.toFloat()
                    monthTitle01.value = "${value}월"
                }
                1 -> {
                    val second = months[value]
                    month02.value = second?.size?.toFloat()
                    monthTitle02.value = "${value}월"
                }
            }

        }
    }
}
package com.acacia.seventodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acacia.seventodo.utils.getCalendarList
import com.acacia.seventodo.utils.getTimeList
import com.acacia.seventodo.utils.getWeek
import java.util.*
import kotlin.collections.ArrayList

class TodoDateViewModel : ViewModel() {

    // Hour RecyclerView adapter items
    private val _hourList = MutableLiveData<ArrayList<String>>()
    val hourList: LiveData<ArrayList<String>> = _hourList

    // Minute RecyclerView adapter items
    private val _minList = MutableLiveData<ArrayList<String>>()
    val minList: LiveData<ArrayList<String>> = _minList

    private val _selectedHour = MutableLiveData<Int>(0)
    val selectedHour: LiveData<Int> = _selectedHour

    private val _selectedMin = MutableLiveData<Int>(0)
    val selectedMin: LiveData<Int> = _selectedMin

    // Date textView
    val yearText = MutableLiveData<String>()
    val dateText = MutableLiveData<String>()


    private var selectedDayIndex = 0

    lateinit var selectedCal: Calendar


    fun init(cal: Calendar) {

        selectedCal = cal

        _hourList.value = getTimeList(24)
        _minList.value = getTimeList(60)

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val week = cal.get(Calendar.DAY_OF_WEEK)

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        _selectedHour.value = hour + 2
        _selectedMin.value = min + 2

        yearText.value = "${year}"
        dateText.value = "${month + 1}월 ${day}일 ${getWeek(week)}요일"

        val list = getCalendarList()
        for ((index, date) in list.withIndex()) {
            if (date.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                selectedDayIndex = index
            }
        }

    }

    /**
     * 시간 변경할때(스크롤) 호출됨
     */
    fun setSelectedHour(hour: Int) {
        /**
         * 현재 값하고 다를때만 설정, 아니면 무한 루프됨
         */
        if (_selectedHour.value != hour) {
            _selectedHour.value = hour
        }
    }

    fun setSelectedMin(min: Int) {
        if (_selectedMin.value != min) {
            _selectedMin.value = min
        }
    }

    /**
     * 오늘 날짜가 선택되었을때 현재 시간보다 전 시간을 설정할 수 없도록 변경
     */
    fun getHour(): Int {
        return if (selectedDayIndex == 0) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val min = now.get(Calendar.MINUTE)

            when {
                selectedHour.value!! < hour -> {
                    hour + 2
                }
                (selectedHour.value!! == hour && selectedMin.value!! < min) -> {
                    _selectedMin.value = min
                    _selectedHour.value!!
                }
                else -> {
                    _selectedHour.value!!
                }
            }
        } else {
            _selectedHour.value!!
        }
    }


    fun getMin(): Int {
        return if (selectedDayIndex == 0) {

            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val min = now.get(Calendar.MINUTE)

            if (selectedHour.value!! <= hour && selectedMin.value!! < min) {
                min + 2
            }else {
                selectedMin.value!!
            }
        } else {
            selectedMin.value!!
        }
    }


    fun saveTime(): Calendar {

        selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour.value!!)
        selectedCal.set(Calendar.MINUTE, selectedMin.value!!)
        return selectedCal
    }

}
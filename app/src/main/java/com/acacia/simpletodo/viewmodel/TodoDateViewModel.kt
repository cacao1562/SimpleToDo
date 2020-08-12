package com.acacia.simpletodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acacia.simpletodo.utils.getAddDate
import com.acacia.simpletodo.utils.getDateList
import com.acacia.simpletodo.utils.getDisplayDate
import java.util.*
import kotlin.collections.ArrayList

class TodoDateViewModel : ViewModel() {

    private val _hourList = MutableLiveData<ArrayList<String>>()
    val hourList: LiveData<ArrayList<String>> = _hourList

    private val _minList = MutableLiveData<ArrayList<String>>()
    val minList: LiveData<ArrayList<String>> = _minList

    private val _selectedDay = MutableLiveData<Int>(0)
    val selectedDay: LiveData<Int> = _selectedDay

    private val _selectedHour = MutableLiveData<Int>(0)
    val selectedHour: LiveData<Int> = _selectedHour

    private val _selectedMin = MutableLiveData<Int>(0)
    val selectedMin: LiveData<Int> = _selectedMin

    /**
     * 라디오 버튼 체크
     */
    val date01 = MutableLiveData<String>()
    val date02 = MutableLiveData<String>()
    val date03 = MutableLiveData<String>()
    val date04 = MutableLiveData<String>()
    val date05 = MutableLiveData<String>()
    val date06 = MutableLiveData<String>()
    val date07 = MutableLiveData<String>()

    private val dateList = listOf<Calendar>(getAddDate(0),
                                            getAddDate(1),
                                            getAddDate(2),
                                            getAddDate(3),
                                            getAddDate(4),
                                            getAddDate(5),
                                            getAddDate(6))

    fun init(cal: Calendar?) {

        date01.value = getDisplayDate(dateList[0])
        date02.value = getDisplayDate(dateList[1])
        date03.value = getDisplayDate(dateList[2])
        date04.value = getDisplayDate(dateList[3])
        date05.value = getDisplayDate(dateList[4])
        date06.value = getDisplayDate(dateList[5])
        date07.value = getDisplayDate(dateList[6])

        _hourList.value = getDateList(24)
        _minList.value = getDateList(60)

        cal?.let {
            for ((index, date) in dateList.withIndex()) {
                if (date.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                    _selectedDay.value = index
                }
            }

        }

    }

    fun setSelectedDay(index: Int) {
        /**
         * 오늘이 아닌 다른 날짜에서 현재 시간보다 낮게 설정하고 오늘 날짜 탭 했을때
         * 지금 시간으로 스크롤되게 설정
         */
        if (index == 0) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val min = now.get(Calendar.MINUTE)
            _selectedHour.value = hour
            _selectedMin.value = min
        }
        _selectedDay.value = index
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
        return if (_selectedDay.value == 0) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            if (selectedHour.value!! < hour) {
                hour + 2
            }else {
                _selectedHour.value!!
            }
        }else {
            _selectedHour.value!!
        }
    }

    fun getMin(): Int {
        return if (_selectedDay.value == 0) {
            val now = Calendar.getInstance()
            val min = now.get(Calendar.MINUTE)
            if (selectedMin.value!! < min) {
                min + 2
            }else {
                selectedMin.value!!
            }
        }else {
            selectedMin.value!!
        }
    }

}
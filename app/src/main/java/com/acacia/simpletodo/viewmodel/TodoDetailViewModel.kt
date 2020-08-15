package com.acacia.simpletodo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import com.acacia.simpletodo.utils.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TodoDetailViewModel @Inject constructor(private val todoRepository: TodoRepository) :
    ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

//    private val _task = MutableLiveData<TodoEntity>()
//    val task: LiveData<TodoEntity> = _task

    private val _selectedDay = MutableLiveData<Int>(0)
    val selectedDay: LiveData<Int> = _selectedDay

    // Month bar view layout_weight
    val month01 = MutableLiveData<Float>(0f)
    val month02 = MutableLiveData<Float>(0f)

    // Month bar view textView text
    val monthTitle01 = MutableLiveData<String>()
    val monthTitle02 = MutableLiveData<String>()

    private var taskId: String? = null

    lateinit var selectedCalendar: Calendar

    val notiDate = MutableLiveData<String>()
    var notiTime = MutableLiveData<String>()

    val isChecked = MutableLiveData<Boolean>(false)

    fun loadTodo(id: String?) {

        taskId = id

        id?.let {
            viewModelScope.launch {
//                _task.value = todoRepository.getTodoById(id)
                val todo = todoRepository.getTodoById(id)
                todo?.let {
                    title.value = todo.title
                    description.value = todo.description
                    _selectedDay.value = getDatePosition(todo.date)
                    isChecked.value = !todo.notiDate.isNullOrEmpty()

                }
            }
        }

        initMonthBar()


    }

    private fun initMonthBar() {

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

    fun updateTodo() {

        val title = title.value

        if (title.isNullOrEmpty()) {
            ToastHelper.showToast("제목을 입력해 주세요..")
            return
        }

        val des = description.value
        val date = getStringDate(_selectedDay.value!!)
        var notiTime = ""

        if (isChecked.value!!) {
            if (selectedCalendar.before(Calendar.getInstance())) {
                ToastHelper.showToast("알림 시간을 현재 시간 이후로 설정해주세요.")
                return
            }else {
                notiTime = getStringNotiTime(selectedCalendar)
            }
        }

        taskId?.let {
            val todo = TodoEntity(title = title, description = des ?: "", id = it, date = date, notiDate = notiTime)
            update(todo)
        } ?: run {
            val todo = TodoEntity(title = title, description = des ?: "", date = date, notiDate = notiTime)
            update(todo)
        }

    }

    private fun update(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
        }
    }

    fun getRadioText(index: Int): String {
        val list = getCalendarList()
        return getDisplayDate(list[index])
    }


    fun setSelectedDay(index: Int) {
        _selectedDay.value = index
        selectedCalendar = getCalendarList()[index]
    }

    fun setNotiView(cal: Calendar) {
        selectedCalendar = cal
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val week = cal.get(Calendar.DAY_OF_WEEK)

        notiDate.value = "${year}.${month}.${day} (${getWeek(week)}요일)"

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        notiTime.value = "${hour}시 ${min}분"
    }
}
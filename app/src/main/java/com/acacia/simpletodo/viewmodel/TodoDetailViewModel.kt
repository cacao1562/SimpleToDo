package com.acacia.simpletodo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import com.acacia.simpletodo.utils.getCalendarList
import com.acacia.simpletodo.utils.getDatePosition
import com.acacia.simpletodo.utils.getDisplayDate
import com.acacia.simpletodo.utils.getStringDate
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

    val month01 = MutableLiveData<Float>(0f)
    val month02 = MutableLiveData<Float>(0f)

    val monthTitle01 = MutableLiveData<String>()
    val monthTitle02 = MutableLiveData<String>()

    private var taskId: String? = null

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
                }
            }
        }

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
        val des = description.value
        val date = getStringDate(_selectedDay.value!!)

        if (title.isNullOrEmpty()) {
            return
        }

        taskId?.let {
            val todo = TodoEntity(title = title, description = des ?: "", id = it, date = date)
            update(todo)
        } ?: run {
            val todo = TodoEntity(title = title, description = des ?: "", date = date)
            update(todo)
        }

    }

    private fun update(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
        }
    }

    fun setRadioText(index: Int): String {
        val list = getCalendarList()
        return getDisplayDate(list[index])
    }


    fun setSelectedDay(index: Int) {
        _selectedDay.value = index
    }
}
package com.acacia.simpletodo.viewmodel

import android.graphics.drawable.AnimatedVectorDrawable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.R
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import com.acacia.simpletodo.todo.detail.TodoDay
import com.acacia.simpletodo.utils.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TodoDetailViewModel @Inject constructor(private val todoRepository: TodoRepository) :
    ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val todoTitle = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    // RadidoButton checked value
    private val _selectedDay = MutableLiveData<Int>(0)
    val selectedDay: LiveData<Int> = _selectedDay

    var beforeSelectedDay = 0

    // Month bar view layout_weight
    val month01 = MutableLiveData<Float>(0f)
    val month02 = MutableLiveData<Float>(0f)

    // Month bar view textView text
    val monthTitle01 = MutableLiveData<String>()
    val monthTitle02 = MutableLiveData<String>()

    var taskId = MutableLiveData<Int>(-1)

    lateinit var selectedCalendar: Calendar

    // Noti Date view text
    val notiDate = MutableLiveData<String>()
    var notiTime = MutableLiveData<String>()

    var todoEntity = MutableLiveData<TodoEntity>()

    // Noti Date view switch
    val isChecked = MutableLiveData<Boolean>(false)

    val isUpdated = MutableLiveData<Boolean>(false)

    private var isInitChecked = false
    private var initNotiDate = ""

    var isEditMode = false

    fun loadTodo(id: Int) {

        taskId.value = id

        if (taskId.value != -1) {
            isEditMode = true
            viewModelScope.launch {
//                _task.value = todoRepository.getTodoById(id)
                val todo = todoRepository.getTodoById(id)
                todo?.let {
                    todoEntity.value = todo
                    todoTitle.value = todo.title
                    description.value = todo.description
                    _selectedDay.value = getDatePosition(todo.date)
                    isInitChecked = !todo.notiDate.isNullOrEmpty()
                    if (todo.notiDate.isEmpty()) {
                        isChecked.value = false
                    }else {
                        isChecked.value = true
                        val date =  todo.notiDate
                        initNotiDate = date
                        val cal = getStringToCalendar(date)
                        cal?.let {
                            setNotiView(it)
                        }
                    }
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

        val title = todoTitle.value

        if (title.isNullOrEmpty()) {
            ToastHelper.showToast("제목을 입력해 주세요..")
            return
        }

        val des = description.value
        val date = getStringDate(_selectedDay.value!!)
        var notiTime = ""

        if (isChecked.value!!) {
            selectedCalendar?.let {
                if (it.before(Calendar.getInstance())) {
                    ToastHelper.showToast("알림 시간을 현재 시간 이후로 설정해주세요.")
                    return
                } else {
                    notiTime = getCalendarToString(it)
                }
            }
        }

        if (taskId.value == -1) {
            val todo =
                TodoEntity(title = title, description = des ?: "", date = date, notiDate = notiTime)
            update(todo)
        } else {
            val todo = TodoEntity(
                title = title,
                description = des ?: "",
                id = taskId.value!!,
                date = date,
                notiDate = notiTime
            )
            update(todo)
        }

    }

    private fun update(todo: TodoEntity) {
        viewModelScope.launch {
            val id = todoRepository.insertTodo(todo)
            if (taskId.value == -1) {
                id?.let {
                    taskId.value = it.toInt()
                }
            }
            isUpdated.value = true
        }
    }

    fun getRadioText(index: Int): String {
        val list = getCalendarList()
        return getDisplayDate(list[index])
    }


    fun setSelectedDay(index: Int) {
        _selectedDay.value = index
        val cal = Calendar.getInstance().apply {
            add(Calendar.DATE, index)
        }
        selectedCalendar = cal
    }

    fun setNotiView(cal: Calendar) {
        selectedCalendar = cal
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val week = cal.get(Calendar.DAY_OF_WEEK)

        notiDate.value = "${year}.${month + 1}.${day} (${getWeek(week)}요일)"

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        notiTime.value = "${hour}시 ${min}분"
    }

    fun isModifyTodo(): Boolean {

        if (taskId.value == -1) return true

        Log.d("hhh", "title = ${todoEntity.value?.title} == ${todoTitle.value}")
        Log.d("hhh", "description = ${todoEntity.value?.description} == ${description.value}")
        Log.d("hhh", "date = ${todoEntity.value?.date} == ${getStringDate(_selectedDay.value!!)}")
        Log.d("hhh", "notiDate = ${todoEntity.value?.notiDate} == ${getCalendarToString(selectedCalendar)}")
        return todoEntity.value?.title == todoTitle.value &&
               todoEntity.value?.description == description.value &&
               todoEntity.value?.date == getStringDate(_selectedDay.value!!) &&
               isModifyNoti()
    }

    private fun isModifyNoti(): Boolean {
        return if (todoEntity.value?.notiDate.isNullOrEmpty()) {
            true
        }else {
            (todoEntity.value?.notiDate == initNotiDate && isInitChecked == isChecked.value)
        }
    }



    fun onClickEditSave(view: View) {

        if (view is AppCompatImageButton) {
            view.setImageResource(if (isEditMode) R.drawable.avd_anim_edit_save else R.drawable.avd_anim_save_edit )
            val icon = view.drawable
            isEditMode = !isEditMode
            if (icon is AnimatedVectorDrawable) {
                icon.start()
            }
        }

    }
}
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
import com.acacia.simpletodo.utils.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class TodoDetailViewModel @Inject constructor(private val todoRepository: TodoRepository) :
    ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val todoTitle = MutableLiveData<String>()

    val errorTitle = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val todoDescription = MutableLiveData<String>()

    var isCompleted = false
    val isInitCompleted = MutableLiveData<Boolean>(false)

    // 0 ~ 6 날짜 인덱스
    private val _selectedDayIndex = MutableLiveData<Int>(0)
    val selectedDayIndex: LiveData<Int> = _selectedDayIndex

    var beforeSelectedDay = 0

    // Month bar view layout_weight
    val month01 = MutableLiveData<Float>(0f)
    val month02 = MutableLiveData<Float>(0f)

    // Month bar view textView text
    val monthTitle01 = MutableLiveData<String>()
    val monthTitle02 = MutableLiveData<String>()

    var todoId = MutableLiveData<Int>(-1)

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

        todoId.value = id
        setSelectedDay(_selectedDayIndex.value!!)

        if (todoId.value != -1) {
            isEditMode = true
            viewModelScope.launch {
//                _task.value = todoRepository.getTodoById(id)
                val todo = todoRepository.getTodoById(id)
                todo?.let {
                    todoEntity.value = todo
                    todoTitle.value = todo.title
                    todoDescription.value = todo.description
                    isInitCompleted.value = todo.isCompleted
                    isCompleted = todo.isCompleted
                    _selectedDayIndex.value = getDatePosition(todo.date)
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
            errorTitle.value = "제목을 입력해 주세요"
            ToastHelper.showToast("제목을 입력해 주세요..")
            return
        }

        val des = todoDescription.value
        val date = getStringDate(_selectedDayIndex.value!!)
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

        if (todoId.value == -1) {
            val todo =
                TodoEntity(title = title, description = des ?: "", date = date, notiDate = notiTime)
            update(todo)
        } else {
            val todo = TodoEntity(
                title = title,
                description = des ?: "",
                id = todoId.value!!,
                date = date,
                notiDate = notiTime
            )
            update(todo)
        }

    }

    /**
     * room 데이터베이스에 업데이트,
     * 끝나면 알림등록하고 뒤로가기
     */
    private fun update(todo: TodoEntity) {
        viewModelScope.launch {
            val id = todoRepository.insertTodo(todo)
            if (todoId.value == -1) {
                id?.let {
                    todoId.value = it.toInt()
                }
            }
            isUpdated.value = true
        }
    }

    // delete
    fun getRadioText(index: Int): String {
        val list = getCalendarList()
        return getDisplayDate(list[index])
    }


    /**
     * 날짜 선택 했을때, noti 설정에 날짜만 보이도록 세팅
     */
    fun setSelectedDay(index: Int) {
        _selectedDayIndex.value = index
        val cal = Calendar.getInstance().apply {
            // 오늘 날짜에서 0~6 선택한 날짜를 더 한다
            add(Calendar.DATE, index)
        }
        selectedCalendar = cal
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val week = cal.get(Calendar.DAY_OF_WEEK)

        notiDate.value = "${year}.${month + 1}.${day} (${getWeek(week)})"
    }

    /**
     * 진입했을때 이미 설정 되어있을때,
     * DatePickerDialog 시간 콜백 받았을때
     */
    fun setNotiView(cal: Calendar) {
        selectedCalendar = cal
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DATE)
        val week = cal.get(Calendar.DAY_OF_WEEK)

        notiDate.value = "${year}.${month + 1}.${day} (${getWeek(week)})"

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        notiTime.value = "${hour}시 ${min}분"
    }

    /**
     * 이미 추가했던 todo를 수정했는지 체크
     */
    fun isModifyTodo(): Boolean {

        if (todoId.value == -1) return true

        Log.d("hhh", "title = ${todoEntity.value?.title} == ${todoTitle.value}")
        Log.d("hhh", "description = ${todoEntity.value?.description} == ${todoDescription.value}")
        Log.d("hhh", "date = ${todoEntity.value?.date} == ${getStringDate(_selectedDayIndex.value!!)}")
        Log.d("hhh", "notiDate = ${todoEntity.value?.notiDate} == ${getCalendarToString(selectedCalendar)}")
        return todoEntity.value?.title == todoTitle.value &&
               todoEntity.value?.description == todoDescription.value &&
               todoEntity.value?.date == getStringDate(_selectedDayIndex.value!!) &&
               isModifyNoti()
    }

    private fun isModifyNoti(): Boolean {
        return if (todoEntity.value?.notiDate.isNullOrEmpty()) {
            true
        }else {
            (todoEntity.value?.notiDate == initNotiDate && isInitChecked == isChecked.value)
        }
    }


    fun onClickNotiSwitch(isOn: Boolean) {
        isChecked.value = isOn
    }


    fun onClickCompleted(view: View) {

        if (view is AppCompatImageButton) {
            view.setImageResource(if (isCompleted) R.drawable.avd_anim_to_unchecked else R.drawable.avd_anim_to_checked )
            val icon = view.drawable
            isCompleted = !isCompleted
            viewModelScope.launch {
                if (todoId.value != -1) {
                    todoRepository.updateCompleted(todoId.value!!, isCompleted)
                }
            }

            if (icon is AnimatedVectorDrawable) {
                icon.start()
            }
        }

    }
}
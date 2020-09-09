package com.acacia.seventodo.viewmodel

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.seventodo.database.TodoEntity
import com.acacia.seventodo.repository.TodoRepository
import com.acacia.seventodo.todo.history.HistoryType
import com.acacia.seventodo.todo.history.TodoHistoryEntity
import com.acacia.seventodo.utils.dpToPx
import com.acacia.seventodo.utils.getDeviceWidth
import com.acacia.seventodo.utils.getStringDate
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoHistoryViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {

    private val _items = MutableLiveData<List<TodoHistoryEntity>>().apply { value = emptyList() }
    val items: LiveData<List<TodoHistoryEntity>> = _items

    fun loadHistory() {

        viewModelScope.launch {
            val today = getStringDate(0)
            val last = getStringDate(6)
            todoRepository.getTaskNotBetweenDate(today, last)?.let {
                _items.value = addType(it)
            }
        }
    }

    private fun addType(list: List<TodoEntity>): List<TodoHistoryEntity> {

        var previous = ""
        val historyList = mutableListOf<TodoHistoryEntity>()

        for (todo in list) {
            if (previous != todo.date) {
                previous = todo.date
                val section = TodoHistoryEntity(HistoryType.Section.ordinal, todo.title, todo.description, todo.date, todo.id)
                historyList.add(section)
            }
            val data = TodoHistoryEntity(HistoryType.Data.ordinal, todo.title, todo.description, todo.date, todo.id)
            historyList.add(data)
        }
        return historyList
    }

    fun showPopupDelete(v: View) {
        val li =
            v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = li.inflate(com.acacia.seventodo.R.layout.layout_custom_popupmenu, null)
        val deleteTextview = popupView.findViewById<AppCompatTextView>(com.acacia.seventodo.R.id.layout_custom_tv_delete)


        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
//        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isOutsideTouchable = true

        deleteTextview.setOnClickListener {
            popupWindow.dismiss()

            viewModelScope.launch {
                val today = getStringDate(0)
                val last = getStringDate(6)
                todoRepository.deleteTodoBetween(today, last)
                loadHistory()
            }
        }
//        popupWindow.showAsDropDown(v)
        popupWindow.showAtLocation(v, Gravity.RIGHT or Gravity.TOP, 0, 0)

//        if (window[1] > height) {
//            popupWindow.showAsDropDown(v, v.context.getDeviceWidth(), -(80.dpToPx(v.context)))
//        }else {
//            popupWindow.showAsDropDown(v, v.context.getDeviceWidth(), -(40.dpToPx(v.context)))
//        }
//        popupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0)
    }
}
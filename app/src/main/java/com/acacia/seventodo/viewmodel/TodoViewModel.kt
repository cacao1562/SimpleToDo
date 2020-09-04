package com.acacia.seventodo.viewmodel

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
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
import com.acacia.seventodo.utils.dpToPx
import com.acacia.seventodo.utils.getDeviceWidth
import com.acacia.seventodo.utils.getStringDate
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _items = MutableLiveData<List<TodoEntity>>().apply { value = emptyList() }
    val items: LiveData<List<TodoEntity>> = _items

    private val _mainList = MutableLiveData<List<TodoEntity>>().apply { value = emptyList() }
    val mainList: LiveData<List<TodoEntity>> = _mainList

    private val _todoId = MutableLiveData<Int>()
    val todoId: LiveData<Int> = _todoId

    var initIndex = 0

    fun getTodoList(index: Int) {
        initIndex = index
        viewModelScope.launch {
            val date = getStringDate(index)
            todoRepository.getTodoByDate(date)?.let {
                _items.value = it
            }
            val today = getStringDate(0)
            val last = getStringDate(6)
            todoRepository.getTaskBetweenDate(today, last)?.let {
                _mainList.value = it
            }
        }
    }

    fun openTodoDetail(todoId: Int) {
        _todoId.value = todoId
    }

    fun showPopup(v: View, todoId: Int) {

        val window = IntArray(2)
        val screen = IntArray(2)

        v.getLocationInWindow(window)
        v.getLocationOnScreen(screen)

        val displayMetrics: DisplayMetrics = v.context.resources.displayMetrics

        /**
         * 0.9 임의로 정한 값, 리스트 마지막 아이템에 팝업 윈도우가 안보여 클릭한 뷰의 윈도우 y값을 체크해서 위치 조정
         */
        val height = displayMetrics.heightPixels * 0.9

        val li =
            v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = li.inflate(com.acacia.seventodo.R.layout.layout_custom_popupmenu, null)
        val textview = popupView.findViewById<AppCompatTextView>(com.acacia.seventodo.R.id.layout_custom_tv_delete)


        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
//        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            //TODO do sth here on dismiss
        })

        textview.setOnClickListener {
            popupWindow.dismiss()

            viewModelScope.launch {
                todoRepository.deleteTodoById(todoId)
                getTodoList(initIndex)
//                isDeleted.value = true
            }
        }

        if (window[1] > height) {
            popupWindow.showAsDropDown(v, v.context.getDeviceWidth(), -(80.dpToPx(v.context)))
        }else {
            popupWindow.showAsDropDown(v, v.context.getDeviceWidth(), -(40.dpToPx(v.context)))
        }
//        popupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0)
    }

    fun updateCompleted(todoId: Int, isChecked: Boolean) {
        Log.d("ppp", "updateCompleted  id = $todoId , isChecked = $isChecked")
        viewModelScope.launch {
            todoRepository.updateCompleted(todoId, isChecked)
            getTodoList(initIndex)
        }
    }

    fun deleteTodod(todoId: Int) {
        viewModelScope.launch {
            todoRepository.deleteTodoById(todoId)
            getTodoList(initIndex)
//                isDeleted.value = true
        }
    }
}
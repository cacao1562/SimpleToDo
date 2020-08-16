package com.acacia.simpletodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import com.acacia.simpletodo.utils.getStringDate
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _items = MutableLiveData<List<TodoEntity>>().apply { value = emptyList() }
    val items: LiveData<List<TodoEntity>> = _items

    private val _todoId = MutableLiveData<Int>()
    val todoId: LiveData<Int> = _todoId

    fun getTodoList(index: Int) {
        viewModelScope.launch {
//            _items.value = todoRepository.getTodoList()
            val date = getStringDate(index)
            todoRepository.getTodoByDate(date)?.let {
                _items.value = it
            }
        }
    }

    fun insertTodo() {
        viewModelScope.launch {

            val currentTitle = title.value
            val currentDescription = description.value

            if (currentTitle.isNullOrEmpty()) {
                return@launch
            }
            todoRepository.insertTodo(TodoEntity(currentTitle, currentDescription ?: ""))

//            getTodoList()
        }
    }

    fun openTodoDetail(todoId: Int) {
        _todoId.value = todoId
    }
}
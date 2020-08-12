package com.acacia.simpletodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _items = MutableLiveData<List<TodoEntity>>().apply { value = emptyList() }
    val items: LiveData<List<TodoEntity>> = _items

    private val _todoId = MutableLiveData<String>()
    val todoId: LiveData<String> = _todoId

    fun getTodoList() {
        viewModelScope.launch {
            _items.value = todoRepository.getTodoList()
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

    fun openTodoDetail(todoId: String) {
        _todoId.value = todoId
    }
}
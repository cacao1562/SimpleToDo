package com.acacia.simpletodo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acacia.simpletodo.database.TodoEntity
import com.acacia.simpletodo.repository.TodoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoDetailViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {


    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

//    private val _task = MutableLiveData<TodoEntity>()
//    val task: LiveData<TodoEntity> = _task

    private var taskId: String? = null

    fun loadTodo(id: String?) {

        taskId = id

        id?.let {
            viewModelScope.launch {
//                _task.value = todoRepository.getTodoById(id)
                val todo = todoRepository.getTodoById(id)
                title.value = todo?.title
                description.value = todo?.description
            }
        }

    }

    fun updateTodo() {

        val title = title.value
        val des = description.value

        if (title.isNullOrEmpty()) {
            return
        }

        taskId?.let {
            val todo = TodoEntity(title = title, description = des ?: "", id = it)
            update(todo)
        } ?: run {
            val todo = TodoEntity(title = title, description = des ?: "")
            update(todo)
        }

    }

    private fun update(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
        }
    }
}
package com.acacia.simpletodo.repository

import com.acacia.simpletodo.database.TodoDAO
import com.acacia.simpletodo.database.TodoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(private val todoDao: TodoDAO) {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getTodoList() =
        withContext(ioDispatcher) {
            return@withContext try {
                 todoDao.getTasks()
            }catch (e: Exception) {
                null
            }
        }

    suspend fun insertTodo(todo: TodoEntity) {
        withContext(ioDispatcher) {
            todoDao.insertTask(todo)
        }
    }

    suspend fun getTodoById(id: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.getTaskById(id)
            }catch (e: Exception) {
                null
            }
        }

}
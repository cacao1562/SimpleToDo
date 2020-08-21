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

    suspend fun insertTodo(todo: TodoEntity) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.insertTask(todo)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


    suspend fun getTodoById(id: Int) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.getTaskById(id)
            }catch (e: Exception) {
                null
            }
        }

    suspend fun getTodoByDate(date: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.getTaskByDate(date)
            }catch (e: Exception) {
                null
            }
        }

    suspend fun deleteTodoById(id: Int) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.deleteTaskById(id)
            }catch (e: Exception) {
                -1
            }
        }
}
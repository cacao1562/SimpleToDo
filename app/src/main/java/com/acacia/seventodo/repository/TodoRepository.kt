package com.acacia.seventodo.repository

import com.acacia.seventodo.database.TodoDAO
import com.acacia.seventodo.database.TodoEntity
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
                e.printStackTrace()
                null
            }
        }

    suspend fun getTodoByDate(date: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.getTaskByDate(date)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun deleteTodoById(id: Int) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.deleteTaskById(id)
            }catch (e: Exception) {
                e.printStackTrace()
                -1
            }
        }

    suspend fun updateCompleted(id: Int, completed: Boolean) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.updateCompleted(id, completed)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun getTaskBetweenDate(from: String, to: String) =
        withContext(ioDispatcher) {
            return@withContext try {
                todoDao.getTaskBetweenDate(from, to)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
package com.acacia.seventodo.database

import androidx.room.*

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface TodoDAO {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM todolist")
    suspend fun getTasks(): List<TodoEntity>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM todoList WHERE entryid = :taskId")
    suspend fun getTaskById(taskId: Int): TodoEntity?


    @Query("SELECT * FROM todoList WHERE date = :date")
    suspend fun getTaskByDate(date: String): List<TodoEntity>?

    @Query("SELECT * FROM todoList WHERE date BETWEEN :from AND :to")
    suspend fun getTaskBetweenDate(from: String, to: String): List<TodoEntity>?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoEntity): Long

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    suspend fun updateTask(task: TodoEntity): Int

    /**
     * Update the complete status of a task
     *
     * @param taskId    id of the task
     * @param completed status to be updated
     */
    @Query("UPDATE todoList SET completed = :completed WHERE entryid = :taskId")
    suspend fun updateCompleted(taskId: Int, completed: Boolean)

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM todoList WHERE entryid = :taskId")
    suspend fun deleteTaskById(taskId: Int): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM todoList")
    suspend fun deleteTasks()

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("DELETE FROM todoList WHERE completed = 1")
    suspend fun deleteCompletedTasks(): Int
}
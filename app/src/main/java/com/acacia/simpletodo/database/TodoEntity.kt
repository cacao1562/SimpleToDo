package com.acacia.simpletodo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "todoList")
data class TodoEntity @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
    @ColumnInfo(name = "date") var date: String = "",
    @ColumnInfo(name = "notiDate") var notiDate: String = "",
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "entryid") var id: Int = 0
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description


    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}
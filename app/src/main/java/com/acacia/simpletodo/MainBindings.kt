package com.acacia.simpletodo

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acacia.simpletodo.database.TodoEntity

object MainBindings {

    @JvmStatic
    @BindingAdapter("items")
    fun setItems(listView: RecyclerView, items: List<TodoEntity>) {
        (listView.adapter as TodoAdapter).submitList(items)
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(listView: RecyclerView, items: ArrayList<String>) {
        listView.adapter = DatePickerAdapter(items)
    }

    @JvmStatic
    @BindingAdapter("completedTask")
    fun setStyle(textView: TextView, enabled: Boolean) {
        if (enabled) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

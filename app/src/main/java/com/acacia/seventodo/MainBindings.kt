package com.acacia.seventodo

import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acacia.seventodo.database.TodoEntity
import com.acacia.seventodo.todo.datedialog.DatePickerAdapter
import com.acacia.seventodo.todo.history.HistoryAdapter
import com.acacia.seventodo.todo.history.TodoHistoryEntity
import com.acacia.seventodo.todo.list.TodoAdapter
import com.acacia.seventodo.viewmodel.TodoViewModel


object MainBindings {

    @JvmStatic
    @BindingAdapter("items")
    fun setItems(listView: RecyclerView, items: List<TodoEntity>) {
        (listView.adapter as TodoAdapter).submitList(items)
    }

    @JvmStatic
    @BindingAdapter("historyItems")
    fun setHistoryItems(listView: RecyclerView, items: List<TodoHistoryEntity>) {
        (listView.adapter as HistoryAdapter).submitList(items)
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(listView: RecyclerView, items: ArrayList<String>) {
        listView.adapter =
            DatePickerAdapter(items)
    }

    @JvmStatic
    @BindingAdapter("completedTask")
    fun setStyle(textView: TextView, enabled: Boolean) {
        if (enabled) {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorA3))
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.color222222))
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    @JvmStatic
    @BindingAdapter("android:layout_weight")
    fun setLayoutWeight(view: View, weight: Float) {
        val layoutParams = view.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.let {
            it.weight = weight
            view.layoutParams = it
        }
    }

    @JvmStatic
    @BindingAdapter("android:textStyle")
    fun setTypeface(textView: AppCompatTextView, style: String?) {
        when (style) {
            "bold" -> textView.setTypeface(null, Typeface.BOLD)
            else -> textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("android:textStyle")
    fun setTypefaceRadio(radioButton: RadioButton, style: String?) {
        when (style) {
            "bold" -> radioButton.setTypeface(null, Typeface.BOLD)
            else -> radioButton.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("onChecked_vm", "onChecked_id")
    fun setOnCheckChange(view: AppCompatCheckBox, viewModel: TodoViewModel, todoId: Int) {
//        view.setOnCheckedChangeListener(null)
        view.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateCompleted(todoId, isChecked)
        }
    }
}

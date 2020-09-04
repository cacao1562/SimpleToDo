package com.acacia.seventodo.todo.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acacia.seventodo.databinding.DayItemBinding
import com.acacia.seventodo.viewmodel.TodoDetailViewModel

data class TodoDay(val day: String, val week: String)

class TodoDayAdapter(private val viewModel: TodoDetailViewModel,
                     private val list: List<TodoDay>):
                     RecyclerView.Adapter<TodoDayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, list[position])
    }

    class ViewHolder private constructor(val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vm: TodoDetailViewModel, todoDay: TodoDay) {
            binding.day = todoDay
            binding.viewModel = vm
            binding.index = adapterPosition
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DayItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }
}
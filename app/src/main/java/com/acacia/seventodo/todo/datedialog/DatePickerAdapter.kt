package com.acacia.seventodo.todo.datedialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acacia.seventodo.databinding.DateItemBinding

class DatePickerAdapter(private val list: ArrayList<String>) : RecyclerView.Adapter<DatePickerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ViewHolder private constructor(val binding: DateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(str: String) {

//            binding.executePendingBindings()
            binding.dateItemTvDate.text = str
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DateItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }
}
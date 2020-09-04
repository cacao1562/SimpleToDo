package com.acacia.seventodo.todo.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acacia.seventodo.R
import com.acacia.seventodo.database.TodoEntity
import com.acacia.seventodo.databinding.TodoItemBinding
import com.acacia.seventodo.viewmodel.TodoViewModel

/**
 * Adapter for the task list. Has a reference to the [TasksViewModel] to send actions back to it.
 */
class TodoAdapter(private val viewModel: TodoViewModel) :
    ListAdapter<TodoEntity, TodoAdapter.ViewHolder>(
        TaskDiffCallback()
    ) {

    fun getTodoId(pos: Int) = getItem(pos).id

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

//        ObjectAnimator.ofFloat(holder.itemView, "alpha", 0.0f, 1.0f).start()
        val ani = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
        holder.itemView.startAnimation(ani)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TodoViewModel, item: TodoEntity) {

            binding.viewmodel = viewModel
            binding.todo = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TodoItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class TaskDiffCallback : DiffUtil.ItemCallback<TodoEntity>() {
    override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
        return oldItem == newItem
    }
}
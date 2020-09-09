package com.acacia.seventodo.todo.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acacia.seventodo.R
import com.acacia.seventodo.viewmodel.TodoHistoryViewModel
import com.acacia.seventodo.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.item_history_data.view.*
import kotlinx.android.synthetic.main.item_history_section.view.*


data class TodoHistoryEntity(
    val type: Int,
    val title: String,
    val description: String,
    val date: String,
    val id: Int
)

enum class HistoryType {
    Section,
    Data
}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(obj: T)
}

class HistoryAdapter (private val viewModel: TodoHistoryViewModel) :
    ListAdapter<TodoHistoryEntity, BaseViewHolder<*>>(
        HistoryDiffCallback()
    ) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when(viewType) {
            HistoryType.Section.ordinal -> {
                SectionViewHolder(inflater.inflate(R.layout.item_history_section, parent, false))
            }
            HistoryType.Data.ordinal -> {
                DataViewHolder(inflater.inflate(R.layout.item_history_data, parent, false))
            }
            else -> {
                SectionViewHolder(inflater.inflate(R.layout.item_history_section, parent, false))
            }
        }
    }

    class SectionViewHolder internal constructor(itemView: View) :
        BaseViewHolder<TodoHistoryEntity>(itemView) {

        override fun bind(obj: TodoHistoryEntity) {

            itemView.historyItem_tv_date.text = obj.date
        }
    }

    class DataViewHolder internal constructor(itemView: View) :
        BaseViewHolder<TodoHistoryEntity>(itemView) {

        override fun bind(obj: TodoHistoryEntity) {
            itemView.historyItem_tv_title.text = obj.title

            if (obj.description.isNotEmpty()) {
                itemView.historyItem_tv_description.text = obj.description
            }else {
                itemView.historyItem_tv_description.visibility = View.GONE
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val ani = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
        holder.itemView.startAnimation(ani)
        (holder as BaseViewHolder<Any>).bind(getItem(position))
    }

}

class HistoryDiffCallback : DiffUtil.ItemCallback<TodoHistoryEntity>() {
    override fun areItemsTheSame(oldItem: TodoHistoryEntity, newItem: TodoHistoryEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoHistoryEntity, newItem: TodoHistoryEntity): Boolean {
        return oldItem == newItem
    }
}
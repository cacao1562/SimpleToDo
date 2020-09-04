package com.acacia.seventodo.todo.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TodoViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private val fmList: ArrayList<Fragment>) : FragmentStateAdapter(fm, lifecycle) {

    private val fmIds = fmList.map { it.hashCode().toLong() }

    override fun getItemCount(): Int {
        return fmList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fmList[position]
    }

    override fun getItemId(position: Int): Long {
        return fmList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return fmIds.contains(itemId)
    }
}
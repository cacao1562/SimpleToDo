package com.acacia.simpletodo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.acacia.simpletodo.databinding.FragmentTodoDetailBinding
import com.acacia.simpletodo.databinding.FragmentTodoListBinding
import com.acacia.simpletodo.databinding.FragmentTodoMainBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.utils.getDate
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import com.acacia.simpletodo.viewmodel.TodoViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_todo_main.*
import java.util.*
import javax.inject.Inject

class TodoMainFragment : Fragment() {

    private lateinit var binding: FragmentTodoMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("ung", "Main fm onActivityCreated")
        binding.lifecycleOwner = this
        binding.todoMainFmViewpager.adapter =
            TodoViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle,
                arrayListOf(
                    TodoListFragment(),
                    TodoListFragment(),
                    TodoListFragment(),
                    TodoListFragment(),
                    TodoListFragment(),
                    TodoListFragment(),
                    TodoListFragment()))

        binding.todoMainFmViewpager.adapter?.notifyDataSetChanged()
//        TabLayoutMediator(binding.todoMainFmTabs, binding.todoMainFmViewpager,
//            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
//
//                val today = Calendar.getInstance()
//                today.get(Calendar.MONTH)
//                today.get(Calendar.DATE)
//                today.get(Calendar.DAY_OF_WEEK)
//                when (position) {
//                    0 -> { tab.text = getDate(position)}
//                    1 -> { tab.text = "TAB TWO"}
//                }
//            }).attach()

        TabLayoutMediator(binding.todoMainFmTabs, binding.todoMainFmViewpager) { tab, position ->
            tab.text = getDate(position)
        }.attach()


        todoMainFm_fbtn_addTodo.setOnClickListener {
            openTodoDetail(null)
        }
//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
    }

    fun openTodoDetail(todoId: String?) {
        val action = TodoMainFragmentDirections.actionTodoMainFragmentToTodoDetailFragment2(todoId)
        findNavController().navigate(action)
    }


}
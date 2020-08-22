package com.acacia.simpletodo.todo.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.acacia.simpletodo.R
import com.acacia.simpletodo.TodoApplication
import com.acacia.simpletodo.todo.list.TodoListFragment
import com.acacia.simpletodo.databinding.FragmentTodoMainBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.utils.getDate
import com.acacia.simpletodo.viewmodel.TodoMainViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_todo_main.*
import javax.inject.Inject

class TodoMainFragment : Fragment() {

    private lateinit var binding: FragmentTodoMainBinding

    private val viewModel by viewModels<TodoMainViewModel>()

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

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        viewModel.initMonthBar()
        binding.todoMainFmViewpager.adapter =
            TodoViewPagerAdapter(
                childFragmentManager, lifecycle,
                arrayListOf(
                    TodoListFragment.newInstance(0),
                    TodoListFragment.newInstance(1),
                    TodoListFragment.newInstance(2),
                    TodoListFragment.newInstance(3),
                    TodoListFragment.newInstance(4),
                    TodoListFragment.newInstance(5),
                    TodoListFragment.newInstance(6)
                )
            )

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

        binding.todoMainFmTabs.setTabTextColors(Color.WHITE, Color.GREEN)

        todoMainFm_fbtn_addTodo.setOnClickListener {
            openTodoDetail(-1)
        }

        binding.todoMainFmAppbarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

                if (verticalOffset == 0) {
                    binding.todoMainFmFbtnAddTodo.show()
                }else if (-verticalOffset == binding.todoMainFmAppbarLayout.totalScrollRange) {
                    binding.todoMainFmFbtnAddTodo.hide()
                }
            }
        })

        // viewpager2 swipe enable
//        binding.todoMainFmViewpager.isUserInputEnabled = false

//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
//        binding.todoMainFmTabs.addTab(binding.todoMainFmTabs.newTab().setText("31\n월"))
    }

    fun openTodoDetail(todoId: Int) {
        val action =
            TodoMainFragmentDirections.actionTodoMainFragmentToTodoDetailFragment2(
                todoId
            )
        findNavController().navigate(action)
    }

}
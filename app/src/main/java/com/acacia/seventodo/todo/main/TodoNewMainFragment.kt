package com.acacia.seventodo.todo.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.acacia.seventodo.R
import com.acacia.seventodo.database.TodoEntity
import com.acacia.seventodo.databinding.FragmentTodoNewMainBinding
import com.acacia.seventodo.todo.list.TodoListFragment
import com.acacia.seventodo.utils.getStringDate
import com.acacia.seventodo.viewmodel.TodoMainViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_todo_new_main.*
import kotlin.math.abs


class TodoNewMainFragment : Fragment() {

    private val viewModel by viewModels<TodoMainViewModel>()

    private lateinit var binding: FragmentTodoNewMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoNewMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        mainFm_btn_add.setOnClickListener {
            openTodoDetail(-1)
        }

        viewModel.listNumber.observe(viewLifecycleOwner, Observer {
            val tr = activity?.supportFragmentManager?.beginTransaction()
            tr?.replace(R.id.mainFm_list_frame, TodoListFragment.newInstance(it))
            tr?.commitAllowingStateLoss()
        })

        binding.mainFmAppbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val ratio = verticalOffset.toFloat() / binding.mainFmAppbarlayout.totalScrollRange
            val rgb = (255 * abs(ratio)).toInt()
            binding.mainFmCountLinear.alpha = ratio + 1
        })

        binding.mainFmBtnSetting.setOnClickListener {
            val action = TodoNewMainFragmentDirections.actionTodoNewMainFragmentToTodoSettingFragment()
            findNavController().navigate(action)
        }
    }

    fun setMainTodoCount(list: List<TodoEntity>) {
        val todayCount = list.count { todoEntity -> todoEntity.date == getStringDate(0) }
        var totalCount = 0
        for (i in 0 until 7) {
            totalCount += list.count { todoEntity -> todoEntity.date == getStringDate(i) }
        }

        binding.mainFmTvTodayCount.text = "오늘 할 일 $todayCount 개"
        binding.mainFmTvTotalCount.text = "남은 할 일 $totalCount 개"

    }

    fun openTodoDetail(todoId: Int) {
        val action =
            TodoNewMainFragmentDirections.actionTodoNewMainFragmentToTodoNewDetailFragment(todoId)
        findNavController().navigate(action)
    }
}
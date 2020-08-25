package com.acacia.simpletodo.todo.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.acacia.simpletodo.R
import com.acacia.simpletodo.databinding.FragmentTodoNewMainBinding
import com.acacia.simpletodo.todo.list.*
import com.acacia.simpletodo.viewmodel.TodoMainViewModel
import kotlinx.android.synthetic.main.fragment_todo_new_main.*

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

        test_btn_add.setOnClickListener {
            openTodoDetail(-1)
        }

        viewModel.listNumber.observe(viewLifecycleOwner, Observer {
            val tr = activity?.supportFragmentManager?.beginTransaction()
            tr?.replace(R.id.test_frame, TodoListFragment.newInstance(it))
            tr?.commitAllowingStateLoss()
        })

    }

    fun openTodoDetail(todoId: Int) {
        val action = TodoNewMainFragmentDirections.actionTodoNewMainFragmentToTodoDetailFragment(todoId)
        findNavController().navigate(action)
    }
}
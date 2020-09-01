package com.acacia.simpletodo.todo.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acacia.simpletodo.R
import com.acacia.simpletodo.TodoApplication
import com.acacia.simpletodo.databinding.FragmentTodoListBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.todo.main.TodoMainFragment
import com.acacia.simpletodo.todo.main.TodoNewMainFragment
import com.acacia.simpletodo.viewmodel.TodoViewModel
import javax.inject.Inject

class TodoListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoListBinding

    private lateinit var listAdapter: TodoAdapter

    private val viewModel by viewModels<TodoViewModel> { viewModelFactory }

    companion object {
        fun newInstance(index: Int): TodoListFragment {
            val fm = TodoListFragment()
            val args = Bundle()
            args.putInt(TodoListFragment::class.simpleName, index)
            fm.arguments = args
            return fm
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.instance.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val vm = binding.viewmodel
        if (vm != null) {
            listAdapter = TodoAdapter(vm)
            binding.todoListFmRvTodoList.setHasFixedSize(true)
            binding.todoListFmRvTodoList.adapter = listAdapter
        }
        MySimpleSwipeHelper(requireContext(), binding.todoListFmRvTodoList, 280) {
            val id = listAdapter.getTodoId(it)
            viewModel.deleteTodod(id)
        }

        arguments?.let {
            val index = it.getInt(TodoListFragment::class.java.simpleName)
            viewModel.getTodoList(index)
        }

        viewModel.todoId.observe(viewLifecycleOwner, Observer { todoId ->
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)?.let {
                for (fragment in it.childFragmentManager.fragments) {
                    if (fragment is TodoMainFragment) {
                        fragment.openTodoDetail(todoId)
                    }
                    if (fragment is TodoNewMainFragment) {
                        fragment.openTodoDetail(todoId)
                    }
                }
            }
        })

        viewModel.mainList.observe(viewLifecycleOwner, Observer { list ->
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)?.let {
                for (fragment in it.childFragmentManager.fragments) {
                    if (fragment is TodoNewMainFragment) {
                        fragment.setMainTodoCount(list)
                    }
                }
            }
        })
    }

}
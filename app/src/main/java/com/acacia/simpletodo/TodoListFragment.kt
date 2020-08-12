package com.acacia.simpletodo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acacia.simpletodo.databinding.FragmentTodoListBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.viewmodel.TodoViewModel
import javax.inject.Inject

class TodoListFragment : Fragment() {

    private val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as TodoApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoListBinding

    private lateinit var listAdapter: TodoAdapter

    private val viewModel by viewModels<TodoViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
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

        Log.d("ung", "list fm onActivityCreated")
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val vm = binding.viewmodel
        if (vm != null) {
            listAdapter = TodoAdapter(vm)
            binding.todoListFmRvTodoList.setHasFixedSize(true)
            binding.todoListFmRvTodoList.adapter = listAdapter
        }
        viewModel.getTodoList()

        viewModel.todoId.observe(viewLifecycleOwner, Observer { todoId ->
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)?.let {
                for (fragment in it.childFragmentManager.fragments) {
                    if (fragment is TodoMainFragment) {
                        fragment.openTodoDetail(todoId)
                    }
                }
            }
        })
    }

}
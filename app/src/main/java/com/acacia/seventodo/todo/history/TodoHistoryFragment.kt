package com.acacia.seventodo.todo.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.acacia.seventodo.TodoApplication
import com.acacia.seventodo.databinding.FragmentTodoHistoryBinding
import com.acacia.seventodo.viewmodel.TodoHistoryViewModel
import javax.inject.Inject

class TodoHistoryFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoHistoryBinding

    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel by viewModels<TodoHistoryViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.instance.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val vm = binding.viewmodel
        if (vm != null) {
            historyAdapter = HistoryAdapter(vm)
            binding.historyFmRv.setHasFixedSize(true)
            binding.historyFmRv.adapter = historyAdapter
        }

        viewModel.loadHistory()

    }
}
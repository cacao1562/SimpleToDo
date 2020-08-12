package com.acacia.simpletodo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.acacia.simpletodo.databinding.FragmentTodoDetailBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import com.acacia.simpletodo.viewmodel.TodoViewModel
import javax.inject.Inject

class TodoDetailFragment : Fragment(), DatePickerDialog.OnDateSelectedListener {

    private val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as TodoApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTodoDetailBinding

    private val viewModel by viewModels<TodoDetailViewModel> { viewModelFactory }

    private val args: TodoDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.loadTodo(args.todoId)

        binding.todoDetailBtnDatePicker.setOnClickListener {
            val dialog = DatePickerDialog(null, this)
            dialog.show(activity?.supportFragmentManager!!, dialog.tag)
        }
    }

    override fun onResult(date: DatePickerDialog.DateSelected) {
        Log.d("ung", date.toString())
    }
}
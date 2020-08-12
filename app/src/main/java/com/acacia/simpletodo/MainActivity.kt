package com.acacia.simpletodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.acacia.simpletodo.databinding.ActivityMainBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.viewmodel.TodoViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (this.application as TodoApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: TodoViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var listAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[TodoViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

//        viewModel.items.observe(this, Observer {
//
//        })

        val vm = binding.viewmodel
        if (vm != null) {
            listAdapter = TodoAdapter(vm)
            binding.rvTodoList.adapter = listAdapter
        }

        viewModel.getTodoList()
    }
}
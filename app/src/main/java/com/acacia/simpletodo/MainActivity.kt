package com.acacia.simpletodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.acacia.simpletodo.databinding.ActivityMainBinding
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.todo.detail.TodoDetailFragment
import com.acacia.simpletodo.todo.detail.TodoNewDetailFragment
import com.acacia.simpletodo.viewmodel.TodoViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: TodoViewModel

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TodoApplication.instance.appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[TodoViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.let {
            for (fragment in it.childFragmentManager.fragments) {
                if (fragment is TodoNewDetailFragment && !fragment.onBackPressed()) {
                    return
                }else {
                    super.onBackPressed()
                }
            }
        }
    }
}
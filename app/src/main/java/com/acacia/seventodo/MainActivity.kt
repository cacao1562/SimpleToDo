package com.acacia.seventodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acacia.seventodo.databinding.ActivityMainBinding
import com.acacia.seventodo.todo.detail.TodoNewDetailFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
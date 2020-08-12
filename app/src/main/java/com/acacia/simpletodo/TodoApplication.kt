package com.acacia.simpletodo

import android.app.Application
import com.acacia.simpletodo.di.DaggerTodoComponent
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.di.TodoModule

class TodoApplication : Application() {

    val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerTodoComponent
            .builder()
            .todoModule(TodoModule(this))
            .build()

    }

    override fun onCreate() {
        super.onCreate()
        injectMembers()
    }

    private fun injectMembers() = appComponent.inject(this)
}
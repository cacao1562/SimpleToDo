package com.acacia.simpletodo

import android.app.Application
import com.acacia.simpletodo.di.DaggerTodoComponent
import com.acacia.simpletodo.di.TodoComponent
import com.acacia.simpletodo.di.TodoModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TodoApplication : Application() {


    companion object {
        lateinit var instance: TodoApplication private set
    }

    val appComponent: TodoComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerTodoComponent
            .builder()
            .todoModule(TodoModule(this))
            .build()

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        injectMembers()
    }

    private fun injectMembers() = appComponent.inject(this)

//    @Inject
//    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
//
//    override fun androidInjector(): AndroidInjector<Any> {
//        TODO("Not yet implemented")
//    }
}
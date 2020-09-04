package com.acacia.seventodo

import android.app.Application
import com.acacia.seventodo.di.DaggerTodoComponent
import com.acacia.seventodo.di.TodoComponent
import com.acacia.seventodo.di.TodoModule

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
package com.acacia.simpletodo.di

import com.acacia.simpletodo.*
import com.acacia.simpletodo.di.viewmodel.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TodoModule::class, ViewModelModule::class])
interface TodoComponent {
    fun inject(todoApplication: TodoApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(todoMainFragment: TodoMainFragment)
    fun inject(todoListFragment: TodoListFragment)
    fun inject(todoDetailFragment: TodoDetailFragment)

}
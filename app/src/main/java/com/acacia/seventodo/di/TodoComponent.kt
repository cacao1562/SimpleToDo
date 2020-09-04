package com.acacia.seventodo.di

import com.acacia.seventodo.*
import com.acacia.seventodo.di.viewmodel.ViewModelModule
import com.acacia.seventodo.todo.detail.TodoNewDetailFragment
import com.acacia.seventodo.todo.list.TodoListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TodoModule::class, ViewModelModule::class])
interface TodoComponent {
    fun inject(todoApplication: TodoApplication)
    fun inject(todoListFragment: TodoListFragment)
    fun inject(todoNewDetailFragment: TodoNewDetailFragment)
    fun inject(notifyActionReceiver: NotifyActionReceiver)

}
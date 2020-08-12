package com.acacia.simpletodo.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.acacia.simpletodo.viewmodel.TodoDateViewModel
import com.acacia.simpletodo.viewmodel.TodoDetailViewModel
import com.acacia.simpletodo.viewmodel.TodoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TodoViewModel::class)
    abstract fun bindsTodoViewModel(todoViewModel: TodoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TodoDetailViewModel::class)
    abstract fun bindsTodoDetailViewModel(todoDetailViewModel: TodoDetailViewModel): ViewModel

}
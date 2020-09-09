package com.acacia.seventodo.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.acacia.seventodo.viewmodel.TodoDetailViewModel
import com.acacia.seventodo.viewmodel.TodoHistoryViewModel
import com.acacia.seventodo.viewmodel.TodoViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(TodoHistoryViewModel::class)
    abstract fun bindsTodoHistoryViewModel(todoHistoryViewModel: TodoHistoryViewModel): ViewModel

}
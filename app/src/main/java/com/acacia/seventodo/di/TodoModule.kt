package com.acacia.seventodo.di

import android.content.Context
import androidx.room.Room
import com.acacia.seventodo.TodoApplication
import com.acacia.seventodo.database.TodoDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TodoModule (private val application: TodoApplication) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application

    @Singleton
    @Provides
    fun providesRoomDatabase(): TodoDatabase {
        return Room.databaseBuilder(application, TodoDatabase::class.java, "library_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTodoDAO(todoDatabase: TodoDatabase) = todoDatabase.getTodoDAO()
}
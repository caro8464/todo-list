package com.mad.todolist.todo

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Task::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
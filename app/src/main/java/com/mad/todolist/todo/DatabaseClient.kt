package com.mad.todolist.todo

import android.arch.persistence.room.Room
import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseClient private constructor(private val mCtx: Context) {

    //our app database object
    val appDatabase: AppDatabase = Room.databaseBuilder(mCtx, AppDatabase::class.java, "TestToDo")
        .fallbackToDestructiveMigration()
        .build()

    init {
        //creating the app database with Room database builder
        //MyToDos is the name of the database
    }

    companion object {
        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance as DatabaseClient
        }
    }
}
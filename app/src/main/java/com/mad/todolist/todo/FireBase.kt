package com.mad.todolist.todo

import android.content.ContentValues.TAG
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.database.*

class BasicFireBase(context: Context, email: String) {
    private var email: String = email
    private var appContext: Context = context

    fun clearAllDB() {
        class ClearAllTasks : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg voids: Void): Boolean {
                DatabaseClient.getInstance(appContext)
                    .appDatabase
                    .clearAllTables()
                return true
            }
        }

        val clear = ClearAllTasks()
        clear.execute()
    }

    fun clearAllFB(): DatabaseReference {
        val fbDatabase = FirebaseDatabase.getInstance().reference
        fbDatabase.child(Statics.FIREBASE_TASK + email).removeValue()
        return fbDatabase
    }

    fun writeAllFB(tasks: List<Task>) {

        val fbDatabase = clearAllFB()
        tasks.forEach {
            val newItem = fbDatabase.child(Statics.FIREBASE_TASK + email).push()
            it.firebaseId = newItem.key
            newItem.setValue(it)
            updateTaskInDB(it)
        }
    }

    fun updateTaskInFB(task: Task) {
        val fbDatabase = FirebaseDatabase.getInstance().reference
        val newItem = if (task.firebaseId != null && task.firebaseId!!.isNotEmpty()) {
            fbDatabase.child(Statics.FIREBASE_TASK + email).child(task.firebaseId!!).push()
        } else {
            fbDatabase.child(Statics.FIREBASE_TASK + email).push()
        }
        task.firebaseId = newItem.key
        newItem.setValue(task)
        updateTaskInDB(task)
    }

    fun removeTaskInFB(task: Task) {
        val fbDatabase = FirebaseDatabase.getInstance().reference
        val taskReference = fbDatabase.child(Statics.FIREBASE_TASK + email).child(task.firebaseId.toString())
        taskReference.removeValue()
    }


    fun syncNow(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            val fbDatabase = FirebaseDatabase.getInstance().reference
            val listOfTasks = fbDatabase.child(Statics.FIREBASE_TASK + email)
            listOfTasks.orderByKey().addListenerForSingleValueEvent(singleListener)
        } else {
            writeAllFB(tasks)
        }
    }

    private fun updateTaskInDB(it: Task) {
        class UpdateTask : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                DatabaseClient.getInstance(appContext)
                    .appDatabase
                    .taskDao()
                    .update(it)
                return null
            }
        }
        UpdateTask().execute()
    }

    fun insertTaskInDB(it: Task) {
        class InsertTask : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                DatabaseClient.getInstance(appContext)
                    .appDatabase
                    .taskDao()
                    .insert(it)
                return null
            }
        }
        InsertTask().execute()
    }

    private var singleListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            for (task in p0.children) {
                val it = task.getValue(Task::class.java) as Task
                insertTaskInDB(it)
            }
        }
    }
}



package com.mad.todolist.todo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity
class Task : Serializable {

    /*
     * Getters and Setters
     * */

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "firebase_id")
    var firebaseId: String? = null

    @ColumnInfo(name = "task")
    var task: String? = null

    @ColumnInfo(name = "desc")
    var desc: String? = null

    @ColumnInfo(name = "favourite")
    var fav: Boolean = false

    @ColumnInfo(name = "finish_by")
    var finishBy: String? = null

    @ColumnInfo(name = "finished")
    var isFinished: Boolean = false

    /*
    @ColumnInfo(name = "contacts")
    var contacts: List<Int>? = null
    */
}
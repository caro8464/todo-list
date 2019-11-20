package com.mad.todolist.todo

import android.arch.persistence.room.*

@Dao
interface TaskDao {

    @get:Query("SELECT * FROM task ORDER BY finished desc, favourite desc, finish_by desc")
    val sortFavDate: List<Task>
    /*Reihenfolge:
        Eingeteilt in erledigt(oben) & nicht erledigt(unten)
        diese unterteilt nach Favourite ja(oben)/nein(unten) & Datum zukuenftiges Datum(oben)/vergangenes Datum(unten)
     */

    @get:Query("SELECT * FROM task ORDER BY finished desc, finish_by desc, favourite desc")
    val sortDateFav: List<Task>
    /*Reihenfolge:
        Eingeteilt in erledigt(oben) & nicht erledigt(unten)
        diese unterteilt nach Datum zukuenftiges Datum(oben)/vergangenes Datum(unten) & Favourite ja(oben)/nein(unten)
     */

    @Insert
    fun insert(task: Task)

    @Delete
    fun delete(task: Task)

    @Update
    fun update(task: Task)

}
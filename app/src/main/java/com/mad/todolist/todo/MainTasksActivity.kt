package com.mad.todolist.todo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.MAD.todolist.R
import java.util.*
import kotlin.concurrent.schedule


class MainTasksActivity : AppCompatActivity() {

    private lateinit var buttonAddTask: FloatingActionButton
    private lateinit var buttonUpdate: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var sortType: MenuItem
    private lateinit var allTasks: List<Task>
    private lateinit var email: String
    private lateinit var basicFB: BasicFireBase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val unsanitizedmail = (intent.getStringExtra("email").toString())
        val regex = Regex("[^a-zA-Z0-9]*")
        email = regex.replace(unsanitizedmail, "")
        basicFB = BasicFireBase(application, email)
        recyclerView = findViewById(R.id.recyclerview_tasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        buttonAddTask = findViewById(R.id.floating_button_add)
        buttonUpdate = findViewById(R.id.button_update)
        buttonUpdate.setOnClickListener {
            onOptionsItemSelected(sortType)
        }
        buttonAddTask.setOnClickListener {
            val intent = Intent(this@MainTasksActivity, AddTaskActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
        getTasks()
        val intentFromLogin:Boolean? = intent.getBooleanExtra(("logged_in"), false)
        Timer().schedule(800) {
            if (intentFromLogin!! && ::allTasks.isInitialized) {
                basicFB.syncNow(allTasks)
                Timer().schedule(1000) {
                    getTasks()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.action_bar, menu)
        sortType = menu.findItem(R.id.fav_date)
        return true
    }

    // function to clear all tables in DB
    private fun clearAllDB() {
        class ClearAllTasks : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg voids: Void): Boolean {
                DatabaseClient.getInstance(applicationContext)
                    .appDatabase
                    .clearAllTables()
                return true
            }
        }

        val clear = ClearAllTasks()
        clear.execute()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        return when (item.itemId) {
            R.id.fav_date -> {
                getTasks("favDate")
                sortType = item
                true
            }
            R.id.date_fav -> {
                getTasks("dateFav")
                sortType = item
                true
            }
            R.id.write_to_fb -> {
                basicFB.writeAllFB(allTasks)
                true
            }
            R.id.clear_fb -> {
                basicFB.clearAllFB()
                true
            }
            R.id.clear_db -> {
                clearAllDB()
                getTasks("favDate")
                true
            }
            R.id.sync -> {
                basicFB.syncNow(allTasks)
                getTasks("favDate")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getTasks(sort: String = "favDate") {
        class GetTasks : AsyncTask<Void, Void, List<Task>>() {

            override fun doInBackground(vararg voids: Void): List<Task> {
                return when (sort) {
                    "favDate" -> {
                        DatabaseClient.getInstance(applicationContext)
                            .appDatabase
                            .taskDao()
                            .sortFavDate
                    }
                    "dateFav" -> {
                        DatabaseClient.getInstance(applicationContext)
                            .appDatabase
                            .taskDao()
                            .sortDateFav
                    }
                    else -> {
                        DatabaseClient.getInstance(applicationContext)
                            .appDatabase
                            .taskDao()
                            .sortFavDate
                    }
                }
            }


            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                allTasks = tasks
                val adapter = TasksAdapter(this@MainTasksActivity, tasks, email)
                recyclerView.adapter = adapter
            }
        }

        val gt = GetTasks()
        gt.execute()
    }
}
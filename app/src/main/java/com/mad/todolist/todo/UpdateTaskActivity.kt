package com.mad.todolist.todo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.MAD.todolist.R
import com.mad.todolist.data.dtPicker.dtPicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var viewTextFinishBy: TextView
    private lateinit var fav: CheckBox
    private lateinit var checkBoxFinished: CheckBox
    lateinit var date: LocalDateTime
    private var cal = Calendar.getInstance()
    private var dtpicker: dtPicker = dtPicker()
    private lateinit var email:String
    private lateinit var basicFireBase:BasicFireBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)

        editTextTask = findViewById(R.id.editTextTask)
        editTextDesc = findViewById(R.id.editTextDesc)
        viewTextFinishBy = findViewById(R.id.viewTextFinishBy)
        fav = findViewById(R.id.checkedFavourite)
        checkBoxFinished = findViewById(R.id.checkBoxFinished)

        val task = intent.getSerializableExtra("task") as Task

        loadTask(task)
        val finishByAsString: String = viewTextFinishBy.text.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        this.date = if (finishByAsString != "") {
            LocalDateTime.parse(finishByAsString, formatter)
        } else {
            LocalDateTime.now()
        }

        findViewById<View>(R.id.buttonDatePicker).setOnClickListener {
            // sets the viewTextFinishBy.text to date and time picked
            dtpicker.datePicker(this@UpdateTaskActivity, cal, date, viewTextFinishBy)
        }

        findViewById<View>(R.id.button_update).setOnClickListener {
            //Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
            updateTask(task)
        }

        findViewById<View>(R.id.button_delete).setOnClickListener {
            val builder = AlertDialog.Builder(this@UpdateTaskActivity)
            builder.setTitle("Are you sure?")
            builder.setPositiveButton("Yes") { dialogInterface, i -> deleteTask(task) }
            builder.setNegativeButton("No") { dialogInterface, i -> }

            val ad = builder.create()
            ad.show()
        }
        email = intent.getStringExtra("email")
        basicFireBase = BasicFireBase(applicationContext, email)
    }

    private fun loadTask(task: Task) {
        editTextTask.setText(task.task)
        editTextDesc.setText(task.desc)
        viewTextFinishBy.text = task.finishBy
        fav.isChecked = task.fav
        checkBoxFinished.isChecked = task.isFinished
    }

    /**
     *
     */
    private fun updateTask(task: Task) {
        val sTask = editTextTask.text.toString().trim { it <= ' ' }
        val sDesc = editTextDesc.text.toString().trim { it <= ' ' }
        val sFinishBy = viewTextFinishBy.text.toString().trim { it <= ' ' }
        val sFav = fav.isChecked

        if (sTask.isEmpty()) {
            editTextTask.error = "Task required"
            editTextTask.requestFocus()
            return
        }

        if (sDesc.isEmpty()) {
            editTextDesc.error = "Desc required"
            editTextDesc.requestFocus()
            return
        }

        if (sFinishBy.isEmpty()) {
            viewTextFinishBy.error = "Finish by required"
            viewTextFinishBy.requestFocus()
            return
        }
        task.task = sTask
        task.desc = sDesc
        task.finishBy = sFinishBy
        task.fav = sFav
        task.isFinished = checkBoxFinished.isChecked

        class UpdateTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                DatabaseClient.getInstance(applicationContext)
                    .appDatabase
                    .taskDao()
                    .update(task)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                basicFireBase.updateTaskInFB(task)
                Toast.makeText(applicationContext, "Updated", Toast.LENGTH_LONG).show()
                finish()

                val intent = Intent(this@UpdateTaskActivity, MainTasksActivity::class.java)
                intent.putExtra("email", email)

                startActivity(intent)
            }
        }

        val ut = UpdateTask()
        ut.execute()
    }


    private fun deleteTask(task: Task) {
        basicFireBase.removeTaskInFB(task)
        class DeleteTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                DatabaseClient.getInstance(applicationContext).appDatabase
                    .taskDao()
                    .delete(task)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_LONG).show()
                finish()
                val email = intent.getStringExtra("email")
                val intent = Intent(this@UpdateTaskActivity, MainTasksActivity::class.java)
                intent.putExtra("email", email)

                startActivity(intent)
            }
        }

        val dt = DeleteTask()
        dt.execute()

    }

}
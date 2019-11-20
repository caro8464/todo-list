package com.mad.todolist.todo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.MAD.todolist.R
import com.mad.todolist.data.dtPicker.dtPicker
import java.time.LocalDateTime
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var viewTextFinishBy: TextView
    private lateinit var fav: CheckBox
    private var date = LocalDateTime.now()
    private var cal = Calendar.getInstance()
    private var dtpicker: dtPicker = dtPicker()
    private lateinit var email:String
    private lateinit var basicFireBase:BasicFireBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        editTextTask = findViewById(R.id.editTextTask)
        editTextDesc = findViewById(R.id.editTextDesc)
        viewTextFinishBy = findViewById(R.id.viewTextFinishBy)
        fav = this.findViewById(R.id.checkedFavourite)



        findViewById<View>(R.id.buttonDatePicker).setOnClickListener {
            dtpicker.datePicker(this@AddTaskActivity, cal, date, viewTextFinishBy)
        }
        findViewById<View>(R.id.button_save).setOnClickListener { saveTask() }

        findViewById<View>(R.id.button_test).setOnClickListener {
            val intent = Intent(this@AddTaskActivity, ContactsActivity::class.java)
            startActivity(intent)
        }
        email = intent.getStringExtra("email")
        basicFireBase = BasicFireBase(applicationContext, email)
    }

    private fun saveTask() {
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
        val task = Task()
        task.task = sTask
        task.desc = sDesc
        task.finishBy = sFinishBy
        task.fav = sFav
        task.isFinished = false
        class SaveTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                //adding to database
                DatabaseClient.getInstance(applicationContext).appDatabase
                    .taskDao()
                    .insert(task)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                basicFireBase.updateTaskInFB(task)

                finish()

                val intent = Intent(this@AddTaskActivity, MainTasksActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)

                Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }
}

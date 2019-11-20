package com.mad.todolist.todo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.MAD.todolist.R

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        findViewById<View>(R.id.button_back).setOnClickListener {
            val intent = Intent(this@ContactsActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }
}
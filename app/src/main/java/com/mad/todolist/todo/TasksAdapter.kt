package com.mad.todolist.todo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.MAD.todolist.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TasksAdapter(private val mCtx: Context, private val taskList: List<Task>, private val email: String) :
    RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_tasks, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val t = taskList[position]
        holder.textViewTask.text = "Name: " + t.task
        holder.textViewDesc.text = "Beschreibung: " + t.desc
        holder.textViewFinishBy.text = t.finishBy.toString()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val currentDate = formatter.format(LocalDateTime.now())

        if (t.isFinished) {
            holder.textViewStatus.setBackgroundColor(Color.parseColor("#579937"))
            holder.textViewStatus.text = "Erledigt"
        } else {
            holder.textViewStatus.setBackgroundColor(Color.parseColor("#FF0000"))
            if (t.finishBy.toString() <= currentDate) holder.overdue.visibility = View.VISIBLE
            holder.textViewStatus.text = "Nicht erledigt!"
        }

        holder.checkedFavourite.isChecked = t.fav
        holder.checkBoxFinished.isChecked = t.isFinished

        class UpdateTask : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                DatabaseClient.getInstance(holder.itemView.context.applicationContext)
                    .appDatabase
                    .taskDao()
                    .update(t)
                return null
            }
        }

        holder.checkedFavourite.setOnClickListener {
            t.fav = !t.fav
            UpdateTask().execute()
        }

        holder.checkBoxFinished.setOnClickListener {
            t.isFinished = !t.isFinished
            UpdateTask().execute()
        }

    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textViewStatus: TextView
        var textViewTask: TextView
        var textViewDesc: TextView
        var textViewFinishBy: TextView
        var checkedFavourite: CheckBox
        var checkBoxFinished: CheckBox
        var overdue: TextView

        init {
            textViewStatus = itemView.findViewById(R.id.textViewStatus)
            textViewTask = itemView.findViewById(R.id.textViewTask)
            textViewDesc = itemView.findViewById(R.id.textViewDesc)
            textViewFinishBy = itemView.findViewById(R.id.textViewFinishBy)
            /*textViewFav = itemView.findViewById(R.id.textViewFav)*/
            checkedFavourite = itemView.findViewById(R.id.checkedFavourite)
            checkBoxFinished = itemView.findViewById(R.id.checkBoxFinished)
            overdue = itemView.findViewById(R.id.overdue)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val task = taskList[adapterPosition]
            val intent = Intent(mCtx, UpdateTaskActivity::class.java)
            intent.putExtra("task", task)
            intent.putExtra("email", email)
            mCtx.startActivity(intent)
        }
    }
}
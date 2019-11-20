package com.mad.todolist.data.dtPicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.LocalDateTime

import java.util.*

class dtPicker {
    private var date: LocalDateTime = LocalDateTime.now()
    private var date_time: CharSequence = ""
    private var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun datePicker(context: Context, cal: Calendar, date: LocalDateTime, fieldToChange: TextView) {
        this.date = date
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                timePicker(context, cal, date, fieldToChange)
            }, date.year, date.monthValue - 1, date.dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun timePicker(context: Context, cal: Calendar, date: LocalDateTime, fieldToChange: TextView) {

        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                date_time = sdf.format(cal.time)
                fieldToChange.text = date_time
            }, date.hour, date.minute, true
        )
        timePickerDialog.show()
    }
}
package com.ciklum.insulinapp.Activities.MedicineReminder

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ciklum.insulinapp.R
import android.widget.TimePicker
import android.text.format.DateFormat.is24HourFormat
import android.app.TimePickerDialog
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.Button
import android.widget.Toast
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Context.ALARM_SERVICE
import android.provider.AlarmClock


private var myHour:String?=null
private var myMinute:String?=null
private var myTime:String?=null

private var alarmMgr: AlarmManager? = null
private var alarmIntent: PendingIntent? = null

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        myHour=hourOfDay.toString().trim()
        myMinute=minute.toString().trim()

        myTime=myHour + ":" +myMinute

        Toast.makeText(activity?.applicationContext,"The time you chose is " + myTime,Toast.LENGTH_SHORT).show()
    }
}

class ReminderActivity : AppCompatActivity() {

    private var pickTimeBtn:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        pickTimeBtn=findViewById(R.id.pickTimeBtn)


        pickTimeBtn?.setOnClickListener()
        {
            showTimePickerDialog()
        }
    }

    fun showTimePickerDialog() {
        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "timePicker")
    }
}

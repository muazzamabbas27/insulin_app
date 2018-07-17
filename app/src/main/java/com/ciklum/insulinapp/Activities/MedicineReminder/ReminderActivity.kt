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
import android.os.Build
import android.provider.AlarmClock
import android.support.annotation.RequiresApi
import android.widget.TextView
import com.ciklum.insulinapp.Models.AlarmReceiver
import com.ciklum.insulinapp.R.layout.activity_reminder
import kotlinx.android.synthetic.main.activity_reminder.*


private var myHour:String?=null
private var myMinute:String?=null
private var myTime:String?=null
private lateinit var myIntent:Intent

private lateinit var alarmMgr: AlarmManager
lateinit var p1:PendingIntent

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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user


        alarmMgr= activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        myHour=hourOfDay.toString().trim()
        myMinute=minute.toString().trim()

        myTime=myHour + ":" +myMinute

        myIntent=Intent(activity?.applicationContext,AlarmReceiver::class.java)
        var calendar:Calendar= Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, myHour!!.toInt())
        calendar.set(Calendar.MINUTE, myMinute!!.toInt())
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)

        var strHour:String

        if(myHour!!.toInt()>12)
        {
            myHour=(myHour!!.toInt()-12).toString()
        }

        if(myMinute!!.toInt()<10)
        {
            myMinute="0$myMinute"
        }

        activity!!.timeTextView.setText("Alarm set for $myHour:$myMinute")
        Toast.makeText(activity?.applicationContext,"Alarm set for $myHour:$myMinute",Toast.LENGTH_SHORT).show()
        myIntent.putExtra("extra","on")
        p1= PendingIntent.getBroadcast(activity?.applicationContext,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,p1)
    }
}

class ReminderActivity : AppCompatActivity() {

    private var pickTimeBtn:Button?=null
    private lateinit var timeTextView:TextView
    private lateinit var stopAlarmBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        pickTimeBtn=findViewById(R.id.pickTimeBtn)
        timeTextView=findViewById(R.id.timeTextView)
        stopAlarmBtn=findViewById(R.id.stopAlarmBtn)

        pickTimeBtn?.setOnClickListener()
        {
            showTimePickerDialog()
        }
    }

    fun showTimePickerDialog() {

        stopAlarmBtn.setOnClickListener()
        {
            p1= PendingIntent.getBroadcast(applicationContext,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMgr.cancel(p1)
            myIntent.putExtra("extra","off")
            sendBroadcast(myIntent)
        }

        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "timePicker")
    }
}

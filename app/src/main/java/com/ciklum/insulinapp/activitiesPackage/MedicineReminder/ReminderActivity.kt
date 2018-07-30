package com.ciklum.insulinapp.activitiesPackage.MedicineReminder

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ciklum.insulinapp.R
import android.widget.TimePicker
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
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.TextView
import com.ciklum.insulinapp.Models.AlarmReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_reminder.*


private var myHour:String?=null
private var myMinute:String?=null
private var myTime:String?=null
private lateinit var myIntent:Intent

private lateinit var alarmMgr: AlarmManager
lateinit var p1:PendingIntent

private var mFirebaseUser: FirebaseUser?=null
private var mAuth: FirebaseAuth?=null
private lateinit var mFirebaseDatabase: FirebaseDatabase
private lateinit var mFirebaseDatabaseReference: DatabaseReference

private lateinit var currentUserEmailID:String

private lateinit var someTime:String

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


        alarmMgr.setRepeating(AlarmManager.RTC,calendar.timeInMillis,AlarmManager.INTERVAL_DAY,p1)

        /*mAuth = FirebaseAuth.getInstance()

        mFirebaseUser = mAuth?.currentUser
        currentUserEmailID = mFirebaseUser?.email!!


        mFirebaseDatabase = FirebaseDatabase.getInstance()


        var mAlarmDB= AlarmDB(currentUserEmailID,"$myHour:$myMinute",p1, alarmMgr)
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Alarm Data")
        mFirebaseDatabaseReference.push().setValue(mAlarmDB)*/
    }
}

class ReminderActivity : AppCompatActivity() {

    private var pickTimeBtn:Button?=null
    private lateinit var timeTextView:TextView
    private lateinit var cancelAlarmBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        pickTimeBtn = findViewById(R.id.pickTimeBtn)
        timeTextView = findViewById(R.id.timeTextView)
        cancelAlarmBtn = findViewById(R.id.cancelAlarmBtn)

        mAuth = FirebaseAuth.getInstance()

        mFirebaseUser = mAuth?.currentUser
        currentUserEmailID = mFirebaseUser?.email!!


        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Alarm Data")

        var rootRef7 = FirebaseDatabase.getInstance().getReference("Alarm Data")
        rootRef7.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isCanceled: Boolean = false
                for (data: DataSnapshot in dataSnapshot.children) {

                    if (isCanceled == false) {
                        var oldEmailID = data.child("emailID").getValue().toString()
                        if (oldEmailID.equals(currentUserEmailID))
                        {
                            someTime=data.child("mTime").getValue().toString()
                            timeTextView.setText("Alarm set for " + someTime)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


        pickTimeBtn?.setOnClickListener()
        {
            showTimePickerDialog()
        }

        cancelAlarmBtn?.setOnClickListener()
        {

            //Code doesn't work
            try {
                val notificationIntent = Intent(applicationContext, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(applicationContext, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                timeTextView.setText("Alarms canceled")
            } catch (e: Exception) {
                e.printStackTrace()
            }


            var rootRef6 = FirebaseDatabase.getInstance().getReference("Alarm Data")
            rootRef6.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var isCanceled: Boolean = false
                    for (data: DataSnapshot in dataSnapshot.children) {

                        if (isCanceled == false) {
                            var oldEmailID = data.child("emailID").getValue().toString()
                            if (oldEmailID.equals(currentUserEmailID)) run {
                                var somePendingIntent= data.child("myPendingIntent").getValue() as PendingIntent
                                var someAlarmManager=data.child("myAlarmManager").getValue() as AlarmManager
                                someAlarmManager.cancel(somePendingIntent)
                                Toast.makeText(applicationContext,"All alarms canceled!",Toast.LENGTH_SHORT).show()
                                timeTextView.setText("Alarms canceled")
                            }
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    fun showTimePickerDialog() {

        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "timePicker")
    }
}

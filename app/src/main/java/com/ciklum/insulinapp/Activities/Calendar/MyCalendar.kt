package com.ciklum.insulinapp.Activities.Calendar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import com.ciklum.insulinapp.Activities.Notes.NotesActivity
import com.ciklum.insulinapp.R
import kotlinx.android.synthetic.main.activity_my_calendar.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MyCalendar : AppCompatActivity() {

    private lateinit var mCalendarView:CalendarView
    private lateinit var makeNotesBtn:Button

    var myDate:String=""
    var myMonth:String=""
    var myYear:String=""
    var isDateChanged=false

    var tempDate:String?=null
    var tempMonth:String?=null
    var tempYear:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_calendar)

        mCalendarView=findViewById(R.id.calendarView)
        makeNotesBtn=findViewById(R.id.makeNotesBtn)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            //Months are indexed from 0. So, 0 means january, 1 means February, 2 means march etc.
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year

            myDate=dayOfMonth.toString()
            var someMonth=month+1
            myMonth=someMonth.toString()
            myYear=year.toString()

            isDateChanged=true
        }


        makeNotesBtn.setOnClickListener()
        {

            if(isDateChanged==false)
            {
                val date = Calendar.getInstance().time

                val sdf1 = SimpleDateFormat("dd")
                tempDate=sdf1.format(date)

                val sdf2 = SimpleDateFormat("M")
                tempMonth=sdf2.format(date)

                val sdf3 = SimpleDateFormat("yyyy")
                tempYear=sdf3.format(date)

                val i=Intent(this,NotesActivity::class.java)
                i.putExtra("date",tempDate)
                i.putExtra("month",tempMonth)
                i.putExtra("year",tempYear)
                startActivity(i)
                finish()
            }

            else
            {
                val i=Intent(this,NotesActivity::class.java)
                i.putExtra("date",myDate)
                i.putExtra("month",myMonth)
                i.putExtra("year",myYear)
                startActivity(i)
                finish()
            }
        }


    }
}

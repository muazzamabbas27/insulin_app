package com.ciklum.insulinapp.Activities.Calendar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import com.ciklum.insulinapp.Activities.Notes.NotesActivity
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_calendar.*
import java.text.SimpleDateFormat
import java.util.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Adapters.adapter2
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import kotlin.collections.ArrayList


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


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference
    private lateinit var mFirebaseUser:FirebaseUser

    private lateinit var rootRef: DatabaseReference
    private lateinit var rootRef10:DatabaseReference

    private lateinit var currentUserEmailID:String

    private lateinit var currentCompleteDate:String

    private lateinit var eventDB:String
    private lateinit var currentBGDB:String
    private lateinit var targetBGDB:String
    private lateinit var amountOfCHODB:String
    private lateinit var disposedCHODB:String
    private lateinit var correctionFactorDB:String
    private lateinit var insulinRecommendationDB:String
    private lateinit var typeOfInsulinDB:String

    private lateinit var basalWeightDB:String
    private lateinit var basalTDIDB:String
    private lateinit var basalInsulinRecommendationDB:String

    private lateinit var RV1: RecyclerView
    private lateinit var RV3:RecyclerView
    var mBolusBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBasalBGList:ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var datesList:ArrayList<String> = ArrayList(1000)
    var datesList2:ArrayList<String> = ArrayList(1000)


    private var someMonth:Int=-1
    private lateinit var monthString: String
    private lateinit var clickedDate:String

    private lateinit var bolusTitleTextView: TextView
    private lateinit var basalTitleTextview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_calendar)

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Bolus BG Data")
        rootRef=FirebaseDatabase.getInstance().getReference("Bolus BG Data")
        rootRef10=FirebaseDatabase.getInstance().getReference("Basal BG Data")

        mFirebaseUser= mAuth.currentUser!!

        currentUserEmailID= mFirebaseUser.email!!

        mCalendarView=findViewById(R.id.calendarView)
        makeNotesBtn=findViewById(R.id.makeNotesBtn)
        RV1=findViewById(R.id.RV1)
        RV3=findViewById(R.id.RV3)

        getCurrentCalendarDate()

        bolusTitleTextView=findViewById(R.id.bolusTitleTextView)
        basalTitleTextview=findViewById(R.id.basalTitleTextView)

        bolusTitleTextView.setText("")
        basalTitleTextview.setText("")

        Toast.makeText(applicationContext,"Loading Blood Glucose data",Toast.LENGTH_SHORT).show()

        if (mBolusBGList.size > 0)
        {
            RV1.removeAllViews()
            mBolusBGList.clear()
        }

        if(mBasalBGList.size>0)
        {
            RV3.removeAllViews()
            mBasalBGList.clear()
        }

        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren())
                {
                    var emailIDDB=data.child("emailID").getValue().toString().trim()

                    if(emailIDDB.equals(currentUserEmailID))
                    {
                        var dateDB=data.child("calendarTime").getValue().toString().trim()
                        datesList.add(dateDB)
                        if(dateDB.equals(currentCompleteDate))
                        {
                            eventDB=data.child("beforeEvent").getValue().toString().trim()
                            currentBGDB=data.child("currentBGLevel").getValue().toString().trim()
                            targetBGDB=data.child("targetBGLevel").getValue().toString().trim()
                            amountOfCHODB=data.child("totalCHO").getValue().toString().trim()
                            disposedCHODB=data.child("amountDisposedByInsulin").getValue().toString().trim()
                            correctionFactorDB=data.child("correctionFactor").getValue().toString().trim()
                            insulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                            typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()
                            var mBGRecyclerView: BolusBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                            mBolusBGList.add(mBGRecyclerView)
                        }
                    }
                }

                if (mBolusBGList.size > 0) {
                    bolusTitleTextView.setText("Bolus Insulin")
                    RV1.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV1.setAdapter(adapter1(mBolusBGList))
                    //Toast.makeText(applicationContext,"Data loaded, contains " + mBolusBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext,"No Bolus Blood Glucose data saved for this day",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Could not read from database, please check your internet connection",Toast.LENGTH_LONG).show()
            }
        })


        rootRef10.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren())
                {
                    var emailIDDB=data.child("emailID").getValue().toString().trim()

                    if(emailIDDB.equals(currentUserEmailID))
                    {
                        var dateDB=data.child("calendarTime").getValue().toString().trim()
                        datesList2.add(dateDB)
                        if(dateDB.equals(currentCompleteDate))
                        {
                            basalWeightDB=data.child("mweight").getValue().toString().trim()
                            basalTDIDB=data.child("mtdi").getValue().toString().trim()
                            basalInsulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                            typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()

                            var mBasalBGRecyclerView: BasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                            mBasalBGList.add(mBasalBGRecyclerView)
                        }
                    }
                }

                if (mBasalBGList.size > 0) {
                    basalTitleTextview.setText("Basal Insulin")
                    RV3.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV3.setAdapter(adapter2(mBasalBGList))
                    //Toast.makeText(applicationContext,"Data loaded, contains " + mBasalBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext,"No Basal Blood Glucose data saved for this day",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Could not read from database, please check your internet connection",Toast.LENGTH_LONG).show()
            }
        })


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            //Months are indexed from 0. So, 0 means january, 1 means February, 2 means march etc.
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year

            myDate=dayOfMonth.toString()
            someMonth=month+1
            myMonth=someMonth.toString()
            myYear=year.toString()

            isDateChanged=true

            bolusTitleTextView.setText("")
            basalTitleTextview.setText("")

            convertMonthNumbertoWord()
            clickedDate=myDate+"-"+monthString+"-"+myYear

            Toast.makeText(applicationContext,"Loading Blood Glucose data",Toast.LENGTH_SHORT).show()

            if (mBolusBGList.size > 0) {
                RV1.removeAllViews()
                mBolusBGList.clear()
            }

            if(mBasalBGList.size>0)
            {
                RV3.removeAllViews()
                mBasalBGList.clear()
            }

            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (data: DataSnapshot in dataSnapshot.getChildren())
                    {
                        var emailIDDB=data.child("emailID").getValue().toString().trim()

                        if(emailIDDB.equals(currentUserEmailID))
                        {
                            var dateDB=data.child("calendarTime").getValue().toString().trim()
                            if(dateDB.equals(clickedDate))
                            {
                                eventDB=data.child("beforeEvent").getValue().toString().trim()
                                currentBGDB=data.child("currentBGLevel").getValue().toString().trim()
                                targetBGDB=data.child("targetBGLevel").getValue().toString().trim()
                                amountOfCHODB=data.child("totalCHO").getValue().toString().trim()
                                disposedCHODB=data.child("amountDisposedByInsulin").getValue().toString().trim()
                                correctionFactorDB=data.child("correctionFactor").getValue().toString().trim()
                                insulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                                typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()
                                var mBGRecyclerView: BolusBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                                mBolusBGList.add(mBGRecyclerView)
                            }
                        }
                    }

                    if (mBolusBGList.size > 0) {
                        bolusTitleTextView.setText("Bolus Insulin")
                        RV1.setLayoutManager(LinearLayoutManager(applicationContext))
                        RV1.setAdapter(adapter1(mBolusBGList))
                        Toast.makeText(applicationContext,"Data loaded, contains " + mBolusBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"No Bolus Blood Glucose data saved for this day",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"Could not read from database, please check your internet connection",Toast.LENGTH_LONG).show()
                }
            })

            rootRef10.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (data: DataSnapshot in dataSnapshot.getChildren())
                    {
                        var emailIDDB=data.child("emailID").getValue().toString().trim()

                        if(emailIDDB.equals(currentUserEmailID))
                        {
                            var dateDB=data.child("calendarTime").getValue().toString().trim()
                            datesList2.add(dateDB)
                            if(dateDB.equals(clickedDate))
                            {
                                basalWeightDB=data.child("mweight").getValue().toString().trim()
                                basalTDIDB=data.child("mtdi").getValue().toString().trim()
                                basalInsulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                                typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()

                                var mBasalBGRecyclerView: BasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                                mBasalBGList.add(mBasalBGRecyclerView)
                            }
                        }
                    }

                    if (mBasalBGList.size > 0) {
                        basalTitleTextview.setText("Basal Insulin")
                        RV3.setLayoutManager(LinearLayoutManager(applicationContext))
                        RV3.setAdapter(adapter2(mBasalBGList))
                        //Toast.makeText(applicationContext,"Data loaded, contains " + mBasalBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(applicationContext,"No Basal Blood Glucose data saved for this day",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"Could not read from database, please check your internet connection",Toast.LENGTH_LONG).show()
                }
            })
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
            }

            else
            {
                val i=Intent(this,NotesActivity::class.java)
                i.putExtra("date",myDate)
                i.putExtra("month",myMonth)
                i.putExtra("year",myYear)
                startActivity(i)
            }
        }
    }


    fun getCurrentCalendarDate()
    {
        val c = java.util.Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("dd-MMM-yyyy")
        val currentTime = df.format(c)

        currentCompleteDate=currentTime.toString()
    }

    fun convertMonthNumbertoWord()
    {
        if(someMonth==1)
        {
            monthString="Jan"
        }

        else if(someMonth==2)
        {
            monthString="Feb"
        }

        else if(someMonth==3)
        {
            monthString="Mar"
        }

        else if(someMonth==4)
        {
            monthString="Apr"
        }

        else if(someMonth==5)
        {
            monthString="May"
        }

        else if(someMonth==6)
        {
            monthString="Jun"
        }

        else if(someMonth==7)
        {
            monthString="Jul"
        }

        else if(someMonth==8)
        {
            monthString="Aug"
        }

        else if(someMonth==9)
        {
            monthString="Sep"
        }

        else if(someMonth==10)
        {
            monthString="Oct"
        }

        else if(someMonth==11)
        {
            monthString="Nov"
        }

        else if(someMonth==12)
        {
            monthString="Dec"
        }
    }
}

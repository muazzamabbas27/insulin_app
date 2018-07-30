package com.ciklum.insulinapp.activitiesPackage.calendarPackage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import com.ciklum.insulinapp.activitiesPackage.Notes.NotesActivity
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

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var mCalendarView:CalendarView
    private lateinit var makeNotesBtn:Button
    private lateinit var bolusTitleTextView: TextView
    private lateinit var basalTitleTextView: TextView
    private lateinit var calendarBolusRV: RecyclerView
    private lateinit var calendarBasalRV:RecyclerView

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private lateinit var currentUserEmailID:String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseUser:FirebaseUser
    private lateinit var calendarBolusRootRef: DatabaseReference
    private lateinit var calendarBasalRootRef:DatabaseReference

    /*-----------------------------------------Bolus data to get from Firebase-------------------------------------------------*/
    private lateinit var eventDB:String
    private lateinit var currentBGDB:String
    private lateinit var targetBGDB:String
    private lateinit var amountOfCHODB:String
    private lateinit var disposedCHODB:String
    private lateinit var correctionFactorDB:String
    private lateinit var insulinRecommendationDB:String
    private lateinit var typeOfInsulinDB:String

    /*------------------------------------------Basal data to get from Firebase------------------------------------------------*/
    private lateinit var basalWeightDB:String
    private lateinit var basalTDIDB:String
    private lateinit var basalInsulinRecommendationDB:String


    /*------------------------------------------ArrayLists to store Firebase objects------------------------------------------------*/
    var mBolusBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBasalBGList:ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var datesList:ArrayList<String> = ArrayList(1000)
    var datesList2:ArrayList<String> = ArrayList(1000)


    /*------------------------------------------Date-related variables------------------------------------------------*/
    private var myDate:String=""
    private var myMonth:String=""
    private var myYear:String=""
    private var isDateChanged=false

    private var tempDate:String?=null
    private var tempMonth:String?=null
    private var tempYear:String?=null

    private var someMonth:Int=-1
    private lateinit var monthString: String
    private lateinit var clickedDate:String

    private lateinit var currentCompleteDate:String


    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_calendar)


        /*-----------------Fetching views and initializing data-------------------*/

        mCalendarView=findViewById(R.id.calendarView)
        makeNotesBtn=findViewById(R.id.makeNotesBtn)
        calendarBolusRV=findViewById(R.id.calendarBolusRV)
        calendarBasalRV=findViewById(R.id.calendarBasalRV)
        bolusTitleTextView=findViewById(R.id.bolusTitleTextView)
        basalTitleTextView=findViewById(R.id.basalTitleTextView)
        bolusTitleTextView.text = ""
        basalTitleTextView.text = ""
        getCurrentCalendarDate()

        /*-----------------Clearing previous data-------------------*/

        if (mBolusBGList.size > 0)
        {
            calendarBolusRV.removeAllViews()
            mBolusBGList.clear()
        }

        if(mBasalBGList.size>0)
        {
            calendarBasalRV.removeAllViews()
            mBasalBGList.clear()
        }

        /*-----------------Fetching Firebase data-------------------*/

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        calendarBolusRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.bolusBGDataTable))
        calendarBasalRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.basalBGDataTable))
        mFirebaseUser= mAuth.currentUser!!
        currentUserEmailID= mFirebaseUser.email!!


        Toast.makeText(applicationContext,resources.getString(R.string.calendarLoadingDataToastTextLiteral),Toast.LENGTH_SHORT).show()


        /*-----------------Load Bolus data when app starts-------------------*/

        calendarBolusRootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data: DataSnapshot in dataSnapshot.children)
                {
                    val emailIDDB=data.child(resources.getString(R.string.bolusEmailColumn)).value.toString().trim()
                    if(emailIDDB == currentUserEmailID)
                    {
                        val dateDB=data.child(resources.getString(R.string.bolusCalendarTimeColumn)).value.toString().trim()
                        datesList.add(dateDB)
                        if(dateDB == currentCompleteDate)
                        {
                            eventDB=data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString().trim()
                            currentBGDB=data.child(resources.getString(R.string.bolusCurrentBGLevelColumn)).value.toString().trim()
                            targetBGDB=data.child(resources.getString(R.string.bolusTargetBGLevelColumn)).value.toString().trim()
                            amountOfCHODB=data.child(resources.getString(R.string.bolusTotalCHOColumn)).value.toString().trim()
                            disposedCHODB=data.child(resources.getString(R.string.bolusCHOAmountDisposedByInsulinColumn)).value.toString().trim()
                            correctionFactorDB=data.child(resources.getString(R.string.bolusCorrectionFactorColumn)).value.toString().trim()
                            insulinRecommendationDB=data.child(resources.getString(R.string.bolusInsulinRecommendationColumn)).value.toString().trim()
                            typeOfInsulinDB=data.child(resources.getString(R.string.bolusTypeOfBGColumn)).value.toString().trim()
                            val mBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                            mBolusBGList.add(mBGRecyclerView)
                        }
                    }
                }

                if (mBolusBGList.size > 0) {
                    bolusTitleTextView.text = resources.getString(R.string.calendarBolusLiteral)
                    calendarBolusRV.layoutManager = LinearLayoutManager(applicationContext)
                    calendarBolusRV.adapter = adapter1(mBolusBGList)
                }
                else
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.calendarNoBGDataToastTextLiteral),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
            }
        })


        /*-----------------Load Basal data when app starts-------------------*/

        calendarBasalRootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data: DataSnapshot in dataSnapshot.children)
                {
                    val emailIDDB=data.child(resources.getString(R.string.basalEmailColumn)).value.toString().trim()

                    if(emailIDDB == currentUserEmailID)
                    {
                        val dateDB=data.child(resources.getString(R.string.basalCalendarTimeColumn)).value.toString().trim()
                        datesList2.add(dateDB)
                        if(dateDB == currentCompleteDate)
                        {
                            basalWeightDB=data.child(resources.getString(R.string.basalWeightColumn)).value.toString().trim()
                            basalTDIDB=data.child(resources.getString(R.string.basalTDIColumn)).value.toString().trim()
                            basalInsulinRecommendationDB=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()
                            typeOfInsulinDB=data.child(resources.getString(R.string.basalTypeOfBGColumn)).value.toString().trim()

                            val mBasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                            mBasalBGList.add(mBasalBGRecyclerView)
                        }
                    }
                }

                if (mBasalBGList.size > 0) {
                    basalTitleTextView.text = resources.getString(R.string.calendarBasalLiteral)
                    calendarBasalRV.layoutManager = LinearLayoutManager(applicationContext)
                    calendarBasalRV.adapter = adapter2(mBasalBGList)
                }
                else
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.calendarNoBasalBGDataToastTextLiteral),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
            }
        })


        /*-----------------When user changes date on calendar-------------------*/

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            /*-----------------Reinitialize data-------------------*/

            bolusTitleTextView.text = ""
            basalTitleTextView.text = ""

            if (mBolusBGList.size > 0) {
                calendarBolusRV.removeAllViews()
                mBolusBGList.clear()
            }

            if(mBasalBGList.size>0)
            {
                calendarBasalRV.removeAllViews()
                mBasalBGList.clear()
            }


            /*-----------------Calculate date-------------------*/

            isDateChanged=true

            myDate=dayOfMonth.toString()
            someMonth=month+1
            myMonth=someMonth.toString()
            myYear=year.toString()

            convertMonthNumbertoWord()
            clickedDate= "$myDate-$monthString-$myYear"

            Toast.makeText(applicationContext,resources.getString(R.string.loadingDataDB),Toast.LENGTH_SHORT).show()


            /*-----------------Load Bolus data on date change-------------------*/

            calendarBolusRootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data: DataSnapshot in dataSnapshot.children)
                    {
                        val emailIDDB=data.child(resources.getString(R.string.bolusEmailColumn)).value.toString().trim()

                        if(emailIDDB == currentUserEmailID)
                        {
                            val dateDB=data.child(resources.getString(R.string.bolusCalendarTimeColumn)).value.toString().trim()
                            if(dateDB == clickedDate)
                            {
                                eventDB=data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString().trim()
                                currentBGDB=data.child(resources.getString(R.string.bolusCurrentBGLevelColumn)).value.toString().trim()
                                targetBGDB=data.child(resources.getString(R.string.bolusTargetBGLevelColumn)).value.toString().trim()
                                amountOfCHODB=data.child(resources.getString(R.string.bolusTotalCHOColumn)).value.toString().trim()
                                disposedCHODB=data.child(resources.getString(R.string.bolusCHOAmountDisposedByInsulinColumn)).value.toString().trim()
                                correctionFactorDB=data.child(resources.getString(R.string.bolusCorrectionFactorColumn)).value.toString().trim()
                                insulinRecommendationDB=data.child(resources.getString(R.string.bolusInsulinRecommendationColumn)).value.toString().trim()
                                typeOfInsulinDB=data.child(resources.getString(R.string.bolusTypeOfBGColumn)).value.toString().trim()
                                val mBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                                mBolusBGList.add(mBGRecyclerView)
                            }
                        }
                    }

                    if (mBolusBGList.size > 0) {
                        bolusTitleTextView.text = resources.getString(R.string.calendarBolusLiteral)
                        calendarBolusRV.layoutManager = LinearLayoutManager(applicationContext)
                        calendarBolusRV.adapter = adapter1(mBolusBGList)
                    }
                    else
                    {
                        Toast.makeText(applicationContext,resources.getString(R.string.calendarNoBGDataToastTextLiteral),Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
                }
            })


            /*-----------------Load Basal data on date change-------------------*/

            calendarBasalRootRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data: DataSnapshot in dataSnapshot.children)
                    {
                        val emailIDDB=data.child(resources.getString(R.string.basalEmailColumn)).value.toString().trim()

                        if(emailIDDB == currentUserEmailID)
                        {
                            val dateDB=data.child(resources.getString(R.string.basalCalendarTimeColumn)).value.toString().trim()
                            datesList2.add(dateDB)
                            if(dateDB == clickedDate)
                            {
                                basalWeightDB=data.child(resources.getString(R.string.basalWeightColumn)).value.toString().trim()
                                basalTDIDB=data.child(resources.getString(R.string.basalTDIColumn)).value.toString().trim()
                                basalInsulinRecommendationDB=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()
                                typeOfInsulinDB=data.child(resources.getString(R.string.basalTypeOfBGColumn)).value.toString().trim()

                                val mBasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                                mBasalBGList.add(mBasalBGRecyclerView)
                            }
                        }
                    }

                    if (mBasalBGList.size > 0) {
                        basalTitleTextView.text = resources.getString(R.string.calendarBasalLiteral)
                        calendarBasalRV.layoutManager = LinearLayoutManager(applicationContext)
                        calendarBasalRV.adapter = adapter2(mBasalBGList)
                    }
                    else
                    {
                        Toast.makeText(applicationContext,resources.getString(R.string.calendarNoBasalBGDataToastTextLiteral),Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
                }
            })
        }


        /*-----------------Notes button implementation-------------------*/

        makeNotesBtn.setOnClickListener()
        {

            //if user has not changed date, find current date and pass to Notes activity

            if(!isDateChanged)
            {
                val date = Calendar.getInstance().time

                val sdf1 = SimpleDateFormat("dd", Locale.US)
                tempDate=sdf1.format(date)

                val sdf2 = SimpleDateFormat("M",Locale.US)
                tempMonth=sdf2.format(date)

                val sdf3 = SimpleDateFormat("yyyy",Locale.US)
                tempYear=sdf3.format(date)

                val i=Intent(this,NotesActivity::class.java)
                i.putExtra("date",tempDate)
                i.putExtra("month",tempMonth)
                i.putExtra("year",tempYear)
                startActivity(i)
            }


            //if user has changed date, pass date to Notes activity as is

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


    /*-----------------Miscellaneous date-related functions-------------------*/

    private fun getCurrentCalendarDate()
    {
        val c = java.util.Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MMM-yyyy",Locale.US)
        val currentTime = df.format(c)

        currentCompleteDate=currentTime.toString()
    }

    private fun convertMonthNumbertoWord()
    {
        when (someMonth) {
            1 -> monthString=resources.getString(R.string.monthJan)
            2 -> monthString=resources.getString(R.string.monthFeb)
            3 -> monthString=resources.getString(R.string.monthMar)
            4 -> monthString=resources.getString(R.string.monthApr)
            5 -> monthString=resources.getString(R.string.monthMay)
            6 -> monthString=resources.getString(R.string.monthJun)
            7 -> monthString=resources.getString(R.string.monthJul)
            8 -> monthString=resources.getString(R.string.monthAug)
            9 -> monthString=resources.getString(R.string.monthSep)
            10 -> monthString=resources.getString(R.string.monthOct)
            11 -> monthString=resources.getString(R.string.monthNov)
            12 -> monthString=resources.getString(R.string.monthDec)
        }
    }
}

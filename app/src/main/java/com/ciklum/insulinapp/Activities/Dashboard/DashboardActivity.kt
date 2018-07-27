package com.ciklum.insulinapp.Activities.Dashboard

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.EventLogTags
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ciklum.insulinapp.Activities.Menu.MenuActivity
import com.ciklum.insulinapp.R
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.ciklum.insulinapp.Activities.Calendar.MyCalendar
import com.ciklum.insulinapp.Activities.DetailedReports.DetailedReportsActivity
import com.ciklum.insulinapp.Activities.Disclaimer.DisclaimerActivity
import com.ciklum.insulinapp.Activities.Doctors.DoctorActivity
import com.ciklum.insulinapp.Activities.HealthArticles.HealthArticlesActivity
import com.ciklum.insulinapp.Activities.LogBG.LogBasalBGActivity
import com.ciklum.insulinapp.Activities.LogBG.LogBolusBGActivity
import com.ciklum.insulinapp.Activities.MedicineReminder.ReminderActivity
import com.ciklum.insulinapp.Activities.UserProfile.EditUserProfileActivity
import com.ciklum.insulinapp.Activities.UserProfile.UserProfileActivity
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Adapters.adapter2
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class DashboardActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout:DrawerLayout
    private lateinit var mToggle:ActionBarDrawerToggle
    private lateinit var navigationView:NavigationView

    private lateinit var progressBar4: ProgressBar
    private lateinit var nameDashboardTextView:TextView
    private lateinit var ageDashboardTextView:TextView
    private lateinit var RV5:RecyclerView
    private lateinit var RV6:RecyclerView
    private lateinit var latestBolusTextView:TextView
    private lateinit var latestBasalTextView: TextView

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var currentUserEmailID:String

    private lateinit var dateDB:String
    private lateinit var eventDB:String
    private lateinit var currentBGDB:String
    private lateinit var targetBGDB:String
    private lateinit var amountOfCHODB:String
    private lateinit var disposedCHODB:String
    private lateinit var correctionFactorDB:String
    private lateinit var insulinRecommendationDB:String
    private lateinit var typeOfInsulinDB:String

    var mBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBGListShow: ArrayList<BolusBGRecyclerView> = ArrayList(1)

    private var totalBolusSugarLevel:Int=0
    private var avgBolusSugarLevel:Int=0
    private lateinit var assitantTextView:TextView


    private lateinit var basalWeightDB:String
    private lateinit var basalTDIDB:String
    private lateinit var basalInsulinRecommendationDB:String

    private var lowBGDB:Int=0
    private var normalBGDB:Int=0
    private var highBGDB:Int=0

    var mBasalBGList: ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var mBasalBGListShow: ArrayList<BasalBGRecyclerView> = ArrayList(1)

    var entries: ArrayList<PieEntry> = ArrayList(1000)
    private lateinit var myPieChart:PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        progressBar4=findViewById(R.id.progressBar4)
        nameDashboardTextView=findViewById(R.id.nameDashboardTextView)
        ageDashboardTextView=findViewById(R.id.ageDashboardTextView)
        RV5=findViewById(R.id.RV5)
        RV6=findViewById(R.id.RV6)
        latestBolusTextView=findViewById(R.id.latestBolusTextView)
        latestBasalTextView=findViewById(R.id.latestBasalTextView)
        assitantTextView=findViewById(R.id.assistantTextView)
        myPieChart=findViewById(R.id.myPieChart)

        progressBar4.setVisibility(View.VISIBLE)
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!

        mFirebaseDatabase= FirebaseDatabase.getInstance()

        var rootRef12=FirebaseDatabase.getInstance().getReference("User")

        // Read from the database
        rootRef12.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        var userName = data.child("mname").getValue().toString().trim()
                        var userAge = data.child("mage").getValue().toString().trim()

                        nameDashboardTextView.setText(userName)
                        ageDashboardTextView.setText(userAge)
                    }
                }
            }
        })


        var rootRef13=FirebaseDatabase.getInstance().getReference("Bolus BG Data")


        rootRef13.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var emailIDDB = data.child("emailID").getValue().toString().trim()
                    if (emailIDDB.equals(currentUserEmailID)) {
                        dateDB=data.child("calendarTime").getValue().toString().trim()
                        eventDB = data.child("beforeEvent").getValue().toString().trim()
                        currentBGDB = data.child("currentBGLevel").getValue().toString().trim()
                        targetBGDB = data.child("targetBGLevel").getValue().toString().trim()
                        amountOfCHODB = data.child("totalCHO").getValue().toString().trim()
                        disposedCHODB = data.child("amountDisposedByInsulin").getValue().toString().trim()
                        correctionFactorDB = data.child("correctionFactor").getValue().toString().trim()
                        insulinRecommendationDB = data.child("insulinRecommendation").getValue().toString().trim()
                        typeOfInsulinDB = data.child("typeOfBG").getValue().toString().trim()

                        totalBolusSugarLevel=currentBGDB.toInt()+totalBolusSugarLevel

                        if(currentBGDB.toInt()<70)
                        {
                            lowBGDB++
                        }

                        if(currentBGDB.toInt()>70 && currentBGDB.toInt()<90)
                        {
                            normalBGDB++
                        }

                        if(currentBGDB.toInt()>99)
                        {
                            highBGDB++
                        }

                        var mBGRecyclerView: BolusBGRecyclerView = BolusBGRecyclerView(eventDB, currentBGDB, targetBGDB, amountOfCHODB, disposedCHODB, correctionFactorDB, insulinRecommendationDB, typeOfInsulinDB)
                        mBGList.add(mBGRecyclerView)
                    }
                }

                if(mBGList.size>0)
                {
                    latestBolusTextView.setText("Latest Bolus Log " + "(" + dateDB + "):")
                    mBGListShow.add(mBGList[(mBGList.size)-1])
                    RV5.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV5.setAdapter(adapter1(mBGListShow))
                    avgBolusSugarLevel=totalBolusSugarLevel/mBGList.size

                    if(avgBolusSugarLevel>99)
                    {
                        assitantTextView.setText("Assitant says: Your lifetime average bolus sugar level has been " +avgBolusSugarLevel.toString() + " mg/dl in the past " + mBGList.size.toString() + " readings. You should consume less carbohydrates. Normal pre-meal blood sugar level is 70-99mg/dl.")
                    }
                    if(avgBolusSugarLevel<70)
                    {
                        assitantTextView.setText("Assistant says: Your lifetime average bolus sugar level has been " +avgBolusSugarLevel.toString() + " mg/dl in the past " + mBGList.size.toString() + " readings. You should consume more carbohydrates. Normal pre-meal blood sugar level is 70-99mg/dl.")
                    }
                    if(avgBolusSugarLevel>70 && avgBolusSugarLevel<99)
                    {
                        assitantTextView.setText("Assistant says: Your lifetime average bolus sugar level has been " +avgBolusSugarLevel.toString() + " mg/dl in the past " + mBGList.size.toString() + " readings. Normal pre-meal blood sugar level is 70-99mg/dl, good work!")
                    }

                    var listOfColors:ArrayList<Int> = ArrayList(3)

                    if(lowBGDB!=0)
                    {
                        var lowPercentage:Float=lowBGDB.toFloat()
                        entries.add(PieEntry(lowPercentage,"Low Bolus Blood Sugar"))
                        listOfColors.add(Color.YELLOW)
                    }

                    if(normalBGDB!=0)
                    {
                        var normalPercentage:Float=normalBGDB.toFloat()
                        entries.add(PieEntry(normalPercentage,"Normal Bolus Blood Sugar"))
                        listOfColors.add(Color.GREEN)
                    }

                    if(highBGDB!=0)
                    {
                        var highPercentage:Float=highBGDB.toFloat()
                        entries.add(PieEntry(highPercentage,"High Bolus Blood Sugar"))
                        listOfColors.add(Color.RED)
                    }

                    var set:PieDataSet= PieDataSet(entries,"")
                    set.setSliceSpace(3f)
                    set.setSelectionShift(5f)


                    set.setColors(listOfColors)

                    var data:PieData= PieData(set)
                    data.setValueTextSize(10f)
                    data.setValueTextColor(Color.BLACK)

                    var mDescription:Description=Description()
                    mDescription.setTextSize(15f)
                    mDescription.setText("Bolus Blood Sugar Levels")

                    myPieChart.setDrawEntryLabels(false)
                    myPieChart.setDescription(mDescription)
                    myPieChart.setUsePercentValues(true)
                    myPieChart.getDescription().setEnabled(false)
                    myPieChart.setExtraOffsets(5f,10f,5f,5f)
                    myPieChart.setDragDecelerationFrictionCoef(0.99f)
                    myPieChart.setDrawHoleEnabled(true)
                    myPieChart.setHoleColor(Color.WHITE)
                    myPieChart.setTransparentCircleRadius(61f)

                    myPieChart.setData(data)
                    myPieChart.setTransparentCircleAlpha(0)

                    myPieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic)
                    myPieChart.invalidate()
                }
                else
                {
                    latestBolusTextView.setText("No Bolus data logged yet")
                }

            }

            override fun onCancelled(p0: DatabaseError?) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })


        var rootRef14=FirebaseDatabase.getInstance().getReference("Basal BG Data")

        rootRef14.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var emailIDDB = data.child("emailID").getValue().toString().trim()

                    if (emailIDDB.equals(currentUserEmailID)) {
                        dateDB=data.child("calendarTime").getValue().toString().trim()
                        basalWeightDB=data.child("mweight").getValue().toString().trim()
                        basalTDIDB=data.child("mtdi").getValue().toString().trim()
                        basalInsulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                        typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()

                        var mBasalBGRecyclerView: BasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                        mBasalBGList.add(mBasalBGRecyclerView)
                    }
                }

                if(mBasalBGList.size>0)
                {
                    latestBasalTextView.setText("Latest Basal Log " + "(" + dateDB + "):")
                    mBasalBGListShow.add(mBasalBGList[(mBasalBGList.size)-1])
                    RV6.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV6.setAdapter(adapter2(mBasalBGListShow))
                }
                else
                {
                    latestBolusTextView.setText("No Basal data logged yet")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })

        progressBar4.setVisibility(View.INVISIBLE)

        mDrawerLayout=findViewById(R.id.mDrawerLayout)
        mToggle= ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close)
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        navigationView= findViewById(R.id.nav_view)
        navigationView.bringToFront()

        navigationView.setNavigationItemSelectedListener {menuItem ->
            // set item as selected to persist highlight
            //menuItem.isChecked = true
            // close drawer when item is tapped
            //mDrawerLayout.closeDrawers()

            var id:Int=menuItem.itemId

            if(id==R.id.viewProfileDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,UserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.editProfileDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,EditUserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBolusBGDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,LogBolusBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBasalBGDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,LogBasalBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.calendarDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,MyCalendar::class.java)
                startActivity(i)
            }

            if(id==R.id.medicineReminderDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,ReminderActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.healthArticlesDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,HealthArticlesActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.doctorInfoDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,DoctorActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.disclaimerDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,DisclaimerActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.detailedReportsDrawerBtn)
            {
                val i:Intent=Intent(applicationContext,DetailedReportsActivity::class.java)
                startActivity(i)
            }

            true
        }

    }

    override fun onRestart() {
        super.onRestart()
        if(mDrawerLayout.isDrawerOpen(navigationView))
        {
            mDrawerLayout.closeDrawer(navigationView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        if(mToggle.onOptionsItemSelected(item))
        {
            return true
        }

        when (item.itemId) {
            R.id.logoutMenuBtn -> {
                mAuth?.signOut()
                val i:Intent=Intent(this,MenuActivity::class.java)
                startActivity(i)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}

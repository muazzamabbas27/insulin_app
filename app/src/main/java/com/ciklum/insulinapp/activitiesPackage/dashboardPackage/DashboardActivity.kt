package com.ciklum.insulinapp.activitiesPackage.dashboardPackage

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ciklum.insulinapp.activitiesPackage.menuPackage.MenuActivity
import com.ciklum.insulinapp.R
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.ciklum.insulinapp.activitiesPackage.calendarPackage.MyCalendar
import com.ciklum.insulinapp.activitiesPackage.detailedReportsPackage.DetailedReportsActivity
import com.ciklum.insulinapp.activitiesPackage.disclaimerPackage.DisclaimerActivity
import com.ciklum.insulinapp.activitiesPackage.doctorsPackage.DoctorActivity
import com.ciklum.insulinapp.activitiesPackage.healthArticlesPackage.HealthArticlesActivity
import com.ciklum.insulinapp.activitiesPackage.logBGPackage.LogBasalBGActivity
import com.ciklum.insulinapp.activitiesPackage.logBGPackage.LogBolusBGActivity
import com.ciklum.insulinapp.activitiesPackage.medicineReminderPackage.ReminderActivity
import com.ciklum.insulinapp.activitiesPackage.userProfilePackage.EditUserProfileActivity
import com.ciklum.insulinapp.activitiesPackage.userProfilePackage.UserProfileActivity
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Adapters.adapter2
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.Utility.InternetUtility
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.github.mikephil.charting.data.PieEntry


class DashboardActivity : AppCompatActivity() {


    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var mDrawerLayout:DrawerLayout
    private lateinit var mToggle:ActionBarDrawerToggle
    private lateinit var navigationView:NavigationView
    private lateinit var progressBar4: ProgressBar
    private lateinit var nameDashboardTextView:TextView
    private lateinit var ageDashboardTextView:TextView
    private lateinit var dashboardBolusRV:RecyclerView
    private lateinit var dashboardBasalRV:RecyclerView
    private lateinit var latestBolusTextView:TextView
    private lateinit var latestBasalTextView: TextView
    private lateinit var myPieChart:PieChart


    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var currentUserEmailID:String
    private lateinit var dashboardUserRootRef:DatabaseReference
    private lateinit var dashboardBolusRootRef:DatabaseReference
    private lateinit var dashboardBasalRootRef:DatabaseReference

    /*-----------------------------------------Bolus data to get from Firebase-------------------------------------------------*/
    private lateinit var dateDB:String
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
    var mBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBGListShow: ArrayList<BolusBGRecyclerView> = ArrayList(1)
    var mBasalBGList: ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var mBasalBGListShow: ArrayList<BasalBGRecyclerView> = ArrayList(1)
    var entries: ArrayList<PieEntry> = ArrayList(1000)

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var totalBolusSugarLevel:Int=0
    private var avgBolusSugarLevel:Int=0
    private lateinit var assistantTextView:TextView

    private var lowBGDB:Int=0
    private var normalBGDB:Int=0
    private var highBGDB:Int=0

    private lateinit var finalString:String

    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        /*-----------------Fetching views and initializing data-------------------*/

        progressBar4=findViewById(R.id.progressBar4)
        progressBar4.visibility = View.VISIBLE
        nameDashboardTextView=findViewById(R.id.nameDashboardTextView)
        ageDashboardTextView=findViewById(R.id.ageDashboardTextView)
        dashboardBolusRV=findViewById(R.id.dashboardBolusRV)
        dashboardBasalRV=findViewById(R.id.dashboardBasalRV)
        latestBolusTextView=findViewById(R.id.latestBolusTextView)
        latestBasalTextView=findViewById(R.id.latestBasalTextView)
        assistantTextView=findViewById(R.id.assistantTextView)
        myPieChart=findViewById(R.id.myPieChart)
        mDrawerLayout=findViewById(R.id.mDrawerLayout)
        mToggle= ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close)
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigationView= findViewById(R.id.nav_view)
        navigationView.bringToFront()
        if(mDrawerLayout.isDrawerOpen(navigationView))
        {
            mDrawerLayout.closeDrawer(navigationView)
        }
        if(supportActionBar!=null)
        {
            supportActionBar!!.title = resources.getString(R.string.dashboardActionBarString)
        }

        /*-----------------Fetching Firebase data-------------------*/

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        dashboardUserRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.userTable))
        dashboardBolusRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.bolusBGDataTable))
        dashboardBasalRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.basalBGDataTable))


        /*-----------------Load User data when app starts-------------------*/

        dashboardUserRootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                if(!isInternetConnected)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                    return
                }

                for (data: DataSnapshot in dataSnapshot.children) {
                    val currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        val userName = data.child(resources.getString(R.string.userNameColumn)).value.toString().trim()
                        val userAge = data.child(resources.getString(R.string.userAgeColumn)).value.toString().trim()

                        nameDashboardTextView.text = userName
                        ageDashboardTextView.text = userAge
                    }
                }
            }
        })


        /*-----------------Load Bolus data when app starts-------------------*/

        dashboardBolusRootRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data: DataSnapshot in dataSnapshot.children) {

                    isInternetConnected=InternetUtility.isNetworkAvailable(applicationContext)

                    if(!isInternetConnected)
                    {
                        Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                        return
                    }

                    val emailIDDB = data.child(resources.getString(R.string.bolusEmailColumn)).value.toString().trim()
                    if (emailIDDB == currentUserEmailID) {
                        dateDB=data.child(resources.getString(R.string.bolusCalendarTimeColumn)).value.toString().trim()
                        eventDB = data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString().trim()
                        currentBGDB = data.child(resources.getString(R.string.bolusCurrentBGLevelColumn)).value.toString().trim()
                        targetBGDB = data.child(resources.getString(R.string.bolusTargetBGLevelColumn)).value.toString().trim()
                        amountOfCHODB = data.child(resources.getString(R.string.bolusTotalCHOColumn)).value.toString().trim()
                        disposedCHODB = data.child(resources.getString(R.string.bolusCHOAmountDisposedByInsulinColumn)).value.toString().trim()
                        correctionFactorDB = data.child(resources.getString(R.string.bolusCorrectionFactorColumn)).value.toString().trim()
                        insulinRecommendationDB = data.child(resources.getString(R.string.bolusInsulinRecommendationColumn)).value.toString().trim()
                        typeOfInsulinDB = data.child(resources.getString(R.string.bolusTypeOfBGColumn)).value.toString().trim()

                        totalBolusSugarLevel += currentBGDB.toInt()

                        if(currentBGDB.toInt()<70)
                        {
                            lowBGDB++
                        }

                        if(currentBGDB.toInt() in 71..89)
                        {
                            normalBGDB++
                        }

                        if(currentBGDB.toInt()>99)
                        {
                            highBGDB++
                        }

                        val mBGRecyclerView = BolusBGRecyclerView(eventDB, currentBGDB, targetBGDB, amountOfCHODB, disposedCHODB, correctionFactorDB, insulinRecommendationDB, typeOfInsulinDB)
                        mBGList.add(mBGRecyclerView)
                    }
                }

                if(mBGList.size>0)
                {
                    finalString=resources.getString(R.string.latestBolusLogTextLiteralStart) + dateDB + resources.getString(R.string.latestBolusLogTextLiteralEnd)
                    latestBolusTextView.text = finalString
                    mBGListShow.add(mBGList[(mBGList.size)-1])
                    dashboardBolusRV.layoutManager = LinearLayoutManager(applicationContext)
                    dashboardBolusRV.adapter = adapter1(mBGListShow)
                    avgBolusSugarLevel=totalBolusSugarLevel/mBGList.size

                    if(avgBolusSugarLevel>99)
                    {
                        finalString=resources.getString(R.string.assistantPreTextLiteral) +  " " + avgBolusSugarLevel.toString() + resources.getString(R.string.assistantMidTextLiteral) + " " + (mBGList.size).toString() + " " + resources.getString(R.string.assistantEndHighTextLiteral)
                        assistantTextView.text=finalString
                    }
                    if(avgBolusSugarLevel<70)
                    {
                        finalString=resources.getString(R.string.assistantPreTextLiteral) +  " " + avgBolusSugarLevel.toString() + resources.getString(R.string.assistantMidTextLiteral) + " " + (mBGList.size).toString() + " " + resources.getString(R.string.assistantEndLowTextLiteral)
                        assistantTextView.text=finalString
                    }
                    if(avgBolusSugarLevel in 71..99)
                    {
                        finalString=resources.getString(R.string.assistantPreTextLiteral) +  " " + avgBolusSugarLevel.toString() + resources.getString(R.string.assistantMidTextLiteral) + " " + (mBGList.size).toString() + " " + resources.getString(R.string.assistantEndNormalTextLiteral)
                        assistantTextView.text=finalString
                    }

                    val listOfColors:ArrayList<Int> = ArrayList(3)

                    if(lowBGDB!=0)
                    {
                        val lowPercentage:Float=lowBGDB.toFloat()
                        entries.add(PieEntry(lowPercentage,resources.getString(R.string.dashboardLowBGTextLiteral)))
                        listOfColors.add(Color.YELLOW)
                    }

                    if(normalBGDB!=0)
                    {
                        val normalPercentage:Float=normalBGDB.toFloat()
                        entries.add(PieEntry(normalPercentage,resources.getString(R.string.dashboardNormalBGTextLiteral)))
                        listOfColors.add(Color.GREEN)
                    }

                    if(highBGDB!=0)
                    {
                        val highPercentage:Float=highBGDB.toFloat()
                        entries.add(PieEntry(highPercentage,resources.getString(R.string.dashboardHighBGTextLiteral)))
                        listOfColors.add(Color.RED)
                    }

                    val set= PieDataSet(entries,"")
                    set.sliceSpace = 3f
                    set.selectionShift = 5f


                    set.colors = listOfColors

                    val data= PieData(set)
                    data.setValueTextSize(10f)
                    data.setValueTextColor(Color.BLACK)

                    val mDescription=Description()
                    mDescription.textSize = 15f
                    mDescription.text = resources.getString(R.string.dashboardPieChartDescriptionTextLiteral)

                    myPieChart.setDrawEntryLabels(false)
                    myPieChart.description = mDescription
                    myPieChart.setUsePercentValues(true)
                    myPieChart.description.isEnabled = false
                    myPieChart.setExtraOffsets(5f,10f,5f,5f)
                    myPieChart.dragDecelerationFrictionCoef = 0.99f
                    myPieChart.isDrawHoleEnabled = true
                    myPieChart.setHoleColor(Color.WHITE)
                    myPieChart.transparentCircleRadius = 61f

                    myPieChart.data = data
                    myPieChart.setTransparentCircleAlpha(0)

                    myPieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic)
                    myPieChart.invalidate()
                }
                else
                {
                    latestBolusTextView.text = resources.getString(R.string.noBolusDataTextLiteral)
                }

            }

            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }
        })

        /*-----------------Load Basal data when app starts-------------------*/

        dashboardBasalRootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                isInternetConnected=InternetUtility.isNetworkAvailable(applicationContext)

                if(!isInternetConnected)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                    return
                }

                for (data: DataSnapshot in dataSnapshot.children) {
                    val emailIDDB = data.child(resources.getString(R.string.basalEmailColumn)).value.toString().trim()

                    if (emailIDDB == currentUserEmailID) {
                        dateDB=data.child(resources.getString(R.string.basalCalendarTimeColumn)).value.toString().trim()
                        basalWeightDB=data.child(resources.getString(R.string.basalWeightColumn)).value.toString().trim()
                        basalTDIDB=data.child(resources.getString(R.string.basalTDIColumn)).value.toString().trim()
                        basalInsulinRecommendationDB=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()
                        typeOfInsulinDB=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()

                        val mBasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                        mBasalBGList.add(mBasalBGRecyclerView)
                    }
                }

                if(mBasalBGList.size>0)
                {
                    finalString=resources.getString(R.string.latestBasalLogTextLiteralStart) + dateDB + resources.getString(R.string.latestBasalLogTextLiteralEnd)
                    latestBasalTextView.text =finalString
                    mBasalBGListShow.add(mBasalBGList[(mBasalBGList.size)-1])
                    dashboardBasalRV.layoutManager = LinearLayoutManager(applicationContext)
                    dashboardBasalRV.adapter = adapter2(mBasalBGListShow)
                }
                else
                {
                    latestBolusTextView.text = resources.getString(R.string.noBolusDataTextLiteral)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }
        })

        progressBar4.visibility = View.INVISIBLE

        /*-----------------Navigation Drawer Implementation-------------------*/

        navigationView.setNavigationItemSelectedListener {menuItem ->

            val id:Int=menuItem.itemId

            if(id==R.id.viewProfileDrawerBtn)
            {
                val i=Intent(applicationContext,UserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.editProfileDrawerBtn)
            {
                val i=Intent(applicationContext,EditUserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBolusBGDrawerBtn)
            {
                val i=Intent(applicationContext,LogBolusBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBasalBGDrawerBtn)
            {
                val i=Intent(applicationContext,LogBasalBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.calendarDrawerBtn)
            {
                val i=Intent(applicationContext,MyCalendar::class.java)
                startActivity(i)
            }

            if(id==R.id.medicineReminderDrawerBtn)
            {
                val i=Intent(applicationContext,ReminderActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.healthArticlesDrawerBtn)
            {
                val i=Intent(applicationContext,HealthArticlesActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.doctorInfoDrawerBtn)
            {
                val i=Intent(applicationContext,DoctorActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.disclaimerDrawerBtn)
            {
                val i=Intent(applicationContext,DisclaimerActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.detailedReportsDrawerBtn)
            {
                val i=Intent(applicationContext,DetailedReportsActivity::class.java)
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

    /*-----------------Options Menu Implementation-------------------*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true
        }

        return when (item.itemId) {
            R.id.logoutMenuBtn -> {
                mAuth?.signOut()
                val i=Intent(this,MenuActivity::class.java)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

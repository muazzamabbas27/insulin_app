package com.ciklum.insulinapp.activitiesPackage.detailedReportsPackage

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.Toast
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Adapters.adapter2
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.R
import com.ciklum.insulinapp.Utility.InternetUtility
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class DetailedReportsActivity :AppCompatActivity() {


    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var reportsBolusRV: RecyclerView
    private lateinit var reportsBasalRV:RecyclerView
    private lateinit var mLineChart: LineChart
    private lateinit var mBarChart:BarChart

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var reportsBolusRootRef:DatabaseReference
    private lateinit var reportsBasalRootRef:DatabaseReference

    /*-----------------------------------------Bolus data to get from Firebase-------------------------------------------------*/
    private lateinit var eventDB:String
    private lateinit var currentBGDB:String
    private lateinit var targetBGDB:String
    private lateinit var amountOfCHODB:String
    private lateinit var disposedCHODB:String
    private lateinit var correctionFactorDB:String
    private lateinit var insulinRecommendationDB:String
    private lateinit var typeOfInsulinDB:String
    private lateinit var dateDB:String
    private lateinit var currentUserEmailID:String

    /*-----------------------------------------Basal data to get from Firebase-------------------------------------------------*/
    private lateinit var basalWeightDB:String
    private lateinit var basalTDIDB:String
    private lateinit var basalInsulinRecommendationDB:String

    /*------------------------------------------ArrayLists to store Firebase objects------------------------------------------------*/
    private var dateList: ArrayList<String> = ArrayList(1000)
    private var bgList: ArrayList<BarEntry> = ArrayList(1000)
    var mBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBGListShow: ArrayList<BolusBGRecyclerView> = ArrayList(1)
    private var basalInsulinList: ArrayList<Entry> = ArrayList(1000)
    private var basalDateList: ArrayList<String> = ArrayList(1000)
    var mBasalBGList:ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var mBasalBGListShow:ArrayList<BasalBGRecyclerView> = ArrayList(1)

    /*------------------------------------------Local data variables------------------------------------------------*/
    private lateinit var set:BarDataSet
    private var barSelected:Int=-1
    private lateinit var basalInsulinData:String
    private var basalInsulinNumber:Float=0.0F
    private var pointSelected:Int=-1
    private lateinit var bgDB:String
    private var bgDBNumber:Float=0.0F
    private lateinit var trimmedDate:String
    private lateinit var trimmedMonth:String
    private lateinit var trimmedYear:String
    private lateinit var trimmedEvent:String
    private lateinit var compactDBDate:String
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_reports)

        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.detailedReportsActionBarString)
        }



        /*-----------------Fetching views and initializing data-------------------*/

        reportsBolusRV=findViewById(R.id.reportsBolusRV)
        reportsBasalRV=findViewById(R.id.reportsBasalRV)
        mLineChart=findViewById(R.id.lineChart)
        mBarChart=findViewById(R.id.bargraph)
        mBarChart.setNoDataText(resources.getString(R.string.detailsNoBarChartTextLiteral))

        /*-----------------Fetching Firebase data-------------------*/

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        reportsBolusRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.bolusBGDataTable))
        reportsBasalRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.basalBGDataTable))
        mFirebaseUser= mAuth?.currentUser!!
        currentUserEmailID= mFirebaseUser?.email!!


        /*-----------------Load Bolus data when activity starts-------------------*/

        reportsBolusRootRef.addValueEventListener(object : ValueEventListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                barSelected= e!!.x.toInt()
                if(barSelected!=-1)
                {
                    mBGListShow.clear()
                    mBGListShow.add(mBGList[barSelected])
                    reportsBolusRV.layoutManager = LinearLayoutManager(applicationContext)
                    reportsBolusRV.adapter = adapter1(mBGListShow)
                }
            }

            override fun onNothingSelected() {
                reportsBolusRV.removeAllViews()
                if(mBGListShow.size>0)
                {
                    mBGListShow.clear()
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i=0.0F

                isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                if(isInternetConnected==false)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                    return
                }

                for (data: DataSnapshot in dataSnapshot.children) {
                    val emailIDDB = data.child(resources.getString(R.string.bolusEmailColumn)).value.toString().trim()

                    if (emailIDDB == currentUserEmailID) {
                        dateDB=data.child(resources.getString(R.string.bolusCalendarTimeColumn)).value.toString().trim() + " (" + data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString().trim() + ")"
                        val parsedDateList=dateDB.split("-")
                        trimmedDate=parsedDateList[0]
                        trimmedMonth=parsedDateList[1]
                        val temp=parsedDateList[2].split("(")
                        trimmedYear=temp[0]
                        val temp2=temp[1]
                        trimmedEvent=temp2[0].toString()

                        compactDBDate= "$trimmedDate-$trimmedMonth-$trimmedYear($trimmedEvent)"


                        bgDB=data.child(resources.getString(R.string.bolusCurrentBGLevelColumn)).value.toString().trim()
                        bgDBNumber=bgDB.toFloat()

                        dateList.add(compactDBDate)

                        bgList.add(BarEntry(i,bgDBNumber))
                        i++

                        eventDB=data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString().trim()
                        currentBGDB=data.child(resources.getString(R.string.bolusCurrentBGLevelColumn)).value.toString().trim()
                        targetBGDB=data.child(resources.getString(R.string.bolusTargetBGLevelColumn)).value.toString().trim()
                        amountOfCHODB=data.child(resources.getString(R.string.bolusTotalCHOColumn)).value.toString().trim()
                        disposedCHODB=data.child(resources.getString(R.string.bolusCHOAmountDisposedByInsulinColumn)).value.toString().trim()
                        correctionFactorDB=data.child(resources.getString(R.string.bolusCorrectionFactorColumn)).value.toString().trim()
                        insulinRecommendationDB=data.child(resources.getString(R.string.bolusInsulinRecommendationColumn)).value.toString().trim()
                        typeOfInsulinDB=data.child(resources.getString(R.string.bolusTypeOfBGColumn)).value.toString().trim()

                        val mBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                        mBGList.add(mBGRecyclerView)
                    }
                }

                set= BarDataSet(bgList,resources.getString(R.string.detailsBarChartLabelTextLiteral))
                mBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
                mBarChart.xAxis.granularity = 1f
                mBarChart.setOnChartValueSelectedListener(this)
                mBarChart.setFitBars(true)
                val mBarData=BarData(set)
                mBarData.barWidth = 0.9f
                mBarChart.data = mBarData
                mBarChart.setTouchEnabled(true)
                mBarChart.isDragEnabled = true
                mBarChart.setScaleEnabled(true)
                mBarChart.setDrawValueAboveBar(true)
                mBarChart.setMaxVisibleValueCount(1000)
                mBarChart.setVisibleXRangeMaximum(3F)
                mBarChart.animateXY(2000, 2000)

            }

            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }
        })


        /*-----------------Load Basal data when app starts-------------------*/

        reportsBasalRootRef.addValueEventListener(object : ValueEventListener,OnChartValueSelectedListener {
            override fun onNothingSelected() {
                reportsBasalRV.removeAllViews()
                if(mBasalBGListShow.size>0)
                {
                    mBasalBGListShow.clear()
                }
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                pointSelected= e!!.x.toInt()
                if(pointSelected!=-1)
                {
                    mBasalBGListShow.clear()
                    mBasalBGListShow.add(mBasalBGList[pointSelected])
                    reportsBasalRV.layoutManager = LinearLayoutManager(applicationContext)
                    reportsBasalRV.adapter = adapter2(mBasalBGListShow)
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i=0.0F

                isInternetConnected=InternetUtility.isNetworkAvailable(applicationContext)

                if(isInternetConnected==false)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                    return
                }

                val set1= LineDataSet(basalInsulinList,resources.getString(R.string.detailsLineChartLabelTextLiteral))
                for (data: DataSnapshot in dataSnapshot.children) {

                    val emailIDDB = data.child(resources.getString(R.string.basalEmailColumn)).value.toString().trim()

                    if (emailIDDB == currentUserEmailID) {
                        basalInsulinData=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()
                        basalInsulinNumber=basalInsulinData.toFloat()
                        basalInsulinList.add(Entry(i,basalInsulinNumber))
                        i++

                        dateDB=data.child(resources.getString(R.string.basalCalendarTimeColumn)).value.toString().trim()
                        basalDateList.add(dateDB)

                        basalWeightDB=data.child(resources.getString(R.string.basalWeightColumn)).value.toString().trim()
                        basalTDIDB=data.child(resources.getString(R.string.basalTDIColumn)).value.toString().trim()
                        basalInsulinRecommendationDB=data.child(resources.getString(R.string.basalInsulinRecommendationColumn)).value.toString().trim()
                        typeOfInsulinDB=data.child(resources.getString(R.string.basalTypeOfBGColumn)).value.toString().trim()

                        val mBasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                        mBasalBGList.add(mBasalBGRecyclerView)
                    }
                }
                mLineChart.isDragEnabled = true
                mLineChart.setScaleEnabled(false)
                set1.fillAlpha = 110
                mLineChart.xAxis.granularity = 1f
                mLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(basalDateList)
                val dataSet=LineDataSet(basalInsulinList,resources.getString(R.string.detailsLineDataSetLabelTextLiteral))
                val mLineData=LineData(dataSet)
                dataSet.circleRadius = 8f
                dataSet.lineWidth = 4f
                dataSet.setCircleColor(Color.rgb(255,0,0))
                dataSet.setCircleColorHole(Color.rgb(0,255,0))
                dataSet.circleHoleRadius = 4.0f
                mLineChart.data = mLineData
                mLineChart.setMaxVisibleValueCount(1000)
                mLineChart.setVisibleXRangeMaximum(3F)
                mLineChart.setOnChartValueSelectedListener(this)
                mLineChart.invalidate()
                mLineChart.animateXY(3000, 3000) // animate horizontal and vertical 3000 milliseconds
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}

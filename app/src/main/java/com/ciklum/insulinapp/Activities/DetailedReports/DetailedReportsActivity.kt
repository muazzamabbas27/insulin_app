package com.ciklum.insulinapp.Activities.DetailedReports

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.EventLogTags
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Adapters.adapter2
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class DetailedReportsActivity :AppCompatActivity() {

    private lateinit var set:BarDataSet

    private lateinit var mBarChart:BarChart
    private var dateList: ArrayList<String> = ArrayList(1000)
    private var BGList: ArrayList<BarEntry> = ArrayList(1000)

    private var barSelected:Int=-1
    var mBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBGListShow: ArrayList<BolusBGRecyclerView> = ArrayList(2)


    private lateinit var mLineChart: LineChart
    private var basalInsulinList: ArrayList<Entry> = ArrayList(1000)
    private var basalDateList: ArrayList<String> = ArrayList(1000)

    private lateinit var basalInsulinData:String
    private var basalInsulinNumber:Float=0.0F


    private var pointSelected:Int=-1
    var mBasalBGList:ArrayList<BasalBGRecyclerView> = ArrayList(1000)
    var mBasalBGListShow:ArrayList<BasalBGRecyclerView> = ArrayList(2)

    private lateinit var basalWeightDB:String
    private lateinit var basalTDIDB:String
    private lateinit var basalInsulinRecommendationDB:String

    private lateinit var currentUserEmailID:String

    private lateinit var dateDB:String
    private lateinit var BGDB:String
    private var BGDBNumber:Float=0.0F

    private lateinit var RV2: RecyclerView
    private lateinit var RV4:RecyclerView

    private lateinit var eventDB:String
    private lateinit var currentBGDB:String
    private lateinit var targetBGDB:String
    private lateinit var amountOfCHODB:String
    private lateinit var disposedCHODB:String
    private lateinit var correctionFactorDB:String
    private lateinit var insulinRecommendationDB:String
    private lateinit var typeOfInsulinDB:String

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_reports)

        RV2=findViewById(R.id.RV2)
        RV4=findViewById(R.id.RV4)

        mLineChart=findViewById(R.id.lineChart)

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Bolus BG Data")
        var rootRef7=FirebaseDatabase.getInstance().getReference("Bolus BG Data")

        mFirebaseUser= mAuth?.currentUser!!

        currentUserEmailID= mFirebaseUser?.email!!

        mBarChart=findViewById(R.id.bargraph)
        mBarChart.setNoDataText("No data has been logged to the cloud yet")


        rootRef7.addValueEventListener(object : ValueEventListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                barSelected= e!!.x.toInt()
                if(barSelected!=-1)
                {
                    mBGListShow.clear()
                    mBGListShow.add(mBGList[barSelected])
                    RV2.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV2.setAdapter(adapter1(mBGListShow))
                    //Toast.makeText(applicationContext,"Data loaded, contains " + mBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {
                RV2.removeAllViews()
                if(mBGListShow.size>0)
                {
                    mBGListShow.clear()
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var i:Float=0.0F

                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var emailIDDB = data.child("emailID").getValue().toString().trim()

                    if (emailIDDB.equals(currentUserEmailID)) {
                        dateDB=data.child("calendarTime").getValue().toString().trim() + " (" + data.child("beforeEvent").getValue().toString().trim() + ")"
                        BGDB=data.child("currentBGLevel").getValue().toString().trim()
                        BGDBNumber=BGDB.toFloat()

                        dateList.add(dateDB)

                        BGList.add(BarEntry(i,BGDBNumber))
                        i++

                        eventDB=data.child("beforeEvent").getValue().toString().trim()
                        currentBGDB=data.child("currentBGLevel").getValue().toString().trim()
                        targetBGDB=data.child("targetBGLevel").getValue().toString().trim()
                        amountOfCHODB=data.child("totalCHO").getValue().toString().trim()
                        disposedCHODB=data.child("amountDisposedByInsulin").getValue().toString().trim()
                        correctionFactorDB=data.child("correctionFactor").getValue().toString().trim()
                        insulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                        typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()

                        var mBGRecyclerView: BolusBGRecyclerView= BolusBGRecyclerView(eventDB,currentBGDB,targetBGDB,amountOfCHODB,disposedCHODB,correctionFactorDB,insulinRecommendationDB,typeOfInsulinDB)
                        mBGList.add(mBGRecyclerView)
                    }
                }

                set= BarDataSet(BGList,"Blood Glucose Levels")
                mBarChart.getXAxis().setValueFormatter(IndexAxisValueFormatter(dateList));
                mBarChart.xAxis.setGranularity(1f);
                //mBarChart.getXAxis().setDrawGridLines(false);
                //mBarChart.xAxis.setLabelRotationAngle(45f);
                mBarChart.setOnChartValueSelectedListener(this)
                var mBarBGDataSet: BarDataSet= BarDataSet(BGList,"BG Level")
                mBarChart.setFitBars(true)
                var mBarData:BarData=BarData(set)
                mBarData.setBarWidth(0.9f)
                mBarChart.setData(mBarData)
                mBarChart.setTouchEnabled(true)
                mBarChart.setDragEnabled(true)
                mBarChart.setScaleEnabled(true)
                //mBarChart.setDescription()
                mBarChart.setDrawValueAboveBar(true)
                mBarChart.setMaxVisibleValueCount(1000)
                mBarChart.setVisibleXRangeMaximum(3F)
                //mBarChart.invalidate()
                mBarChart.animateXY(3000, 3000); // animate horizontal and vertical 3000 milliseconds

            }

            override fun onCancelled(p0: DatabaseError?) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })

        var rootRef1=FirebaseDatabase.getInstance().getReference("Basal BG Data")

        rootRef1.addValueEventListener(object : ValueEventListener,OnChartValueSelectedListener {
            override fun onNothingSelected() {
                RV4.removeAllViews()
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
                    RV4.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV4.setAdapter(adapter2(mBasalBGListShow))
                    //Toast.makeText(applicationContext,"Data loaded, contains " + mBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var i:Float=0.0F
                var set1:LineDataSet= LineDataSet(basalInsulinList,"Data Set 1")
                for (data: DataSnapshot in dataSnapshot.getChildren()) {

                    var emailIDDB = data.child("emailID").getValue().toString().trim()

                    if (emailIDDB.equals(currentUserEmailID)) {
                        basalInsulinData=data.child("insulinRecommendation").getValue().toString().trim()
                        basalInsulinNumber=basalInsulinData.toFloat()
                        basalInsulinList.add(Entry(i,basalInsulinNumber))
                        i++

                        dateDB=data.child("calendarTime").getValue().toString().trim()
                        basalDateList.add(dateDB)

                        basalWeightDB=data.child("mweight").getValue().toString().trim()
                        basalTDIDB=data.child("mtdi").getValue().toString().trim()
                        basalInsulinRecommendationDB=data.child("insulinRecommendation").getValue().toString().trim()
                        typeOfInsulinDB=data.child("typeOfBG").getValue().toString().trim()

                        var mBasalBGRecyclerView: BasalBGRecyclerView= BasalBGRecyclerView(basalWeightDB,basalTDIDB,basalInsulinRecommendationDB,typeOfInsulinDB)
                        mBasalBGList.add(mBasalBGRecyclerView)
                    }
                }
                mLineChart.setDragEnabled(true)
                mLineChart.setScaleEnabled(false)
                set1.setFillAlpha(110)
                mLineChart.xAxis.setGranularity(1f)
                mLineChart.getXAxis().setValueFormatter(IndexAxisValueFormatter(basalDateList));
                var dataSet=LineDataSet(basalInsulinList,"Total Daily Insulin requirement")
                var mLineData=LineData(dataSet)
                dataSet.setCircleRadius(8f)
                dataSet.setLineWidth(4f)
                dataSet.setCircleColor(Color.rgb(255,0,0))
                dataSet.setCircleColorHole(Color.rgb(0,255,0))
                dataSet.setCircleHoleRadius(4.0f)
                mLineChart.setData(mLineData)
                mLineChart.setMaxVisibleValueCount(1000)
                mLineChart.setVisibleXRangeMaximum(3F)
                mLineChart.setOnChartValueSelectedListener(this)
                mLineChart.invalidate()
                mLineChart.animateXY(3000, 3000); // animate horizontal and vertical 3000 milliseconds
            }

            override fun onCancelled(error: DatabaseError) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })


    }
}

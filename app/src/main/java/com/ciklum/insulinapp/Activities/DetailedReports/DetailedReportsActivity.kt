package com.ciklum.insulinapp.Activities.DetailedReports

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ciklum.insulinapp.Adapters.adapter1
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight


interface OnChartValueSelectedListener {
    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected Entry.
     * @param h The corresponding highlight object that contains information
     * about the highlighted position
     */
    fun onValueSelected(e: Entry, h: Highlight)

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    fun onNothingSelected()
}



class DetailedReportsActivity :AppCompatActivity() {

    private lateinit var mBarChart:BarChart
    private var dateList: ArrayList<String> = ArrayList(1000)
    private var BGList: ArrayList<BarEntry> = ArrayList(1000)

    private var barSelected:Int=-1
    var mBGList: ArrayList<BolusBGRecyclerView> = ArrayList(1000)
    var mBGListShow: ArrayList<BolusBGRecyclerView> = ArrayList(2)



    private lateinit var currentUserEmailID:String

    private lateinit var dateDB:String
    private lateinit var BGDB:String
    private var BGDBNumber:Float=0.0F

    private lateinit var RV2: RecyclerView

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

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Bolus BG Data")
        var rootRef7=FirebaseDatabase.getInstance().getReference("Bolus BG Data")

        mFirebaseUser= mAuth?.currentUser!!

        currentUserEmailID= mFirebaseUser?.email!!

        mBarChart=findViewById(R.id.bargraph)
        mBarChart.setNoDataText("No data has been logged to the cloud yet")

        rootRef7.addValueEventListener(object : ValueEventListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, dataSetIndex: Int, h: Highlight?) {
                barSelected= e!!.xIndex
                if(barSelected!=-1)
                {
                    mBGListShow.clear()
                    mBGListShow.add(mBGList[barSelected])
                    RV2.setLayoutManager(LinearLayoutManager(applicationContext))
                    RV2.setAdapter(adapter1(mBGListShow))
                    //Toast.makeText(applicationContext,"Data loaded, contains " + mBGList.size.toString() + " items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var i:Int=0
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var emailIDDB = data.child("emailID").getValue().toString().trim()

                    if (emailIDDB.equals(currentUserEmailID)) {
                        dateDB=data.child("calendarTime").getValue().toString().trim() + " (" + data.child("beforeEvent").getValue().toString().trim() + ")"
                        BGDB=data.child("currentBGLevel").getValue().toString().trim()
                        BGDBNumber=BGDB.toFloat()

                        dateList.add(dateDB)
                        BGList.add(BarEntry(BGDBNumber,i))
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

                mBarChart.xAxis.setLabelsToSkip(0)
                mBarChart.setOnChartValueSelectedListener(this)
                var mBarBGDataSet: BarDataSet= BarDataSet(BGList,"BG Level")
                var mBarData:BarData=BarData(dateList,mBarBGDataSet)
                mBarChart.setData(mBarData)
                mBarChart.setTouchEnabled(true)
                mBarChart.setDragEnabled(true)
                mBarChart.setScaleEnabled(true)
                mBarChart.setDescription("")
                mBarChart.setMaxVisibleValueCount(1000)
                mBarChart.invalidate()
                mBarChart.animateXY(3000, 3000); // animate horizontal and vertical 3000 milliseconds
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }
}

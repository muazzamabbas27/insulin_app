package com.ciklum.insulinapp.activitiesPackage.logBGPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.ciklum.insulinapp.Models.BasalBGLevel
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class LogBasalBGActivity : AppCompatActivity() {


    /*-----------------------------------------UI Elements-------------------------------------------------*/

    private lateinit var weightTextView:TextView
    private lateinit var basalTDITextView:TextView
    private lateinit var calculateBasalBGBtn:Button
    private lateinit var recommendationBasalTextView:TextView

    /*-----------------------------------------Firebase variables-------------------------------------------------*/

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var basalBGRootRef: DatabaseReference
    private lateinit var userRootRef:DatabaseReference

    /*-----------------------------------------Basal data to get from Firebase-------------------------------------------------*/

    private var mWeight:Int=-1
    private var mTDI:Double=0.00
    private var basalBGRecommendation:Double=0.00
    private lateinit var currentUserEmailID:String
    private lateinit var calendarTime:String

    /*------------------------------------------Local data variables------------------------------------------------*/

    private var foundKey:String=""
    private var isKeyFound:Boolean=false
    private lateinit var finalString: String

    /*------------------------------------------Main code------------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_basal_bg)

        /*-----------------Fetching views and initializing data-------------------*/
        weightTextView=findViewById(R.id.weightTextView)
        basalTDITextView=findViewById(R.id.basalTDITextView)
        calculateBasalBGBtn=findViewById(R.id.calculateBasalBGBtn)
        recommendationBasalTextView=findViewById(R.id.recommendationBasalTextView)
        val c = java.util.Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        val currentTime = df.format(c)
        calendarTime=currentTime.toString()


        /*-----------------Fetching Firebase data-------------------*/
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        basalBGRootRef= mFirebaseDatabase.getReference(resources.getString(R.string.basalBGDataTable))
        userRootRef=FirebaseDatabase.getInstance().getReference(resources.getString(R.string.userTable))

        /*-----------------Load user data from Firebase-------------------*/

        userRootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data: DataSnapshot in dataSnapshot.children) {
                    val currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        val userWeight=data.child(resources.getString(R.string.basalWeightColumn)).value.toString().trim()
                        weightTextView.text = userWeight

                        mWeight=userWeight.toInt()
                        mTDI=0.55*mWeight

                        mTDI = java.lang.Double.parseDouble(String.format("%.2f", mTDI))
                        basalTDITextView.text = mTDI.toString()
                    }
                }
            }
        })


        /*-----------------Logging basal data implementation-------------------*/

        calculateBasalBGBtn.setOnClickListener()
        {
            basalBGRecommendation=0.5*mTDI

            basalBGRecommendation=java.lang.Double.parseDouble(String.format("%.2f",basalBGRecommendation))

            basalTDITextView.text = mTDI.toString()

            finalString=resources.getString(R.string.recommendationPreTextLiteral) + " " + basalBGRecommendation.toString() + " " + resources.getString(R.string.recommendationEndTextLiteral)

            recommendationBasalTextView.text = finalString


            var newDataInserted: Boolean

            basalBGRootRef.addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    newDataInserted=false

                    if(!dataSnapshot.exists())
                    {
                        val mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,resources.getString(R.string.basalTypeTextLiteral),mWeight,mTDI,basalBGRecommendation,calendarTime)
                        val key:String=basalBGRootRef.push().toString()

                        val parsedKeyList=key.split("/-")
                        var parsedKey=parsedKeyList[1]
                        parsedKey= "-$parsedKey"

                        basalBGRootRef.child(parsedKey).setValue(mLogBasalBGLevel)

                        val basalKeysRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.basalKeysTable))
                        basalKeysRootRef.push().setValue(parsedKey)
                        Toast.makeText(applicationContext,resources.getString(R.string.dataSavedDB),Toast.LENGTH_SHORT).show()
                        newDataInserted=true
                    }

                    else
                    {
                        for (data:DataSnapshot in dataSnapshot.children)
                        {
                            if(!newDataInserted)
                            {
                                val oldDate=data.child(resources.getString(R.string.basalCalendarTimeColumn)).value.toString()
                                val oldEmailID=data.child(resources.getString(R.string.basalEmailColumn)).value.toString()

                                if(oldDate == calendarTime && oldEmailID == currentUserEmailID)
                                {
                                    foundKey=data.key.toString()
                                    isKeyFound=true

                                    val mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,resources.getString(R.string.basalTypeTextLiteral),mWeight,mTDI,basalBGRecommendation,calendarTime)
                                    basalBGRootRef.child(foundKey).setValue(mLogBasalBGLevel)
                                    Toast.makeText(applicationContext, resources.getString(R.string.previousDataOverwrittenDB), Toast.LENGTH_SHORT).show()
                                    newDataInserted=true

                                }
                            }
                        }

                        if(!newDataInserted)
                        {
                            val mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,resources.getString(R.string.basalTypeTextLiteral),mWeight,mTDI,basalBGRecommendation,calendarTime)
                            val key:String=basalBGRootRef.push().toString()

                            val parsedKeyList=key.split("/-")
                            var parsedKey=parsedKeyList[1]
                            parsedKey= "-$parsedKey"

                            basalBGRootRef.child(parsedKey).setValue(mLogBasalBGLevel)

                            val basalKeysRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.basalKeysTable))
                            basalKeysRootRef.push().setValue(parsedKey)
                            Toast.makeText(applicationContext,resources.getString(R.string.dataSavedDB),Toast.LENGTH_SHORT).show()
                            newDataInserted=true
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}

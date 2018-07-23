package com.ciklum.insulinapp.Activities.LogBG

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
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class LogBasalBGActivity : AppCompatActivity() {

    private lateinit var weightTextView:TextView
    private lateinit var basalTDITextView:TextView
    private lateinit var calculateBasalBGBtn:Button
    private lateinit var recommendationBasalTextView:TextView

    private var mWeight:Int=-1
    private var mTDI:Double=0.00
    private var basalBGRecommendation:Double=0.00

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var currentUserEmailID:String

    private lateinit var calendarTime:String


    private var keyList: ArrayList<String> = ArrayList(1000)
    private var foundKey:String=""
    private var isKeyFound:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_basal_bg)

        weightTextView=findViewById(R.id.weightTextView)
        basalTDITextView=findViewById(R.id.basalTDITextView)
        calculateBasalBGBtn=findViewById(R.id.calculateBasalBGBtn)
        recommendationBasalTextView=findViewById(R.id.recommendationBasalTextView)

        mAuth=FirebaseAuth.getInstance()

        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!


        mFirebaseDatabase= FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference= mFirebaseDatabase?.getReference("Basal BG Data")

        val c = java.util.Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("dd-MMM-yyyy")
        val currentTime = df.format(c)

        calendarTime=currentTime.toString()

        var rootRef=FirebaseDatabase.getInstance().getReference("User")

        // Read from the database
        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        var userWeight=data.child("mweight").getValue().toString().trim()
                        weightTextView.setText(userWeight)

                        mWeight=userWeight.toInt()
                        mTDI=0.55*mWeight

                        mTDI = java.lang.Double.parseDouble(String.format("%.2f", mTDI))
                        basalTDITextView.setText(mTDI.toString())

                    }
                }
            }
        })

        calculateBasalBGBtn.setOnClickListener()
        {
            basalBGRecommendation=0.5*mTDI

            basalBGRecommendation=java.lang.Double.parseDouble(String.format("%.2f",basalBGRecommendation))

            basalTDITextView.setText(mTDI.toString())
            recommendationBasalTextView.setText("Your basal Insulin recommendation is " + basalBGRecommendation.toString() + " units")

            var rootRef5=FirebaseDatabase.getInstance().getReference("Basal BG Data")

            var newDataInserted=false

            rootRef5.addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    newDataInserted=false

                    if(dataSnapshot.exists()==false)
                    {
                        var mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,"Basal Insulin",mWeight,mTDI,basalBGRecommendation,calendarTime)
                        var key:String=mFirebaseDatabaseReference.push().toString()

                        var parsedKeyList=key.split("/-")
                        var parsedKey=parsedKeyList[1]
                        parsedKey="-"+parsedKey

                        mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBasalBGLevel)

                        var mFirebaseDatabaseReference2=mFirebaseDatabase.getReference("Basal BG Keys")
                        mFirebaseDatabaseReference2.push().setValue(parsedKey)
                        Toast.makeText(applicationContext,"Your data has been uploaded to the cloud",Toast.LENGTH_SHORT).show()
                        newDataInserted=true
                    }

                    else
                    {
                        for (data:DataSnapshot in dataSnapshot.children)
                        {
                            if(newDataInserted==false)
                            {
                                var oldDate=data.child("calendarTime").getValue().toString()
                                var oldEmailID=data.child("emailID").getValue().toString()

                                if(oldDate.equals(calendarTime) && oldEmailID.equals(currentUserEmailID))
                                {
                                    foundKey=data.key.toString()
                                    isKeyFound=true

                                    var mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,"Basal Insulin",mWeight,mTDI,basalBGRecommendation,calendarTime)
                                    mFirebaseDatabaseReference.child(foundKey).setValue(mLogBasalBGLevel)
                                    Toast.makeText(applicationContext, "Your previous data has been updated with the new one", Toast.LENGTH_SHORT).show()
                                    newDataInserted=true

                                }
                            }
                        }

                        if(newDataInserted==false)
                        {
                            var mLogBasalBGLevel=BasalBGLevel(currentUserEmailID,"Basal Insulin",mWeight,mTDI,basalBGRecommendation,calendarTime)
                            var key:String=mFirebaseDatabaseReference.push().toString()

                            var parsedKeyList=key.split("/-")
                            var parsedKey=parsedKeyList[1]
                            parsedKey="-"+parsedKey

                            mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBasalBGLevel)

                            var mFirebaseDatabaseReference2=mFirebaseDatabase.getReference("Basal BG Keys")
                            mFirebaseDatabaseReference2.push().setValue(parsedKey)
                            Toast.makeText(applicationContext,"Your data has been uploaded to the cloud",Toast.LENGTH_SHORT).show()
                            newDataInserted=true
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

    }
}

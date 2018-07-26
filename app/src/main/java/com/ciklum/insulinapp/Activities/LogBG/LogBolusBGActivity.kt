package com.ciklum.insulinapp.Activities.LogBG

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.ciklum.insulinapp.Models.BolusBGLevel
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat


class LogBolusBGActivity : AppCompatActivity() {

    private lateinit var currentEmailID:String

    private lateinit var currentBGEditText: EditText
    private lateinit var targetBGEditText: EditText
    private lateinit var CHOEditText:EditText
    private lateinit var CHODisposedEditText: EditText
    private lateinit var correctionFactorEditText: EditText
    private lateinit var showInsulinTextView:TextView
    private lateinit var checkInsulinBtn:Button
    private lateinit var radioGroup2:RadioGroup

    private var mEvent:String=""
    private lateinit var currentBG:String
    private lateinit var targetBG:String
    private lateinit var CHO:String
    private lateinit var CHODisposed:String
    private lateinit var correctionFactor:String
    private lateinit var calendarTime:String

    private var currentBGNumber:Int=-1
    private var targetBGNumber:Int=-1
    private var CHONumber:Int=-1
    private var CHODisposedNumber:Int=-1
    private var correctionFactorNumber:Int=-1

    private var mFirebaseUser:FirebaseUser?=null
    private var mAuth:FirebaseAuth?=null
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference:DatabaseReference

    private var keyList: ArrayList<String> = ArrayList(1000)
    private var foundKey:String=""
    private var isKeyFound:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_bolus_bg)

        currentBGEditText=findViewById(R.id.currentBGEditText)
        targetBGEditText=findViewById(R.id.targetBGEditText)
        CHOEditText=findViewById(R.id.CHOEditText)
        CHODisposedEditText=findViewById(R.id.CHODisposedEditText)
        correctionFactorEditText=findViewById(R.id.correctionFactorEditText)
        showInsulinTextView=findViewById(R.id.showInsulinTextView)
        checkInsulinBtn=findViewById(R.id.checkInsulinBtn)
        radioGroup2=findViewById(R.id.radioGroup2)

        mAuth=FirebaseAuth.getInstance()

        mFirebaseUser=mAuth?.currentUser
        currentEmailID= mFirebaseUser?.email!!


        mFirebaseDatabase= FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference= mFirebaseDatabase?.getReference("Bolus BG Data")

        radioGroup2.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            if(i==R.id.radioBreakfast)
            {
                mEvent="Breakfast"
            }
            if(i==R.id.radioLunch)
            {
                mEvent="Lunch"
            }
            if(i==R.id.radioDinner)
            {
                mEvent="Dinner"
            }
        })

        checkInsulinBtn.setOnClickListener()
        {

            if(mEvent=="")
            {
                Toast.makeText(applicationContext,"Please select the event before which you're taking this Insulin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentBG=currentBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(currentBG))
            {
                Toast.makeText(applicationContext,"You haven't entered your current blood glucose level",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var currentBGNum:Int=currentBG.toInt()

            targetBG=targetBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(targetBG))
            {
                Toast.makeText(applicationContext,"You haven't entered your target blood glucose level",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var targetBGNum:Int=targetBG.toInt()


            if(currentBGNum<targetBGNum)
            {
                Toast.makeText(applicationContext,"You don't need to administer Insulin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CHO=CHOEditText.text.toString().trim()
            if(TextUtils.isEmpty(CHO))
            {
                Toast.makeText(applicationContext,"You haven't entered the CHO amount in your food",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var CHONum:Int=CHO.toInt()

            CHODisposed=CHODisposedEditText.text.toString().trim()
            if(TextUtils.isEmpty(CHODisposed))
            {
                Toast.makeText(applicationContext,"You haven't entered the amount of CHO disposed by 1 unit of Insulin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var CHODisposedNum:Int=CHODisposed.toInt()

            correctionFactor=correctionFactorEditText.text.toString().trim()
            if(TextUtils.isEmpty(correctionFactor))
            {
                Toast.makeText(applicationContext,"You haven't entered the correction factor",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var correctionFactorNum=correctionFactor.toInt()

            //Insulin Recommendation Calculation

            var differenceBGLevel:Int=currentBGNum-targetBGNum
            var insulinDose:Int=CHONum/CHODisposedNum
            var highBloodSugarCorrectionDose:Int=differenceBGLevel/correctionFactorNum
            var totalInsulinRecommendation:Int=insulinDose+highBloodSugarCorrectionDose

            var totalInsulinRecommendationString:String=totalInsulinRecommendation.toString().trim()

            showInsulinTextView.setText("You should take " + totalInsulinRecommendationString + " units of Insulin")

            currentBGNumber=currentBG.toInt()
            targetBGNumber=targetBG.toInt()
            CHONumber=CHO.toInt()
            CHODisposedNumber=CHODisposed.toInt()
            correctionFactorNumber=correctionFactor.toInt()

            val c = java.util.Calendar.getInstance().time
            println("Current time => $c")

            val df = SimpleDateFormat("dd-MMM-yyyy")
            val currentTime = df.format(c)

            calendarTime=currentTime.toString()

            var rootRef4=FirebaseDatabase.getInstance().getReference("Bolus BG Data")

            var newDataInserted=false

            rootRef4.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    newDataInserted=false

                    if(dataSnapshot.exists()==false)
                    {
                        var mLogBolusBGLevel=BolusBGLevel(currentEmailID,"Bolus Insulin",mEvent,currentBGNumber,targetBGNumber,CHONumber,CHODisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                        var key:String=mFirebaseDatabaseReference.push().toString()

                        var parsedKeyList=key.split("/-")
                        var parsedKey=parsedKeyList[1]
                        parsedKey="-"+parsedKey

                        mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBolusBGLevel)

                        var mFirebaseDatabaseReference2=mFirebaseDatabase.getReference("Bolus BG Keys")
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
                                var oldBeforeEvent=data.child("beforeEvent").getValue().toString()
                                var oldDate=data.child("calendarTime").getValue().toString()
                                var oldEmailID=data.child("emailID").getValue().toString()

                                if(oldBeforeEvent.equals(mEvent) && oldDate.equals(calendarTime) && oldEmailID.equals(currentEmailID))
                                {
                                    foundKey=data.key.toString()
                                    isKeyFound=true

                                    var mLogBolusBGLevel=BolusBGLevel(currentEmailID,"Bolus Insulin",mEvent,currentBGNumber,targetBGNumber,CHONumber,CHODisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                                    mFirebaseDatabaseReference.child(foundKey).setValue(mLogBolusBGLevel)
                                    Toast.makeText(applicationContext, "Your previous data has been updated with the new one", Toast.LENGTH_SHORT).show()
                                    newDataInserted=true

                                }
                            }
                        }

                        if(newDataInserted==false)
                        {
                            var mLogBolusBGLevel=BolusBGLevel(currentEmailID,"Bolus Insulin",mEvent,currentBGNumber,targetBGNumber,CHONumber,CHODisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                            var key:String=mFirebaseDatabaseReference.push().toString()

                            var parsedKeyList=key.split("/-")
                            var parsedKey=parsedKeyList[1]
                            parsedKey="-"+parsedKey

                            mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBolusBGLevel)

                            var mFirebaseDatabaseReference2=mFirebaseDatabase.getReference("Bolus BG Keys")
                            mFirebaseDatabaseReference2.push().setValue(parsedKey)
                            Toast.makeText(applicationContext,"Your data has been uploaded to the cloud",Toast.LENGTH_SHORT).show()
                            newDataInserted=true
                        }
                    }
                }

                override fun onCancelled(error:DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }
    }
}

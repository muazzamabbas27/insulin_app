package com.ciklum.insulinapp.activitiesPackage.logBGPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.*
import com.ciklum.insulinapp.Models.BolusBGLevel
import com.ciklum.insulinapp.R
import com.ciklum.insulinapp.Utility.InternetUtility
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class LogBolusBGActivity : AppCompatActivity() {


    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var currentBGEditText: EditText
    private lateinit var targetBGEditText: EditText
    private lateinit var choEditText:EditText
    private lateinit var choDisposedEditText: EditText
    private lateinit var correctionFactorEditText: EditText
    private lateinit var showInsulinTextView:TextView
    private lateinit var checkInsulinBtn:Button
    private lateinit var radioGroup2:RadioGroup

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser:FirebaseUser?=null
    private var mAuth:FirebaseAuth?=null
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var bolusBGRootRef:DatabaseReference

    /*-----------------------------------------Bolus data to get from Firebase-------------------------------------------------*/
    private var mEvent:String=""
    private lateinit var currentBG:String
    private lateinit var targetBG:String
    private lateinit var mSugar:String
    private lateinit var choDisposed:String
    private lateinit var correctionFactor:String
    private lateinit var calendarTime:String
    private lateinit var currentEmailID:String

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var currentBGNumber:Int=-1
    private var targetBGNumber:Int=-1
    private var choNumber:Int=-1
    private var choDisposedNumber:Int=-1
    private var correctionFactorNumber:Int=-1
    private var foundKey:String=""
    private var isKeyFound:Boolean=false
    private lateinit var finalString: String
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_bolus_bg)


        /*-----------------Fetching views and initializing data-------------------*/
        currentBGEditText=findViewById(R.id.currentBGEditText)
        targetBGEditText=findViewById(R.id.targetBGEditText)
        choEditText=findViewById(R.id.CHOEditText)
        choDisposedEditText=findViewById(R.id.CHODisposedEditText)
        correctionFactorEditText=findViewById(R.id.correctionFactorEditText)
        showInsulinTextView=findViewById(R.id.showInsulinTextView)
        checkInsulinBtn=findViewById(R.id.checkInsulinBtn)
        radioGroup2=findViewById(R.id.radioGroup2)
        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.basalLoggingActionBarString)
        }



        /*-----------------Fetching Firebase data-------------------*/
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        bolusBGRootRef= mFirebaseDatabase.getReference(resources.getString(R.string.bolusBGDataTable))


        /*-----------------Radio Group implementation-------------------*/
        radioGroup2.setOnCheckedChangeListener { _, i ->
            if(i==R.id.radioBreakfast)
            {
                mEvent=resources.getString(R.string.bolusBreakfastEvent)
            }
            if(i==R.id.radioLunch)
            {
                mEvent=resources.getString(R.string.bolusLunchEvent)
            }
            if(i==R.id.radioDinner)
            {
                mEvent=resources.getString(R.string.bolusDinnerEvent)
            }
        }


        /*-----------------Check bolus Insulin implementation-------------------*/
        checkInsulinBtn.setOnClickListener()
        {

            if(mEvent=="")
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoEventToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentBG=currentBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(currentBG))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoCurrentBGToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentBGNum:Int=currentBG.toInt()

            targetBG=targetBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(targetBG))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoTargetBGToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val targetBGNum:Int=targetBG.toInt()


            if(currentBGNum<targetBGNum)
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoInsulinRequiredToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mSugar=choEditText.text.toString().trim()
            if(TextUtils.isEmpty(mSugar))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoTotalCHOToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val choNum:Int=mSugar.toInt()

            choDisposed=choDisposedEditText.text.toString().trim()
            if(TextUtils.isEmpty(choDisposed))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoCHODisposedToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val choDisposedNum:Int=choDisposed.toInt()

            correctionFactor=correctionFactorEditText.text.toString().trim()
            if(TextUtils.isEmpty(correctionFactor))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.bolusNoCorrectionFactorToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correctionFactorNum=correctionFactor.toInt()

            //Insulin Recommendation Calculation
            val differenceBGLevel:Int=currentBGNum-targetBGNum
            val insulinDose:Int=choNum/choDisposedNum
            val highBloodSugarCorrectionDose:Int=differenceBGLevel/correctionFactorNum
            val totalInsulinRecommendation:Int=insulinDose+highBloodSugarCorrectionDose

            val totalInsulinRecommendationString:String=totalInsulinRecommendation.toString().trim()

            finalString=resources.getString(R.string.bolusRecommendationPreTextLiteral) + " " + totalInsulinRecommendationString + " " + resources.getString(R.string.bolusRecommendationEndTextLiteral)
            showInsulinTextView.text =finalString

            currentBGNumber=currentBG.toInt()
            targetBGNumber=targetBG.toInt()
            choNumber=mSugar.toInt()
            choDisposedNumber=choDisposed.toInt()
            correctionFactorNumber=correctionFactor.toInt()

            val c = java.util.Calendar.getInstance().time

            val df = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            val currentTime = df.format(c)

            calendarTime=currentTime.toString()

            var newDataInserted: Boolean

            bolusBGRootRef.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                    if(!isInternetConnected)
                    {
                        Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB), Toast.LENGTH_SHORT).show()
                        return
                    }

                    newDataInserted=false

                    if(!dataSnapshot.exists())
                    {
                        val mLogBolusBGLevel=BolusBGLevel(currentEmailID,resources.getString(R.string.bolusTypeTextLiteral),mEvent,currentBGNumber,targetBGNumber,choNumber,choDisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                        val key:String=bolusBGRootRef.push().toString()

                        val parsedKeyList=key.split("/-")
                        var parsedKey=parsedKeyList[1]
                        parsedKey= "-$parsedKey"

                       bolusBGRootRef.child(parsedKey).setValue(mLogBolusBGLevel)

                        val bolusKeyRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.bolusKeysTable))
                        bolusKeyRootRef.push().setValue(parsedKey)
                        Toast.makeText(applicationContext,resources.getString(R.string.dataSavedDB),Toast.LENGTH_SHORT).show()
                        newDataInserted=true
                    }

                    else
                    {
                        for (data:DataSnapshot in dataSnapshot.children)
                        {
                            if(!newDataInserted)
                            {
                                val oldBeforeEvent=data.child(resources.getString(R.string.bolusBeforeEventColumn)).value.toString()
                                val oldDate=data.child(resources.getString(R.string.bolusCalendarTimeColumn)).value.toString()
                                val oldEmailID=data.child(resources.getString(R.string.bolusEmailColumn)).value.toString()

                                if(oldBeforeEvent == mEvent && oldDate == calendarTime && oldEmailID == currentEmailID)
                                {
                                    foundKey=data.key.toString()
                                    isKeyFound=true

                                    val mLogBolusBGLevel=BolusBGLevel(currentEmailID,resources.getString(R.string.bolusTypeTextLiteral),mEvent,currentBGNumber,targetBGNumber,choNumber,choDisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                                    bolusBGRootRef.child(foundKey).setValue(mLogBolusBGLevel)
                                    Toast.makeText(applicationContext, resources.getString(R.string.dataOverwrittenDB), Toast.LENGTH_SHORT).show()
                                    newDataInserted=true

                                }
                            }
                        }

                        if(!newDataInserted)
                        {
                            val mLogBolusBGLevel=BolusBGLevel(currentEmailID,resources.getString(R.string.bolusTypeTextLiteral),mEvent,currentBGNumber,targetBGNumber,choNumber,choDisposedNumber,correctionFactorNumber,totalInsulinRecommendation,calendarTime)
                            val key:String=bolusBGRootRef.push().toString()

                            val parsedKeyList=key.split("/-")
                            var parsedKey=parsedKeyList[1]
                            parsedKey= "-$parsedKey"

                            bolusBGRootRef.child(parsedKey).setValue(mLogBolusBGLevel)

                            val bolusKeyRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.bolusKeysTable))
                            bolusKeyRootRef.push().setValue(parsedKey)
                            Toast.makeText(applicationContext,resources.getString(R.string.dataSavedDB),Toast.LENGTH_SHORT).show()
                            newDataInserted=true
                        }
                    }
                }

                override fun onCancelled(error:DatabaseError) {
                    Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

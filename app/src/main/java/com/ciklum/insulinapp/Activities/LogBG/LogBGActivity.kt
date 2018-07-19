package com.ciklum.insulinapp.Activities.LogBG

import android.icu.util.Calendar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.ciklum.insulinapp.Activities.SplashScreen.mDatabaseReference
import com.ciklum.insulinapp.Models.BGLevel
import com.ciklum.insulinapp.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


class LogBGActivity : AppCompatActivity() {

    private var BGLevelEditText: EditText? = null
    private var recentFoodIntakeSpinner: Spinner? = null
    private var recentEventSpinner: Spinner? = null
    private var checkInsulinBtn: Button? = null
    private var showInsulinTextView: TextView? = null

    private lateinit var BGLevel: String
    private var recentFood: String="Food1"
    private var recentEvent:String="Event1"
    private lateinit var calendarTime:String
    private lateinit var emailID:String
    private lateinit var insulinLevel:String

    private var mFirebaseUser:FirebaseUser?=null
    private var mAuth:FirebaseAuth?=null
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference:DatabaseReference

    var keyList: ArrayList<String> = ArrayList(1000)
    private var foundKey:String=""
    private var isKeyFound:Boolean=false
    private var delayMethod:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_bg)


        mAuth=FirebaseAuth.getInstance()

        mFirebaseUser=mAuth?.currentUser
        emailID= mFirebaseUser?.email!!


        mFirebaseDatabase= FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference= mFirebaseDatabase?.getReference("BG Data")

        BGLevelEditText = findViewById(R.id.BGLevelEditText)
        recentFoodIntakeSpinner = findViewById(R.id.recentFoodIntakeSpinner)
        showInsulinTextView=findViewById(R.id.showInsulinTextView)

        val adapter1 = ArrayAdapter.createFromResource(this, R.array.food_array, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recentFoodIntakeSpinner?.setAdapter(adapter1);

        recentFoodIntakeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //On nothing selected, do nothing
                recentFood = "Food1"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                recentFood = selectedItem
            }
        }

        recentEventSpinner = findViewById(R.id.recentEventSpinner)

        val adapter2 = ArrayAdapter.createFromResource(this, R.array.event_array, android.R.layout.simple_spinner_item)

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recentEventSpinner?.setAdapter(adapter2)

        recentEventSpinner?.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                recentEvent="Event1"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem:String=parent?.getItemAtPosition(position).toString()
                recentEvent=selectedItem
            }
        }


        val c = java.util.Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("dd-MMM-yyyy")
        val currentTime = df.format(c)

        insulinLevel="Test"
        checkInsulinBtn=findViewById(R.id.checkInsulinBtn)
        calendarTime=currentTime.toString()

        checkInsulinBtn?.setOnClickListener()
        {
            var isDataValidCheck:Boolean=validateData()
            var newDataInserted=false
            if(isDataValidCheck==true)
            {
                var rootRef4=FirebaseDatabase.getInstance().getReference("BG Data")
                // Read from the database
                rootRef4.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        newDataInserted=false
                        if(dataSnapshot.hasChildren()==false)   //if the DB is not yet created
                        {
                            var mLogBG:BGLevel= BGLevel(emailID,recentFood,recentEvent,BGLevel,insulinLevel,calendarTime)
                            var key:String=mFirebaseDatabaseReference.push().toString()

                            var parsedKeyList=key.split("/-")
                            var parsedKey=parsedKeyList[1]
                            parsedKey="-"+parsedKey

                            mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBG)

                            var mFirebaseDatabaseReference2=mFirebaseDatabase.getReference("BG Keys")
                            mFirebaseDatabaseReference2.push().setValue(parsedKey)
                            Toast.makeText(applicationContext,"Your data has been uploaded to the cloud",Toast.LENGTH_SHORT).show()
                            newDataInserted=true
                        }

                        else
                        {
                            //newDataInserted=false

                            for(data:DataSnapshot in dataSnapshot.children)
                            {
                                if(newDataInserted==false)
                                {
                                    var oldEvent=data.child("recentEvent").getValue().toString()
                                    var oldDate:String=data.child("calendarTime").getValue().toString()
                                    var oldEmailID:String=data.child("emailID").getValue().toString()

                                    if(oldEvent.equals(recentEvent) && oldDate.equals(calendarTime)&& oldEmailID.equals(emailID))
                                    {
                                        foundKey=data.key.toString()
                                        isKeyFound=true

                                        var mLogBG: BGLevel = BGLevel(emailID, recentFood, recentEvent, BGLevel, insulinLevel, calendarTime)
                                        mFirebaseDatabaseReference.child(foundKey).setValue(mLogBG)
                                        Toast.makeText(applicationContext, "Your previous data has been updated with the new one", Toast.LENGTH_SHORT).show()
                                        newDataInserted=true
                                    }
                                    else
                                    {
                                        if(newDataInserted==false) {
                                            var mLogBG: BGLevel = BGLevel(emailID, recentFood, recentEvent, BGLevel, insulinLevel, calendarTime)
                                            var key: String = mFirebaseDatabaseReference.push().toString()

                                            var parsedKeyList = key.split("/-")
                                            var parsedKey = parsedKeyList[1]
                                            parsedKey = "-" + parsedKey

                                            mFirebaseDatabaseReference.child(parsedKey).setValue(mLogBG)

                                            var mFirebaseDatabaseReference2 = mFirebaseDatabase.getReference("BG Keys")
                                            mFirebaseDatabaseReference2.push().setValue(parsedKey)
                                            Toast.makeText(applicationContext, "Your data has been uploaded to the cloud", Toast.LENGTH_SHORT).show()
                                            newDataInserted = true
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                    }
                })
            }
        }
    }

    fun validateData():Boolean
    {
        BGLevel=BGLevelEditText?.text.toString().trim()
        if(TextUtils.isEmpty(BGLevel))
        {
            Toast.makeText(applicationContext,"You haven't entered your blood glucose level",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}

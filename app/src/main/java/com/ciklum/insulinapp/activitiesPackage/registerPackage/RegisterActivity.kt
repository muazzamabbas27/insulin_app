package com.ciklum.insulinapp.activitiesPackage.registerPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import com.ciklum.insulinapp.activitiesPackage.dashboardPackage.DashboardActivity
import com.ciklum.insulinapp.R
import android.widget.Toast
import android.text.TextUtils
import android.view.MenuItem
import com.ciklum.insulinapp.Models.User
import com.ciklum.insulinapp.Utility.InternetUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity(){

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var ageSpinner: Spinner
    private lateinit var registerConfirmBtn: Button
    private lateinit var emailIDEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var confirmPasswordEditText:EditText
    private lateinit var nameEditText:EditText
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var myContext:Context

    /*-----------------------------------------User data to store in Firebase-------------------------------------------------*/
    private var mAuth: FirebaseAuth?=null
    private lateinit var emailID:String
    private lateinit var mName:String
    private lateinit var mPassword:String
    private lateinit var confirmPassword:String
    private var mAge:Int=-1
    private var mWeight:Int=-1
    private var mHeight:Int=-1
    private lateinit var mGender:String

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mDatabaseReference:DatabaseReference

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var mUser:User?=null
    private var isInternetConnected:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /*-----------------Fetching views and initializing data-------------------*/
        ageSpinner=findViewById(R.id.ageSpinner)
        registerConfirmBtn=findViewById(R.id.registerConfirmBtn)
        emailIDEditText=findViewById(R.id.emailIDEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        confirmPasswordEditText=findViewById(R.id.confirmPasswordEditText)
        nameEditText=findViewById(R.id.nameEditText)
        weightEditText=findViewById(R.id.weightEditText)
        heightEditText=findViewById(R.id.heightEditText)
        myContext=applicationContext
        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.registerActionBarString)
        }


        /*-----------------Spinner implementation-------------------*/
        val adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = adapter

        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //On nothing selected, do nothing
                mAge=5
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem=parent?.getItemAtPosition(position).toString()
                mAge=selectedItem.toInt()
            }
        }

        /*-----------------Register button implementation-------------------*/
        registerConfirmBtn.setOnClickListener()
        {
            val finalCheck=checkInfo()

            if(!finalCheck)
            {
                //Do nothing, error has already been shown
            }

            else
            {
                val i=Intent(this,DashboardActivity::class.java)

                isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                if(!isInternetConnected)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB), Toast.LENGTH_SHORT).show()
                }


                val emailFormat=isValidEmail(emailID)
                if(!emailFormat)
                {
                    Toast.makeText(this,resources.getString(R.string.registerIncorrectEmailFormat),Toast.LENGTH_SHORT).show()
                }

                if(emailFormat && isInternetConnected)
                {
                    mAuth?.createUserWithEmailAndPassword(emailID, mPassword)
                            ?.addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = mAuth?.currentUser
                                    Toast.makeText(this,resources.getString(R.string.authenticationSuccessfulTextLiteral),Toast.LENGTH_SHORT).show()
                                    mDatabaseReference = mDatabase.getReference(resources.getString(R.string.userTable))
                                    mDatabaseReference= mDatabaseReference.child(user?.uid)
                                    mUser=User(emailID,mName,mAge,mGender,false,mWeight,mHeight)
                                    mDatabaseReference.setValue(mUser)
                                    startActivity(i)
                                    finish()
                                } else {
                                    Toast.makeText(this, resources.getString(R.string.authenticationFailedTextLiteral), Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            }
        }
    }


    /*-----------------Radio button implementation-------------------*/
    fun onRadioButtonClicked(view: View) {
        mGender = when (view.id) {
            R.id.radioMale ->{
                resources.getString(R.string.genderMaleTextLiteral)
            }
            R.id.radioFemale ->{
                resources.getString(R.string.genderFemaleTextLiteral)
            }
            else->{
                ""
            }
        }
    }

    /*-----------------Utility function-------------------*/
    private fun checkInfo():Boolean
    {
        emailID=emailIDEditText.text.toString().trim()
        if(TextUtils.isEmpty(emailID))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoEmailIDToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }

        mPassword=passwordEditText.text.toString().trim()
        if(TextUtils.isEmpty(mPassword))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoPasswordToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }

        confirmPassword=confirmPasswordEditText.text.toString().trim()
        if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoAgainPasswordToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }


        if(mPassword != confirmPassword)
        {
            Toast.makeText(this,resources.getString(R.string.registerNoPasswordMatchToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }

        mName=nameEditText.text.toString().trim()
        if(TextUtils.isEmpty(mName))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoNameToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }


        if(mGender == "")
        {
            Toast.makeText(this,resources.getString(R.string.registerNoGenderToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }

        val strWeight=weightEditText.text.toString().trim()
        if(TextUtils.isEmpty(strWeight))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoWeightToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }
        else
        {
            mWeight=strWeight.toInt()
        }

        val strHeight=heightEditText.text.toString().trim()
        if(TextUtils.isEmpty(strHeight))
        {
            Toast.makeText(this,resources.getString(R.string.registerNoHeightToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }
        else
        {
            mHeight=strHeight.toInt()
        }

        return true
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /*-----------------Options menu implementation-------------------*/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

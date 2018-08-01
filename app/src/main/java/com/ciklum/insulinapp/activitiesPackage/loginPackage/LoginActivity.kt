package com.ciklum.insulinapp.activitiesPackage.loginPackage

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ciklum.insulinapp.activitiesPackage.dashboardPackage.DashboardActivity
import com.ciklum.insulinapp.R
import android.view.MenuItem
import com.ciklum.insulinapp.Utility.InternetUtility
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity(){

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var emailIDEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInBtn: Button
    private lateinit var emailID:String
    private lateinit var mPassword:String

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mAuth: FirebaseAuth?=null

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*-----------------Fetching views and initializing data-------------------*/
        emailIDEditText=findViewById(R.id.emailIDEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        signInBtn=findViewById(R.id.signInBtn)
        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.loginActionBarString)
        }


        /*-----------------Sign in button implementation-------------------*/
        signInBtn.setOnClickListener()
        {
            val finalCheck=validateData()
            isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

            if(!isInternetConnected)
            {
                Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB), Toast.LENGTH_SHORT).show()
            }

            if(finalCheck && isInternetConnected)
            {
                mAuth?.signInWithEmailAndPassword(emailID,mPassword)
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, resources.getString(R.string.authenticationSuccessfulTextLiteral), Toast.LENGTH_SHORT).show()
                                val i=Intent(this,DashboardActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                Toast.makeText(this, resources.getString(R.string.authenticationFailedTextLiteral), Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }

    /*-----------------Data Validation functions-------------------*/
    private fun checkEmailID():Boolean
    {
        emailID=emailIDEditText.text.toString().trim()
        if(TextUtils.isEmpty(emailID))
        {
            Toast.makeText(this,resources.getString(R.string.loginNoEmailToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPassword():Boolean
    {
        mPassword=passwordEditText.text.toString().trim()
        if(TextUtils.isEmpty(mPassword))
        {
            Toast.makeText(this,resources.getString(R.string.loginNoPasswordToastTextLiteral),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateData():Boolean
    {
        val emailValidation=checkEmailID()

        if(!emailValidation)
        {
            return false
        }


        val passwordValidation=checkPassword()

        if(!passwordValidation)
        {
            return false
        }
        return true
    }

    /*-----------------Options Menu implementation-------------------*/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

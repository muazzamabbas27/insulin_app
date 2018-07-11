package com.ciklum.insulinapp.Activities.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ciklum.insulinapp.Activities.Dashboard.DashboardActivity
import com.ciklum.insulinapp.Activities.SplashScreen.currentUser
import com.ciklum.insulinapp.Activities.SplashScreen.mAuth
import com.ciklum.insulinapp.R
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.net.ConnectivityManager
import android.view.View


class LoginActivity : AppCompatActivity(), View.OnClickListener{
    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    lateinit var emailIDEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var signInBtn: Button
    lateinit var emailID:String;
    lateinit var Password:String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailIDEditText=findViewById(R.id.emailIDEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        signInBtn=findViewById(R.id.signInBtn)

        signInBtn.setOnClickListener()
        {
            var finalCheck=validateData()
            var isNetworkCheck=isNetworkAvailable()

            if(isNetworkCheck==false)
            {
                Toast.makeText(this,"No internet available",Toast.LENGTH_SHORT).show();
            }

            if(finalCheck==true && isNetworkCheck==true)
            {
                mAuth?.signInWithEmailAndPassword(emailID, Password)
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show()
                                currentUser = mAuth?.getCurrentUser()
                                val i:Intent=Intent(this,DashboardActivity::class.java)
                                startActivity(i)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun checkEmailID():Boolean
    {
        emailID=emailIDEditText.text.toString().trim()
        if(TextUtils.isEmpty(emailID))
        {
            Toast.makeText(this,"Please enter your email ID",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun checkPassword():Boolean
    {
        Password=passwordEditText.text.toString().trim()
        if(TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun validateData():Boolean
    {
        var emailValidation=checkEmailID()

        if(emailValidation==false)
        {
            return false
        }


        var passwordValidation=checkPassword()

        if(passwordValidation==false)
        {
            return false
        }
        return true
    }
}

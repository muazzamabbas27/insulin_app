package com.ciklum.insulinapp.Activities.Register

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import android.widget.RadioButton
import com.ciklum.insulinapp.Activities.Dashboard.DashboardActivity
import com.ciklum.insulinapp.Activities.SplashScreen.mAuth
import com.ciklum.insulinapp.R
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.R.attr.password
import android.net.ConnectivityManager
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.util.Log
import com.ciklum.insulinapp.Activities.SplashScreen.mDatabase
import com.ciklum.insulinapp.Activities.SplashScreen.mDatabaseReference
import com.ciklum.insulinapp.Models.User
import org.w3c.dom.Text


class RegisterActivity : AppCompatActivity(){

    //Declaration
    lateinit var ageSpinner: Spinner
    lateinit var registerConfirmBtn: Button
    lateinit var emailIDEditText:EditText
    lateinit var passwordEditText:EditText
    lateinit var confirmPasswordEditText:EditText
    lateinit var nameEditText:EditText
    lateinit var myContext:Context

    //Attributes for User object
    lateinit var emailID:String
    lateinit var Name:String
    lateinit var Password:String
    lateinit var confirmPassword:String
    var Age:Int=-1
    lateinit var Gender:String


    var mUser:User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ageSpinner=findViewById(R.id.ageSpinner)
        registerConfirmBtn=findViewById(R.id.registerConfirmBtn)
        emailIDEditText=findViewById(R.id.emailIDEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        confirmPasswordEditText=findViewById(R.id.confirmPasswordEditText)
        nameEditText=findViewById(R.id.nameEditText)
        myContext=applicationContext

        val adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);

        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //On nothing selected, do nothing
                Age=5
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem=parent?.getItemAtPosition(position).toString()
                if(selectedItem!=null)                  //if an item is selected
                {
                    Age=selectedItem.toInt()            //convert it to an integer and save in variable
                }
            }
        }

        registerConfirmBtn.setOnClickListener()
        {
            var finalCheck=checkInfo()

            if(finalCheck==false)
            {
                //Do nothing, error has already been shown
            }

            else
            {
                val i:Intent=Intent(this,DashboardActivity::class.java)

                var isNetworkCheck=isNetworkAvailable()

                if(isNetworkCheck==false)
                {
                    Toast.makeText(this,"No internet available",Toast.LENGTH_SHORT).show();
                }


                var emailFormat=isValidEmail(emailID)
                if(emailFormat==false)
                {
                    Toast.makeText(this,"Your email ID is not in the correct format",Toast.LENGTH_SHORT).show()
                }

                if(emailFormat==true && isNetworkCheck==true)
                {
                    mAuth?.createUserWithEmailAndPassword(emailID, Password)
                            ?.addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    val user = mAuth?.getCurrentUser()
                                    Toast.makeText(this,"Authentication successful!",Toast.LENGTH_SHORT).show()
                                    mDatabaseReference = mDatabase?.getReference("User")
                                    mDatabaseReference= mDatabaseReference?.child(user?.uid)
                                    mUser=User(emailID,Name,Age,Gender,false)
                                    mDatabaseReference?.setValue(mUser)
                                    startActivity(i)
                                    finish()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show()
                                }
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


    //Radio button listener
    fun onRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.radioMale ->{
                Gender="Male"
            }
            R.id.radioFemale ->{
                Gender="Female"
            }
            else->{
                Gender=""
            }
        }
    }

    //Checks that correct info is entered//



    fun checkInfo():Boolean
    {
        emailID=emailIDEditText.text.toString().trim()
        if(TextUtils.isEmpty(emailID))
        {
            Toast.makeText(this,"You haven't entered your email ID",Toast.LENGTH_SHORT).show()
            return false
        }

        Password=passwordEditText.text.toString().trim()
        if(TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"You haven't entered your password",Toast.LENGTH_SHORT).show()
            return false
        }

        confirmPassword=confirmPasswordEditText.text.toString().trim()
        if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this,"You haven't re-entered your password",Toast.LENGTH_SHORT).show()
            return false
        }


        if(Password.equals(confirmPassword)==false)
        {
            Toast.makeText(this,"Your passwords don't match",Toast.LENGTH_SHORT).show()
            return false
        }

        Name=nameEditText.text.toString().trim()
        if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(this,"You haven't entered your name",Toast.LENGTH_SHORT).show()
            return false
        }


        if(Gender.equals(""))
        {
            Toast.makeText(this,"You haven't selected your gender",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    //Function to check if email ID is valid or not in terms of format
    //uses Android's built in function

    fun isValidEmail(target: CharSequence?): Boolean {

        //validation check to see if email ID is valid
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

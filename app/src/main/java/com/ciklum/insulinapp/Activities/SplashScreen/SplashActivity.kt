package com.ciklum.insulinapp.Activities.SplashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.ciklum.insulinapp.Activities.Dashboard.DashboardActivity
import com.ciklum.insulinapp.Activities.Menu.MenuActivity
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


var mAuth: FirebaseAuth? = null
var mDatabase:FirebaseDatabase?=null
var mDatabaseReference:DatabaseReference?=null
var currentUser: FirebaseUser?=null
var splashProgressBar: ProgressBar?=null
private val SPLASH_DISPLAY_LENGTH:Long = 2000


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth =FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()
        mDatabaseReference= mDatabase?.getReference("User")


        splashProgressBar =findViewById(R.id.splashProgressBar)


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Handler().postDelayed(Runnable {
            currentUser = mAuth?.getCurrentUser()

            if (currentUser == null) {
                val i: Intent = Intent(this, MenuActivity::class.java)
                startActivity(i)
                finish()
            }

            else
            {
                val i:Intent=Intent(this, DashboardActivity::class.java)
                startActivity(i)
                finish()
            }

            this.finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}

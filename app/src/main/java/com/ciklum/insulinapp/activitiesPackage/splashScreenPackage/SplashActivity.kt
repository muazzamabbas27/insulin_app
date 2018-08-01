package com.ciklum.insulinapp.activitiesPackage.splashScreenPackage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.ciklum.insulinapp.activitiesPackage.dashboardPackage.DashboardActivity
import com.ciklum.insulinapp.activitiesPackage.menuPackage.MenuActivity
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class SplashActivity : AppCompatActivity() {

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mAuth: FirebaseAuth? = null
    private var mDatabase:FirebaseDatabase?=null
    private var mDatabaseReference:DatabaseReference?=null
    private var currentUser: FirebaseUser?=null

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var splashProgressBar: ProgressBar?=null
    private val splashDisplayLength:Long = 2000

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /*-----------------Fetching views and initializing data-------------------*/
        splashProgressBar =findViewById(R.id.splashProgressBar)
        if(supportActionBar!=null)
        {
            supportActionBar!!.title = resources.getString(R.string.splashActionBarString)
        }
        mAuth =FirebaseAuth.getInstance()
        mDatabase= FirebaseDatabase.getInstance()
        mDatabaseReference= mDatabase?.getReference(resources.getString(R.string.userTable))


        /*-----------------Splash screen implementation-------------------*/
        Handler().postDelayed({
            currentUser = mAuth?.currentUser

            if (currentUser == null) {
                val i = Intent(this, MenuActivity::class.java)
                startActivity(i)
                finish()
            }

            else
            {
                val i=Intent(this, DashboardActivity::class.java)
                startActivity(i)
                finish()
            }

            this.finish()
        }, splashDisplayLength)
    }
}

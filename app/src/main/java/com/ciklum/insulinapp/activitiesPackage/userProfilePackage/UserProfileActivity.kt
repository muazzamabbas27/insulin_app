package com.ciklum.insulinapp.activitiesPackage.userProfilePackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.ciklum.insulinapp.R
import com.ciklum.insulinapp.Utility.InternetUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserProfileActivity : AppCompatActivity() {

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var nameTextView:TextView
    private lateinit var ageTextView:TextView
    private lateinit var emailIDTextView:TextView
    private lateinit var genderTextView:TextView
    private lateinit var weightTextView:TextView
    private lateinit var heightTextView: TextView
    private lateinit var progressBar3:ProgressBar

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var userRootRef:DatabaseReference

    /*-----------------------------------------User data to get from Firebase-------------------------------------------------*/
    private lateinit var currentUserEmailID:String

    /*-----------------------------------------Local data variables-------------------------------------------------*/
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        /*-----------------Fetching views and initializing data-------------------*/
        nameTextView=findViewById(R.id.nameDashboardTextView)
        ageTextView=findViewById(R.id.ageDashboardTextView)
        emailIDTextView=findViewById(R.id.emailIDTextView)
        genderTextView=findViewById(R.id.genderTextView)
        weightTextView=findViewById(R.id.weightTextView)
        heightTextView=findViewById(R.id.heightTextView)
        progressBar3=findViewById(R.id.progressBar3)
        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.profileActionBarString)
        }
        progressBar3.visibility = View.VISIBLE
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        userRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.userTable))

        /*-----------------Reading user data from Firebase-------------------*/
        userRootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                if(!isInternetConnected)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB), Toast.LENGTH_SHORT).show()
                    return
                }
                for (data: DataSnapshot in dataSnapshot.children) {
                    val currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        val userName = data.child(resources.getString(R.string.userNameColumn)).value.toString().trim()
                        val userAge = data.child(resources.getString(R.string.userAgeColumn)).value.toString().trim()
                        val userEmailID = data.child(resources.getString(R.string.userEmailIDColumn)).value.toString().trim()
                        val userGender = data.child(resources.getString(R.string.userGenderColumn)).value.toString().trim()
                        val userWeight=data.child(resources.getString(R.string.userWeightColumn)).value.toString().trim()
                        val userHeight:String =data.child(resources.getString(R.string.userHeightColumn)).value.toString().trim()

                        nameTextView.text = userName
                        ageTextView.text = userAge
                        emailIDTextView.text = userEmailID
                        genderTextView.text = userGender
                        weightTextView.text = userWeight
                        heightTextView.text = userHeight
                        progressBar3.visibility = View.INVISIBLE
                    }
                }
            }
        })
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

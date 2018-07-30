package com.ciklum.insulinapp.activitiesPackage.UserProfile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView:TextView
    private lateinit var ageTextView:TextView
    private lateinit var emailIDTextView:TextView
    private lateinit var genderTextView:TextView
    private lateinit var weightTextView:TextView
    private lateinit var heightTextView: TextView

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var currentUserEmailID:String

    private lateinit var progressBar3:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        nameTextView=findViewById(R.id.nameDashboardTextView)
        ageTextView=findViewById(R.id.ageDashboardTextView)
        emailIDTextView=findViewById(R.id.emailIDTextView)
        genderTextView=findViewById(R.id.genderTextView)
        weightTextView=findViewById(R.id.weightTextView)
        heightTextView=findViewById(R.id.heightTextView)
        progressBar3=findViewById(R.id.progressBar3)


        progressBar3.setVisibility(View.VISIBLE)
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!

        mFirebaseDatabase= FirebaseDatabase.getInstance()

        var rootRef=FirebaseDatabase.getInstance().getReference("User")

        // Read from the database
        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data: DataSnapshot in dataSnapshot.getChildren()) {
                    var currentUserID = mFirebaseUser?.uid

                    if (currentUserID == data.key) {
                        var userName = data.child("mname").getValue().toString().trim()
                        var userAge = data.child("mage").getValue().toString().trim()
                        var userEmailID = data.child("emailID").getValue().toString().trim()
                        var userGender = data.child("mgender").getValue().toString().trim()
                        var userWeight=data.child("mweight").getValue().toString().trim()
                        var userHeight=data.child("mheight").getValue().toString().trim()

                        nameTextView.setText(userName)
                        ageTextView.setText(userAge)
                        emailIDTextView.setText(userEmailID)
                        genderTextView.setText(userGender)
                        weightTextView.setText(userWeight)
                        heightTextView.setText(userHeight)
                        progressBar3.setVisibility(View.INVISIBLE)
                    }
                }
            }
        })

    }
}

package com.ciklum.insulinapp.Activities.Doctors

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.Manifest.permission.CALL_PHONE
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.Uri
import com.ciklum.insulinapp.Manifest


class DoctorActivity : AppCompatActivity() {

    private lateinit var doctorNameTextView:TextView
    private lateinit var doctorPhoneNumBtn:Button
    private lateinit var addOrSwitchDoctorBtn:ImageButton
    private lateinit var addOrSwitchDoctorTextView: TextView

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth:FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var currentUserEmailID:String

    private var doctorDataFound:Boolean=false

    private val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        doctorNameTextView=findViewById(R.id.doctorNameTextView)
        doctorPhoneNumBtn=findViewById(R.id.doctorPhoneNumBtn)
        addOrSwitchDoctorBtn=findViewById(R.id.addOrSwitchDoctorBtn)
        addOrSwitchDoctorTextView=findViewById(R.id.addOrSwitchDoctorTextView)

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!

        mFirebaseDatabase= FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference= mFirebaseDatabase?.getReference("Doctor Data")

        var rootRef=FirebaseDatabase.getInstance().getReference("Doctor Data")


        // Read from the database
        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data:DataSnapshot in dataSnapshot.getChildren())
                {
                    var currentUserID=mFirebaseUser?.uid

                    if(currentUserID==data.key)

                    //if(emailIDDB.equals(currentUserEmailID))
                    {
                        var myDoctorName=data.child("mname").getValue().toString().trim()
                        var myDoctorPhoneNum=data.child("phoneNum").getValue().toString().trim()
                        doctorDataFound=true
                        doctorNameTextView.setText(myDoctorName)
                        doctorPhoneNumBtn.setText(myDoctorPhoneNum)
                        addOrSwitchDoctorTextView.setText("Switch doctor")
                    }
                }

                if(doctorDataFound==false)
                {
                    doctorNameTextView.setText("You haven't added a doctor yet")
                    doctorPhoneNumBtn.setText("No number to call")
                    addOrSwitchDoctorTextView.setText("Add doctor")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

        addOrSwitchDoctorBtn.setOnClickListener()
        {
            val i:Intent=Intent(this,AddDoctorActivity::class.java)
            startActivity(i)
        }

        doctorPhoneNumBtn.setOnClickListener()
        {


            if(doctorPhoneNumBtn.text.toString().trim().equals("No number to call"))
            {
                Toast.makeText(this,"You haven't added a doctor yet",Toast.LENGTH_SHORT).show()
            }

            else
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)

                }

                else
                {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:" +doctorPhoneNumBtn.text.toString().trim() )
                    startActivity(callIntent)
                }
            }
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        val callIntent = Intent(Intent.ACTION_DIAL)

        callIntent.setData(Uri.parse("tel:" +doctorPhoneNumBtn.text.toString().trim()));

        when (requestCode) {
            REQUEST_PHONE_CALL -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent)
                } else {

                }
                return
            }
        }
    }
}

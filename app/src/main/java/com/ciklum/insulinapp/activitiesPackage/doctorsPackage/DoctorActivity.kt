package com.ciklum.insulinapp.activitiesPackage.doctorsPackage

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
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.Uri


class DoctorActivity : AppCompatActivity() {

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var doctorNameTextView:TextView
    private lateinit var doctorPhoneNumBtn:Button
    private lateinit var addOrSwitchDoctorBtn:ImageButton
    private lateinit var addOrSwitchDoctorTextView: TextView

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth:FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var doctorRootRef: DatabaseReference

    /*-----------------------------------------Data to fetch from Firebase-------------------------------------------------*/
    private lateinit var currentUserEmailID:String


    /*-----------------------------------------Local data variables-------------------------------------------------*/
    private var doctorDataFound:Boolean=false
    private val requestPhoneCall = 1


    /*-----------------------------------------Main code-------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        /*-----------------Fetching views and initializing data-------------------*/

        doctorNameTextView=findViewById(R.id.doctorNameTextView)
        doctorPhoneNumBtn=findViewById(R.id.doctorPhoneNumBtn)
        addOrSwitchDoctorBtn=findViewById(R.id.addOrSwitchDoctorBtn)
        addOrSwitchDoctorTextView=findViewById(R.id.addOrSwitchDoctorTextView)


        /*-----------------Fetching Firebase data-------------------*/

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        doctorRootRef= mFirebaseDatabase.getReference(resources.getString(R.string.doctorTable))


        /*-----------------Load doctor data when activity starts-------------------*/

        doctorRootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data:DataSnapshot in dataSnapshot.children)
                {
                    val currentUserID=mFirebaseUser?.uid

                    if(currentUserID==data.key)
                    {
                        val myDoctorName=data.child(resources.getString(R.string.doctorNameColumn)).value.toString().trim()
                        val myDoctorPhoneNum=data.child(resources.getString(R.string.doctorPhoneColumn)).value.toString().trim()
                        doctorDataFound=true
                        doctorNameTextView.text = myDoctorName
                        doctorPhoneNumBtn.text = myDoctorPhoneNum
                        addOrSwitchDoctorTextView.text = resources.getString(R.string.doctorSwitchTextLiteral)
                    }
                }

                if(!doctorDataFound)
                {
                    doctorNameTextView.text = resources.getString(R.string.doctorNoDoctorAddedTextLiteral)
                    doctorPhoneNumBtn.text = resources.getString(R.string.doctorNoNumberTextLiteral)
                    addOrSwitchDoctorTextView.text = resources.getString(R.string.doctorAddTextLiteral)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_SHORT).show()
            }
        })

        /*-----------------Change doctor implementation-------------------*/

        addOrSwitchDoctorBtn.setOnClickListener()
        {
            val i=Intent(this,AddDoctorActivity::class.java)
            startActivity(i)
        }


        /*-----------------Call doctor implementation-------------------*/

        doctorPhoneNumBtn.setOnClickListener()
        {
            if(doctorPhoneNumBtn.text.toString().trim() == resources.getString(R.string.doctorNoNumberTextLiteral))
            {
                Toast.makeText(this,resources.getString(R.string.doctorNoDoctorAddedTextLiteral),Toast.LENGTH_SHORT).show()
            }

            else
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), requestPhoneCall)
                }

                else
                {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse(resources.getString(R.string.callIntentTextLiteral) +doctorPhoneNumBtn.text.toString().trim() )
                    startActivity(callIntent)
                }
            }
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        val callIntent = Intent(Intent.ACTION_DIAL)

        callIntent.data = Uri.parse(resources.getString(R.string.callIntentTextLiteral) +doctorPhoneNumBtn.text.toString().trim())

        when (requestCode) {
            requestPhoneCall -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent)
                } else {

                }
                return
            }
        }
    }
}

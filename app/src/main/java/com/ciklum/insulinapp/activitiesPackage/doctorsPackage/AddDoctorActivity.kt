package com.ciklum.insulinapp.activitiesPackage.doctorsPackage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ciklum.insulinapp.Models.Doctor
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDoctorActivity : AppCompatActivity() {

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var doctorNameEditText:EditText
    private lateinit var doctorPhoneNumEditText:EditText
    private lateinit var saveDoctorInfoBtn:Button

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var doctorRootRef: DatabaseReference

    /*-----------------------------------------Data to get from Firebase-------------------------------------------------*/
    private lateinit var currentUserEmailID:String
    private lateinit var doctorName:String
    private lateinit var doctorPhoneNum:String

    /*-----------------------------------------Main code-------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)

        /*-----------------Fetching views and initializing data-------------------*/

        doctorNameEditText=findViewById(R.id.doctorNameEditText)
        doctorPhoneNumEditText=findViewById(R.id.doctorPhoneNumEditText)
        saveDoctorInfoBtn=findViewById(R.id.saveDoctorInfoBtn)

        /*-----------------Fetching Firebase data-------------------*/

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        doctorRootRef= mFirebaseDatabase.getReference(resources.getString(R.string.doctorTable))


        /*-----------------Saving doctor data implementation-------------------*/

        saveDoctorInfoBtn.setOnClickListener()
        {
            doctorName=doctorNameEditText.text.toString().trim()
            if(TextUtils.isEmpty(doctorName))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.doctorNoNameToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            doctorPhoneNum=doctorPhoneNumEditText.text.toString().trim()
            if(TextUtils.isEmpty(doctorPhoneNum))
            {
                Toast.makeText(applicationContext,resources.getString(R.string.doctorNoNumberToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(doctorPhoneNum.length!=11)
            {
                Toast.makeText(applicationContext,resources.getString(R.string.doctorWrongFormatToastTextLiteral),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Toast.makeText(applicationContext,resources.getString(R.string.dataSavedDB),Toast.LENGTH_SHORT).show()
            val mDoctor = Doctor(currentUserEmailID,doctorName,doctorPhoneNum)
            val currentUID= mFirebaseUser!!.uid
            doctorRootRef= doctorRootRef.child(currentUID)
            doctorRootRef.setValue(mDoctor)
            val i= Intent(applicationContext,DoctorActivity::class.java)
            startActivity(i)
            finish()
        }
    }


}

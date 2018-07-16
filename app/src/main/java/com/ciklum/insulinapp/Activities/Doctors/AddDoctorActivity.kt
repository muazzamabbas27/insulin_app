package com.ciklum.insulinapp.Activities.Doctors

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

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var doctorNameEditText:EditText
    private lateinit var doctorPhoneNumEditText:EditText
    private lateinit var saveDoctorInfoBtn:Button

    private lateinit var currentUserEmailID:String

    private lateinit var doctorName:String
    private lateinit var doctorPhoneNum:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!

        mFirebaseDatabase= FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference= mFirebaseDatabase?.getReference("Doctor Data")

        doctorNameEditText=findViewById(R.id.doctorNameEditText)
        doctorPhoneNumEditText=findViewById(R.id.doctorPhoneNumEditText)
        saveDoctorInfoBtn=findViewById(R.id.saveDoctorInfoBtn)

        saveDoctorInfoBtn.setOnClickListener()
        {
            doctorName=doctorNameEditText.text.toString().trim()
            if(TextUtils.isEmpty(doctorName))
            {
                Toast.makeText(applicationContext,"You haven't entered a name for your doctor",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            doctorPhoneNum=doctorPhoneNumEditText.text.toString().trim()
            if(TextUtils.isEmpty(doctorPhoneNum))
            {
                Toast.makeText(applicationContext,"You haven't entered your doctor's phone number",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(doctorPhoneNum.length!=11)
            {
                Toast.makeText(applicationContext,"The format of the phone number is incorrect, it should be like: 12345678910",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Toast.makeText(applicationContext,"Your doctor's information has been saved to our database",Toast.LENGTH_SHORT).show()
            var mDoctor = Doctor(currentUserEmailID,doctorName,doctorPhoneNum)
            val currentUID= mFirebaseUser!!.uid
            mFirebaseDatabaseReference= mFirebaseDatabaseReference?.child(currentUID)
            mFirebaseDatabaseReference.setValue(mDoctor)
            val i:Intent= Intent(applicationContext,DoctorActivity::class.java)
            startActivity(i)
            finish()
        }
    }


}

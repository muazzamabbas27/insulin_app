package com.ciklum.insulinapp.Activities.UserProfile

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.widget.ArrayAdapter
import com.ciklum.insulinapp.Activities.SplashScreen.mDatabaseReference


class EditUserProfileActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageSpinner: Spinner
    private lateinit var saveEditsBtn:Button

    var Age:Int=-1
    lateinit var Gender:String

    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var currentUserEmailID:String

    private var mAge:Int=-1
    private lateinit var mName:String
    private var mGender:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        nameEditText=findViewById(R.id.nameEditText)
        ageSpinner=findViewById(R.id.ageSpinner)
        saveEditsBtn=findViewById(R.id.saveEditsBtn)

        val adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);


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
                        var userGender = data.child("mgender").getValue().toString().trim()

                        mName=userName
                        mAge=userAge.toInt()
                        mGender=userGender


                        if(mGender=="Male")
                        {
                            val checkGender:RadioButton=findViewById(R.id.radioMale)
                            checkGender.isChecked=true
                        }

                        else
                        {
                            val checkGender:RadioButton=findViewById(R.id.radioFemale)
                            checkGender.isChecked=true
                        }

                        ageSpinner.setSelection(mAge-5)
                        nameEditText.setText(mName)
                    }
                }
            }
        })

        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //On nothing selected, do nothing
                mAge = 5
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem != null)                  //if an item is selected
                {
                    mAge = selectedItem.toInt()            //convert it to an integer and save in variable
                }
            }
        }

        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!

        mFirebaseDatabase= FirebaseDatabase.getInstance()


        saveEditsBtn.setOnClickListener()
        {
            mName=nameEditText.text.toString().trim()
            if(TextUtils.isEmpty(mName))
            {
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(mGender=="")
            {
                Toast.makeText(this,"Please select your gender",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("User").child(mFirebaseUser!!.uid)

            mFirebaseDatabaseReference?.child("mname")?.setValue(mName)
            mFirebaseDatabaseReference?.child("mage")?.setValue(mAge)
            mFirebaseDatabaseReference?.child("mgender")?.setValue(mGender)

            Toast.makeText(this,"Your data has been updated",Toast.LENGTH_SHORT).show()

            val i= Intent(this,UserProfileActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    //Radio button listener
    fun onRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.radioMale ->{
                mGender="Male"
            }
            R.id.radioFemale ->{
                mGender="Female"
            }
            else->{
                mGender=""
            }
        }
    }
}

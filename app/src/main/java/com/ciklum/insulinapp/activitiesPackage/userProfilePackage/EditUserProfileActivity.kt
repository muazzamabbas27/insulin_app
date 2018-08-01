package com.ciklum.insulinapp.activitiesPackage.userProfilePackage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.widget.ArrayAdapter
import com.ciklum.insulinapp.Utility.InternetUtility


class EditUserProfileActivity : AppCompatActivity() {

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private lateinit var nameEditText: EditText
    private lateinit var ageSpinner: Spinner
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var saveEditsBtn:Button
    var progressBar2: ProgressBar?=null

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private var mFirebaseUser: FirebaseUser?=null
    private var mAuth: FirebaseAuth?=null
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var userRootRef:DatabaseReference

    /*-----------------------------------------User data to get from Firebase-------------------------------------------------*/
    private lateinit var currentUserEmailID:String
    private var mAge:Int=-1
    private var mWeight:Int=-1
    private var mHeight:Int=-1
    private lateinit var mName:String
    private var mGender:String=""

    /*-----------------------------------------Local data variables-------------------------------------------------*/
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        /*-----------------Fetching views and initializing data-------------------*/
        nameEditText=findViewById(R.id.nameEditText)
        ageSpinner=findViewById(R.id.ageSpinner)
        weightEditText=findViewById(R.id.weightEditText)
        heightEditText=findViewById(R.id.heightEditText)
        saveEditsBtn=findViewById(R.id.saveEditsBtn)
        progressBar2=findViewById(R.id.progressBar2)
        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.editProfileActionBarString)
        }
        progressBar2?.visibility = View.VISIBLE
        mAuth=FirebaseAuth.getInstance()
        mFirebaseUser=mAuth?.currentUser
        currentUserEmailID= mFirebaseUser?.email!!
        mFirebaseDatabase= FirebaseDatabase.getInstance()
        userRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.userTable))

        /*-----------------Spinner implementation-------------------*/
        val adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = adapter

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
                        val userGender = data.child(resources.getString(R.string.userGenderColumn)).value.toString().trim()
                        val userWeight=data.child(resources.getString(R.string.userWeightColumn)).value.toString().trim()
                        val userHeight=data.child(resources.getString(R.string.userHeightColumn)).value.toString().trim()

                        mName=userName
                        mAge=userAge.toInt()
                        mGender=userGender
                        mWeight=userWeight.toInt()
                        mHeight=userHeight.toInt()


                        if(mGender==resources.getString(R.string.genderMaleTextLiteral))
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
                        weightEditText.setText(userWeight)
                        heightEditText.setText(userHeight)
                        progressBar2?.visibility = View.INVISIBLE
                    }
                }
            }
        })


        /*-----------------Spinner selection implementation-------------------*/
        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mAge = 5
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                mAge = selectedItem.toInt()
            }
        }


        /*-----------------Save user information implementation-------------------*/
        saveEditsBtn.setOnClickListener()
        {
            mName=nameEditText.text.toString().trim()
            if(TextUtils.isEmpty(mName))
            {
                Toast.makeText(this,resources.getString(R.string.editProfileNoNameToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(mGender=="")
            {
                Toast.makeText(this,resources.getString(R.string.editProfileNoGenderToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val strWeight=weightEditText.text.toString().trim()
            if(TextUtils.isEmpty(strWeight))
            {
                Toast.makeText(this,resources.getString(R.string.editProfileNoWeightToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else
            {
                mWeight=strWeight.toInt()
            }

            val strHeight=heightEditText.text.toString().trim()
            if(TextUtils.isEmpty(strHeight))
            {
                Toast.makeText(this,resources.getString(R.string.editProfileNoHeightToastTextLiteral),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else
            {
                mHeight=strHeight.toInt()
            }

            isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

            if(isInternetConnected) {
                val mFirebaseDatabaseReference = mFirebaseDatabase.getReference(resources.getString(R.string.userTable)).child(mFirebaseUser!!.uid)

                mFirebaseDatabaseReference?.child(resources.getString(R.string.userNameColumn))?.setValue(mName)
                mFirebaseDatabaseReference?.child(resources.getString(R.string.userAgeColumn))?.setValue(mAge)
                mFirebaseDatabaseReference?.child(resources.getString(R.string.userGenderColumn))?.setValue(mGender)
                mFirebaseDatabaseReference?.child(resources.getString(R.string.userWeightColumn))?.setValue(mWeight)
                mFirebaseDatabaseReference?.child(resources.getString(R.string.userHeightColumn))?.setValue(mHeight)

                Toast.makeText(this, resources.getString(R.string.dataOverwrittenDB), Toast.LENGTH_SHORT).show()

                val i = Intent(this, UserProfileActivity::class.java)
                startActivity(i)
            }
        }
    }

    /*-----------------Radio button implementation-------------------*/
    fun onRadioButtonClicked(view: View) {
        mGender = when (view.id) {
            R.id.radioMale ->{
                resources.getString(R.string.genderMaleTextLiteral)
            }
            R.id.radioFemale ->{
                resources.getString(R.string.genderFemaleTextLiteral)
            }
            else->{
                ""
            }
        }
    }

    /*-----------------Options Menu implementation-------------------*/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

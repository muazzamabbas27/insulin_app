package com.ciklum.insulinapp.activitiesPackage.notesPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import com.ciklum.insulinapp.Models.Notes
import com.ciklum.insulinapp.R
import com.ciklum.insulinapp.Utility.InternetUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



class NotesActivity : AppCompatActivity() {


    /*-----------------------------------------UI Elements-------------------------------------------------*/
    lateinit var dateTextView: TextView
    lateinit var notesEditText: EditText
    lateinit var saveNotesBtn:Button

    /*-----------------------------------------Firebase variables-------------------------------------------------*/
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference
    private lateinit var notesRootRef:DatabaseReference

    /*------------------------------------------Local data variables------------------------------------------------*/
    private var saveNotes:String=""
    private lateinit var currentUserEmailID:String
    private lateinit var tempDate:String
    private lateinit var tempMonth:String
    private lateinit var tempYear:String
    private lateinit var monthString:String
    private var isInternetConnected:Boolean=false

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        /*-----------------Fetching views and initializing data-------------------*/
        dateTextView=findViewById(R.id.dateTextView)
        notesEditText=findViewById(R.id.notesEditText)
        saveNotesBtn=findViewById(R.id.saveNotesBtn)

        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.notesActionBarString)
        }
        val bundle:Bundle= intent.extras
        var notesDate=""
        tempDate=bundle.getString(resources.getString(R.string.dateBundleTextLiteral))
        tempMonth=bundle.getString(resources.getString(R.string.monthBundleTextLiteral))
        tempYear=bundle.getString(resources.getString(R.string.yearBundleTextLiteral))
        convertMonthNumbertoWord()
        notesDate= "$notesDate$tempDate/$tempMonth/$tempYear"
        val displayDate= "$tempDate-$monthString-$tempYear"
        dateTextView.text = displayDate


        /*-----------------Fetching Firebase data-------------------*/
        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        notesRootRef=mFirebaseDatabase.getReference(resources.getString(R.string.notesTable))
        val mFirebaseUser:FirebaseUser?=mAuth.currentUser
        currentUserEmailID=mFirebaseUser?.email.toString().trim()


        /*-----------------Saving notes implementation-------------------*/
        notesRootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

                if(!isInternetConnected)
                {
                    Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                    return
                }
                for (data:DataSnapshot in dataSnapshot.children)
                {
                    val emailIDDB=data.child(resources.getString(R.string.notesEmailColumn)).value.toString().trim()

                    if(emailIDDB == currentUserEmailID)
                    {
                        val dateDB=data.child(resources.getString(R.string.notesTimeColumn)).value.toString().trim()
                        if(dateDB == notesDate)
                        {
                            val notesDB=data.child(resources.getString(R.string.notesDetailsColumn)).value.toString().trim()
                            notesEditText.setText(notesDB)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
            }
        })

        saveNotesBtn.setOnClickListener()
        {

            saveNotes=notesEditText.text.toString().trim()
            var overWrite=false
            notesRootRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    isInternetConnected=InternetUtility.isNetworkAvailable(applicationContext)

                    if(!isInternetConnected)
                    {
                        Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB),Toast.LENGTH_SHORT).show()
                        return
                    }
                    for (data:DataSnapshot in dataSnapshot.children)
                    {
                        if(!overWrite) {
                            val emailIDDB = data.child(resources.getString(R.string.notesEmailColumn)).value.toString().trim()
                            val dateDB = data.child(resources.getString(R.string.notesTimeColumn)).value.toString().trim()

                            if (emailIDDB == currentUserEmailID && dateDB == notesDate) {
                                val foundKey = data.key.toString()
                                val anotherNote = Notes(currentUserEmailID, saveNotes, notesDate)
                                notesRootRef.child(foundKey).setValue(anotherNote)
                                Toast.makeText(applicationContext, resources.getString(R.string.previousDataOverwrittenDB), Toast.LENGTH_SHORT).show()
                                overWrite = true
                                finish()
                            }
                        }
                    }

                    if(!overWrite)
                    {
                        val anotherNote=Notes(currentUserEmailID,saveNotes,notesDate)


                        mFirebaseDatabaseReference.push().setValue(anotherNote)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,resources.getString(R.string.errorReadingDB),Toast.LENGTH_LONG).show()
                }
            })

        }
    }

    /*-----------------Utility function-------------------*/
    private fun convertMonthNumbertoWord()
    {
        monthString = when (tempMonth) {
            "1" -> resources.getString(R.string.monthJan)
            "2" -> resources.getString(R.string.monthFeb)
            "3" -> resources.getString(R.string.monthMar)
            "4" -> resources.getString(R.string.monthApr)
            "5" -> resources.getString(R.string.monthMay)
            "6" -> resources.getString(R.string.monthJun)
            "7" -> resources.getString(R.string.monthJul)
            "8" -> resources.getString(R.string.monthAug)
            "9" -> resources.getString(R.string.monthSep)
            "10" -> resources.getString(R.string.monthOct)
            "11" -> resources.getString(R.string.monthNov)
            else -> resources.getString(R.string.monthDec)
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

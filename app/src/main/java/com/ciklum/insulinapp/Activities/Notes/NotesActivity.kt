package com.ciklum.insulinapp.Activities.Notes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ciklum.insulinapp.Activities.Calendar.MyCalendar
import com.ciklum.insulinapp.Activities.Dashboard.DashboardActivity
import com.ciklum.insulinapp.Activities.SplashScreen.currentUser
import com.ciklum.insulinapp.Activities.SplashScreen.mDatabaseReference
import com.ciklum.insulinapp.Models.Notes
import com.ciklum.insulinapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_notes.*
import org.w3c.dom.Text
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



class NotesActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var mFirebaseDatabaseReference: DatabaseReference

    private lateinit var rootRef:DatabaseReference

    lateinit var dateTextView: TextView
    lateinit var notesEditText: EditText
    lateinit var saveNotesBtn:Button

    var saveNotes:String="";
    lateinit var currentUserEmailID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        var bundle:Bundle=getIntent().getExtras()

        var notesDate:String=""

        var tempDate=bundle.getString("date")
        var tempMonth=bundle.getString("month")
        val tempYear=bundle.getString("year")

        notesDate=notesDate + tempDate+"/"+tempMonth+"/"+tempYear

        dateTextView=findViewById(R.id.dateTextView)
        dateTextView.setText(notesDate)

        notesEditText=findViewById(R.id.notesEditText)

        saveNotesBtn=findViewById(R.id.saveNotesBtn)

        mAuth =FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabaseReference = mFirebaseDatabase?.getReference("Notes")

        var mFirebaseUser:FirebaseUser?=mAuth.currentUser
        currentUserEmailID=mFirebaseUser?.email.toString().trim()

        rootRef=FirebaseDatabase.getInstance().getReference("Notes")

        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (data:DataSnapshot in dataSnapshot.getChildren())
                {
                    var emailIDDB=data.child("myEmailID").getValue().toString().trim()

                    if(emailIDDB.equals(currentUserEmailID))
                    {
                        var dateDB=data.child("myTime").getValue().toString().trim()
                        if(dateDB.equals(notesDate))
                        {
                            var notesDB=data.child("myNotes").getValue().toString().trim()
                            notesEditText.setText(notesDB)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Could not read from database, please check your internet connection",Toast.LENGTH_LONG).show()
            }
        })

        saveNotesBtn.setOnClickListener()
        {
            saveNotes=notesEditText.text.toString().trim()
            var anotherNote:Notes=Notes(currentUserEmailID,saveNotes,notesDate)

            //mFirebaseDatabaseReference= mFirebaseDatabaseReference?.child(mFirebaseUser?.uid)
            mFirebaseDatabaseReference.push().setValue(anotherNote)
            val i: Intent =Intent(this,MyCalendar::class.java)
            startActivity(i)
            finish()
        }

    }
}

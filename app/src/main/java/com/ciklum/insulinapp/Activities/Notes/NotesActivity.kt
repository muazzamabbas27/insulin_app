package com.ciklum.insulinapp.Activities.Notes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ciklum.insulinapp.R

class NotesActivity : AppCompatActivity() {

    lateinit var dateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        var bundle:Bundle=getIntent().getExtras()

        var date:String=""

        var tempDate=bundle.getString("date")
        var tempMonth=bundle.getString("month")
        val tempYear=bundle.getString("year")

        date=date + tempDate+"/"+tempMonth+"/"+tempYear

        dateTextView=findViewById(R.id.dateTextView)
        dateTextView.setText(date)
    }
}

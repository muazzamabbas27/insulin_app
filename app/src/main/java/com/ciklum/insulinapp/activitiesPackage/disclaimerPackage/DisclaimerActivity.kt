package com.ciklum.insulinapp.activitiesPackage.disclaimerPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ciklum.insulinapp.R
import android.content.Intent
import android.view.MenuItem


class DisclaimerActivity : AppCompatActivity() {

    private var devEmailBtn: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.disclaimerActionBarString)
        }


        devEmailBtn=findViewById(R.id.devEmailBtn)

        devEmailBtn?.setOnClickListener()
        {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = resources.getString(R.string.emailIntentTypeTextLiteral)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.emailDevIDTextLiteral)))
            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.emailSubjectTextLiteral))
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.emailStartingTextLiteral))
            startActivity(Intent.createChooser(intent, ""))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

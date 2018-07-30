package com.ciklum.insulinapp.activitiesPackage.disclaimerPackage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ciklum.insulinapp.R
import android.content.Intent



class DisclaimerActivity : AppCompatActivity() {

    private var devEmailBtn: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

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
}

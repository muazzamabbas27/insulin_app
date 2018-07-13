package com.ciklum.insulinapp.Activities.Disclaimer

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
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("usama.jawad@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "About the app")
            intent.putExtra(Intent.EXTRA_TEXT, "Hello, ")
            startActivity(Intent.createChooser(intent, ""))
        }
    }
}

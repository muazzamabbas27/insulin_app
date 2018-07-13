package com.ciklum.insulinapp.Activities.Dashboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.ciklum.insulinapp.Activities.Menu.MenuActivity
import com.ciklum.insulinapp.Activities.Register.RegisterActivity
import com.ciklum.insulinapp.Activities.SplashScreen.mAuth
import com.ciklum.insulinapp.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.view.MenuInflater
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import com.ciklum.insulinapp.Activities.Calendar.MyCalendar
import com.ciklum.insulinapp.Activities.Disclaimer.DisclaimerActivity
import com.ciklum.insulinapp.Activities.HealthArticles.HealthArticlesActivity
import com.ciklum.insulinapp.Activities.LogBG.LogBGActivity


class DashboardActivity : AppCompatActivity() {

    private var calendarBtn: Button?=null
    private var logBGLevelBtn:Button?=null
    private var healthArticlesBtn:Button?=null
    private var viewDisclaimerBtn:Button?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        calendarBtn=findViewById(R.id.calendarBtn)
        logBGLevelBtn=findViewById(R.id.logBGLevelBtn)
        healthArticlesBtn=findViewById(R.id.healthArticlesBtn)
        viewDisclaimerBtn=findViewById(R.id.viewDisclaimerBtn)

        calendarBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this, MyCalendar::class.java)
            startActivity(i)
        }

        logBGLevelBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this,LogBGActivity::class.java)
            startActivity(i)
        }

        healthArticlesBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this,HealthArticlesActivity::class.java)
            startActivity(i)
        }

        viewDisclaimerBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this,DisclaimerActivity::class.java)
            startActivity(i)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.logoutMenuBtn -> {
                mAuth?.signOut()
                val i:Intent=Intent(this,MenuActivity::class.java)
                startActivity(i)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}

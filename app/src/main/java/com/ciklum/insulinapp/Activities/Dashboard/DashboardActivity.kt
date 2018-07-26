package com.ciklum.insulinapp.Activities.Dashboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.ciklum.insulinapp.Activities.Menu.MenuActivity
import com.ciklum.insulinapp.Activities.SplashScreen.mAuth
import com.ciklum.insulinapp.R
import android.widget.Button
import com.ciklum.insulinapp.Activities.Calendar.MyCalendar
import com.ciklum.insulinapp.Activities.DetailedReports.DetailedReportsActivity
import com.ciklum.insulinapp.Activities.Disclaimer.DisclaimerActivity
import com.ciklum.insulinapp.Activities.Doctors.DoctorActivity
import com.ciklum.insulinapp.Activities.HealthArticles.HealthArticlesActivity
import com.ciklum.insulinapp.Activities.LogBG.LogBasalBGActivity
import com.ciklum.insulinapp.Activities.LogBG.LogBolusBGActivity
import com.ciklum.insulinapp.Activities.MedicineReminder.ReminderActivity
import com.ciklum.insulinapp.Activities.UserProfile.EditUserProfileActivity
import com.ciklum.insulinapp.Activities.UserProfile.UserProfileActivity


class DashboardActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout:DrawerLayout
    private lateinit var mToggle:ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mDrawerLayout=findViewById(R.id.mDrawerLayout)
        mToggle= ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close)
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.bringToFront()

        if(mDrawerLayout.isDrawerOpen(navigationView))
        {
            mDrawerLayout.closeDrawer(navigationView)
        }

        navigationView.setNavigationItemSelectedListener {menuItem ->
            // set item as selected to persist highlight
            //menuItem.isChecked = true
            // close drawer when item is tapped
            //mDrawerLayout.closeDrawers()

            var id:Int=menuItem.itemId

            if(id==R.id.viewProfileDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,UserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.editProfileDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,EditUserProfileActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBolusBGDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,LogBolusBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.logBasalBGDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,LogBasalBGActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.calendarDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,MyCalendar::class.java)
                startActivity(i)
            }

            if(id==R.id.medicineReminderDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,ReminderActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.healthArticlesDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,HealthArticlesActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.doctorInfoDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,DoctorActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.disclaimerDrawerBtn)
            {
                //mDrawerLayout.closeDrawers()
                val i:Intent=Intent(applicationContext,DisclaimerActivity::class.java)
                startActivity(i)
            }

            if(id==R.id.detailedReportsDrawerBtn)
            {
                val i:Intent=Intent(applicationContext,DetailedReportsActivity::class.java)
                startActivity(i)
            }

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        if(mToggle.onOptionsItemSelected(item))
        {
            return true
        }

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

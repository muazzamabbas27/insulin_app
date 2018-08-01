package com.ciklum.insulinapp.activitiesPackage.menuPackage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ciklum.insulinapp.activitiesPackage.loginPackage.LoginActivity
import com.ciklum.insulinapp.activitiesPackage.registerPackage.RegisterActivity
import com.ciklum.insulinapp.R

class MenuActivity : AppCompatActivity() {

    /*-----------------------------------------UI Elements-------------------------------------------------*/
    private var menuRegisterBtn:Button?=null
    private var menuLoginBtn:Button?=null

    /*------------------------------------------Main code------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        /*-----------------Fetching views and initializing data-------------------*/
        menuRegisterBtn=findViewById(R.id.menuRegisterBtn)
        menuLoginBtn=findViewById(R.id.menuLoginBtn)

        /*-----------------Register and Login button implementations-------------------*/
        menuRegisterBtn?.setOnClickListener()
        {
            val i=Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        menuLoginBtn?.setOnClickListener()
        {
            val i=Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}

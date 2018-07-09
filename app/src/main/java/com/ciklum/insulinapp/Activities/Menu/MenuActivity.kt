package com.ciklum.insulinapp.Activities.Menu

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ciklum.insulinapp.Activities.Login.LoginActivity
import com.ciklum.insulinapp.Activities.Register.RegisterActivity
import com.ciklum.insulinapp.R

class MenuActivity : AppCompatActivity() {

    private var menuRegisterBtn:Button?=null
    private var menuLoginBtn:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        menuRegisterBtn=findViewById(R.id.menuRegisterBtn)
        menuLoginBtn=findViewById(R.id.menuLoginBtn)

        menuRegisterBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        menuLoginBtn?.setOnClickListener()
        {
            val i:Intent=Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}

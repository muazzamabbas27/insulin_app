package com.ciklum.insulinapp.activitiesPackage.healthArticlesPackage

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ciklum.insulinapp.R
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.ciklum.insulinapp.Utility.InternetUtility

class HealthArticlesActivity : AppCompatActivity() {

    private var webView:WebView?=null
    private var isInternetConnected=false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_articles)

        if(supportActionBar !=null)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = resources.getString(R.string.healthArticlesActionBarString)
        }


        webView = findViewById(R.id.webView)

        isInternetConnected= InternetUtility.isNetworkAvailable(applicationContext)

        if(!isInternetConnected)
        {
            Toast.makeText(applicationContext,resources.getString(R.string.internetErrorDB), Toast.LENGTH_SHORT).show()
            return
        }

        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = WebViewClient()
        webView?.loadUrl(resources.getString(R.string.healthArticlesURL))

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId ==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

package com.ciklum.insulinapp.activitiesPackage.healthArticlesPackage

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ciklum.insulinapp.R
import android.webkit.WebView
import android.webkit.WebViewClient

class HealthArticlesActivity : AppCompatActivity() {

    private var webView:WebView?=null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_articles)

        webView = findViewById(R.id.webView)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = WebViewClient()
        webView?.loadUrl(resources.getString(R.string.healthArticlesURL))

    }
}

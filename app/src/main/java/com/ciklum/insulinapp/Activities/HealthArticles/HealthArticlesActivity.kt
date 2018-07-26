package com.ciklum.insulinapp.Activities.HealthArticles

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ciklum.insulinapp.R
import android.webkit.WebView
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.annotation.TargetApi
import android.widget.Toast
import android.webkit.WebViewClient
import android.app.Activity
import com.ciklum.insulinapp.R.id.webView
import com.ciklum.insulinapp.R.id.webView

class HealthArticlesActivity : AppCompatActivity() {

    private var webView:WebView?=null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_articles)

        webView = findViewById(R.id.webView)
        webView?.getSettings()?.setJavaScriptEnabled(true);
        webView?.setWebViewClient(WebViewClient())
        webView?.loadUrl("https://www.icliniq.com/articles/health-topics/diabetes-health")

    }
}

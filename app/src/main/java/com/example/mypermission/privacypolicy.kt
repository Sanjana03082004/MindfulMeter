package com.example.mypermission

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class privacypolicy : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacypolicy)

        val webViewPrivacyPolicy = findViewById<WebView>(R.id.webViewprivacypolicy)

        // Load a local HTML file from the app's resources
        webViewPrivacyPolicy.loadUrl("file:///android_res/raw/privacypolicy.html")

        // Enable JavaScript
        val webSettings = webViewPrivacyPolicy.settings
        webSettings.javaScriptEnabled = true

        // Set a WebViewClient to handle navigation within the WebView
        webViewPrivacyPolicy.webViewClient = WebViewClient()

        // Set OnApplyWindowInsetsListener if necessary
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

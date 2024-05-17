package com.example.mypermission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityTermsOfService : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_terms_of_service)

        val webViewTermsOfService = findViewById<WebView>(R.id.webViewTermsOfService)

        // Option 1: Load a local HTML file from the app's resources
        webViewTermsOfService.loadUrl("file:///android_res/raw/terms_of_service.html")

        // Option 2: Load a remote URL
        // webViewTermsOfService.loadUrl("https://example.com/terms_of_service.html")

        // Enable JavaScript (if needed)
        val webSettings = webViewTermsOfService.settings
        webSettings.javaScriptEnabled = true

        // Set a WebViewClient to handle navigation within the WebView
        webViewTermsOfService.webViewClient = WebViewClient()

        // Ensure that the view with ID "main" is not null before setting the OnApplyWindowInsetsListener
        findViewById<View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}

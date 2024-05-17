package com.example.mypermission

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypermission.databinding.ActivityMain3Binding


class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding.continueButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val contentView = findViewById<ViewGroup>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun onTermsCheckboxClicked(view: View) {
        if ((view as CheckBox).isChecked) {
            // Show a dialog or some UI to let the user view the Terms and Privacy Policy
            // For example, you could use an AlertDialog with two buttons:
            AlertDialog.Builder(this)
                .setTitle("Terms and Policies")
                .setMessage("Please review our Terms of Service and Privacy Policy.")
                .setPositiveButton("Terms of Service",
                    DialogInterface.OnClickListener { dialog, which -> // Launch the Terms of Service activity/fragment
                        val intent: Intent = Intent(
                            this@MainActivity3,
                            ActivityTermsOfService::class.java
                        )
                        startActivity(intent)
                    })
                .setNegativeButton("Privacy Policy",
                    DialogInterface.OnClickListener { dialog, which -> // Launch the Privacy Policy activity/fragment
                        val intent: Intent = Intent(
                            this@MainActivity3,
                            privacypolicy::class.java
                        )
                        startActivity(intent)
                    })
                .show()
        }
    }

}
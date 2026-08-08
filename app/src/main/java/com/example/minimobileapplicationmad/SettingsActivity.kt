package com.example.minimobileapplicationmad

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minimobileapplicationmad.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupSettings()
    }

    private fun setupSettings() {
        // Dark Mode
        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // About App
        binding.tvAboutApp.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showAboutDialog() {
        val messageView = TextView(this).apply {
            val fullText = """
                MAD Assignment
                
                mini mobile text editor
                
                Developers
                24020567 - N.T.Lakshan
                24020852 - P.H.H.Rashmika
                24020826 - K.D.Punsara
            """.trimIndent()
            
            val spannable = android.text.SpannableString(fullText)
            val firstLineEnd = fullText.indexOf("\n")
            if (firstLineEnd != -1) {
                spannable.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    0,
                    firstLineEnd,
                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    android.text.style.RelativeSizeSpan(1.2f),
                    0,
                    firstLineEnd,
                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            
            text = spannable
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
        }

        AlertDialog.Builder(this)
            .setTitle("About App")
            .setView(messageView)
            .setPositiveButton("Close", null)
            .show()
    }
}

package com.example.autocar

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("screen_caster_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("first_launch", true)

        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)
        val btnGotIt = findViewById<Button>(R.id.btnGotIt)

        if (isFirstLaunch) {
            // First time: show welcome message
            tvSubtitle.text = getString(R.string.onboarding_body)
            btnGotIt.text = getString(R.string.onboarding_ok)
            btnGotIt.setOnClickListener {
                prefs.edit().putBoolean("first_launch", false).apply()
                tvSubtitle.text = getString(R.string.launch_on_phone)
                btnGotIt.visibility = android.view.View.GONE
            }
        } else {
            // Returning user: show normal screen
            tvSubtitle.text = getString(R.string.launch_on_phone)
            btnGotIt.visibility = android.view.View.GONE
        }
    }
}

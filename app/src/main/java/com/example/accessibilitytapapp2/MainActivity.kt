package com.example.accessibilitytapapp2

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnableService = findViewById<Button>(R.id.btnEnableService)
        val btnTap = findViewById<Button>(R.id.btnTap)
        val btnSwipe = findViewById<Button>(R.id.btnSwipe)

        btnEnableService.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnTap.setOnClickListener {
            MyAccessibilityService.instance?.performTap(500f, 500f)
        }

        btnSwipe.setOnClickListener {
            MyAccessibilityService.instance?.performSwipe(300f, 1000f, 300f, 500f)
        }
    }
}
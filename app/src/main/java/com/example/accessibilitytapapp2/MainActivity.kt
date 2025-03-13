package com.example.accessibilitytapapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playSongButton: Button = findViewById(R.id.playSongButton)

        playSongButton.setOnClickListener {
            // Send broadcast to Accessibility Service to perform Spotify actions
            val intent = Intent("ACTION_PLAY_SONG")
            sendBroadcast(intent)
        }
    }
}

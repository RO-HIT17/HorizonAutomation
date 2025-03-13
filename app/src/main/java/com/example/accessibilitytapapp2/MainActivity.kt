package com.example.accessibilitytapapp2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.playSongButton)
        button.setOnClickListener {
            Log.d("MainActivity", "Play song button clicked")
            // Launch Spotify
            val spotifyIntent = packageManager.getLaunchIntentForPackage("com.spotify.music")

            //Log.d("Test", spotifyIntent.toString())
            if (spotifyIntent != null) {
                startActivity(spotifyIntent)
                Log.d("Test", "Open play song action")
                // Delay for Spotify to fully open
                Handler(Looper.getMainLooper()).postDelayed({
                    // Send broadcast to trigger action in AccessibilityService
                    val intent = Intent("ACTION_PLAY_SONG")
                    sendBroadcast(intent)
                }, 5000) // Adjust delay as needed
            } else {
                Toast.makeText(this, "Spotify not installed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
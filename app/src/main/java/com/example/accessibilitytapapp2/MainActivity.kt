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
            val spotifyIntent = packageManager.getLaunchIntentForPackage("com.spotify.music")

            if (spotifyIntent != null) {
                startActivity(spotifyIntent)
                Log.d("Test", "Open play song action")
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent("ACTION_PLAY_SONG")
                    sendBroadcast(intent)
                }, 5000)
            } else {
                Toast.makeText(this, "Spotify not installed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
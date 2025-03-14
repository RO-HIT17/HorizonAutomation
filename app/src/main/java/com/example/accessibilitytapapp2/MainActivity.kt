package com.example.accessibilitytapapp2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnEnableService: Button
    private lateinit var voiceInputButton: Button
    private lateinit var button:Button

    private val VOICE_INPUT_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById<Button>(R.id.playSongButton)
        btnEnableService = findViewById(R.id.btnEnableService)
        voiceInputButton = findViewById(R.id.voiceInputButton)

        btnEnableService.setOnClickListener {
            openAccessibilitySettings()
        }
        voiceInputButton.setOnClickListener {
            startVoiceInput()
        }

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

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open accessibility settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the song name to play")

        try {
            startActivityForResult(intent, VOICE_INPUT_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

}
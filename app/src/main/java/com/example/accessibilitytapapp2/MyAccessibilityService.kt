package com.example.accessibilitytapapp2

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    private lateinit var receiver: BroadcastReceiver

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver()
    }

    private fun registerReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "ACTION_PLAY_SONG") {
                    playSpotifySong()
                }
            }
        }
        val filter = IntentFilter("ACTION_PLAY_SONG")
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun tap(x: Int, y: Int, delay: Long = 500L) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
        Thread.sleep(delay) // delay between actions
    }

    private fun playSpotifySong() {
        // Open Spotify App
        val launchIntent = packageManager.getLaunchIntentForPackage("com.spotify.music")
        launchIntent?.let { startActivity(it) }

        Thread.sleep(5000) // Wait for Spotify to open

        // Tap Search
        tap(264, 1353, 2000)

        // Focus Search again
        tap(264, 1353, 2000)

        // Type song name (NOTE: Text input via AccessibilityService is limited. Might need workaround or special input node finding)

        // Tap first search result
        tap(173, 226, 2000)

        // Tap play button
        tap(300, 1200, 2000)
    }
}

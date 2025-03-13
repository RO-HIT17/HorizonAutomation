package com.example.accessibilitytapapp2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_PLAY_SONG") {
            // Save the song name in shared preferences or global variable
            MyAccessibilityService.songNameToSearch = "Tum Sath Ho"
            Log.d("Receiver", "Received play song action")
        }
    }
}

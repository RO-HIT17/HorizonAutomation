package com.example.accessibilitytapapp2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_PLAY_SONG") {
            MyAccessibilityService.instance?.performSpotifySongSearch("Tum Sath Ho")
        }
    }
}
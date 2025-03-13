package com.example.accessibilitytapapp2

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var songNameToSearch: String? = null  // Song name from BroadcastReceiver
    }

    private var hasPerformedAction = false  // Avoid repeat execution


    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Accessibility", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            Log.d("AccessibilityService", "Event: ${event.eventType} from ${event.packageName}")
        }

        if (event?.packageName == "com.spotify.music" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !hasPerformedAction) {
            Log.d("AccessibilityService", "Event 2: ${event.eventType} from ${event.packageName}")

                performSpotifySongSearch()
                hasPerformedAction = true  // Prevent multiple triggers

        }
    }

    override fun onInterrupt() {}

    private fun performSpotifySongSearch() {
        val songName = "Tum Sath Ho"
        Log.d("Accessibility", "Starting to search song: $songName")

        performTap(264, 1353) // Tap search icon
        Thread.sleep(10000)

        performTap(264, 1353) // Tap search bar
        Thread.sleep(10000)

        inputText(songName)  // Type song name
        Thread.sleep(10000)

        performTap(173, 226) // Select result
        Thread.sleep(10000)

        performTap(92, 277)  // Select first item
        Thread.sleep(10000)

        performTap(300, 1200) // Play button
        Log.d("Accessibility", "Song play sequence completed")
    }

    fun performTap(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    fun inputText(text: String) {
        val rootNode = rootInActiveWindow ?: return
        val inputField = findEditableNode(rootNode) ?: return

        val args = Bundle()
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    private fun findEditableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable) return node
        for (i in 0 until node.childCount) {
            val result = findEditableNode(node.getChild(i))
            if (result != null) return result
        }
        return null
    }
}

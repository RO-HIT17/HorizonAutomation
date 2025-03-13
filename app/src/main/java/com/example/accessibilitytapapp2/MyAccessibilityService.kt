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
        var instance: MyAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun performSpotifySongSearch(songName: String) {
        // Coordinates based on your adb script
        performTap(264, 1353) // Tap search icon
        Thread.sleep(2000)

        performTap(264, 1353) // Tap search bar
        Thread.sleep(1000)

        inputText(songName)  // Type song name
        Thread.sleep(2000)

        performTap(173, 226) // Select result
        Thread.sleep(3000)

        performTap(92, 277)  // Select first item
        Thread.sleep(1000)

        performTap(300, 1200) // Play button
        Log.d("SpotifyAutomation", "Song Play sequence completed!")
    }

    fun performTap(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
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

package com.example.accessibilitytapapp2

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var songNameToSearch: String? = null  // Song name to be received dynamically
    }

    private var hasPerformedAction = false  // Prevents repeated execution


    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AccessibilityService", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        Log.d("AccessibilityService", "Event: ${event.eventType} from ${event.packageName}")

        // Check for Spotify package and specific window state change event
        if (event.packageName == "com.spotify.music" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !hasPerformedAction) {
            Log.d("AccessibilityService", "Spotify window detected. Initiating search...")
            performSpotifySongSearch()
            hasPerformedAction = true  // Avoid re-triggering
        }
    }

    override fun onInterrupt() {
        Log.e("AccessibilityService", "Service interrupted")
    }

    /**
     * Automates the Spotify song search and play
     */
    private fun performSpotifySongSearch() {
        val songName = songNameToSearch ?: "Tum Sath Ho" // Default if null
        Log.d("AccessibilityService", "Starting search for song: $songName")

        // Sequence of actions (tap and input)
        performTap(264, 1353)  // Tap on search icon
        sleep(5000)

        performTap(264, 1353)  // Tap on search bar
        sleep(5000)

        inputTextOrPaste(songName)  // Type or paste song name
        sleep(5000)

        performTap(173, 226)   // Select search result
        sleep(5000)

        performTap(92, 277)    // Select first item
        sleep(5000)

        performTap(300, 1200)  // Play the song
        Log.d("AccessibilityService", "Song play sequence completed")
    }

    /**
     * Performs tap at specified (x, y)
     */
    private fun performTap(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))

        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("AccessibilityService", "Gesture completed at ($x, $y)")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.e("AccessibilityService", "Gesture cancelled at ($x, $y)")
            }
        }, null)
    }

    /**
     * Try direct input, else clipboard + paste
     */
    private fun inputTextOrPaste(text: String) {
        val rootNode = rootInActiveWindow ?: run {
            Log.e("AccessibilityService", "No active window found for input")
            return
        }

        val inputField = findEditableNode(rootNode)

        if (inputField != null) {
            val args = Bundle()
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            val success = inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            if (success) {
                Log.d("AccessibilityService", "Text input successful")
            } else {
                Log.e("AccessibilityService", "Direct text input failed, using clipboard")
                useClipboardToPaste(text)
            }
        } else {
            Log.e("AccessibilityService", "No editable field found, using clipboard")
            useClipboardToPaste(text)
        }
    }

    /**
     * Fallback to clipboard and try pasting
     */
    private fun useClipboardToPaste(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("song_name", text)
        clipboard.setPrimaryClip(clip)
        Log.d("AccessibilityService", "Copied '$text' to clipboard")

        // Try to press Paste if available
        pasteTextIfPossible()
    }

    /**
     * Looks for a 'Paste' button and clicks it
     */
    private fun pasteTextIfPossible() {
        val rootNode = rootInActiveWindow ?: return
        val pasteButton = findNodeWithText(rootNode, "Paste") // Can vary: use "PASTE" if all caps

        if (pasteButton != null) {
            pasteButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("AccessibilityService", "Clicked 'Paste'")
        } else {
            Log.e("AccessibilityService", "No 'Paste' button found")
        }
    }

    /**
     * Recursively finds editable input field
     */
    private fun findEditableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable) return node

        for (i in 0 until node.childCount) {
            findEditableNode(node.getChild(i))?.let { return it }
        }
        return null
    }

    /**
     * Recursively searches for text match in nodes
     */
    private fun findNodeWithText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true) return node

        for (i in 0 until node.childCount) {
            findNodeWithText(node.getChild(i), text)?.let { return it }
        }
        return null
    }

    /**
     * Helper function to sleep safely
     */
    private fun sleep(ms: Long) {
        try {
            Thread.sleep(ms)
        } catch (e: InterruptedException) {
            Log.e("AccessibilityService", "Sleep interrupted: ${e.message}")
        }
    }
}

package com.example.accessibilitytapapp2

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.os.*
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var songNameToSearch: String? = null  // Song name from BroadcastReceiver (future use)
    }

    private var hasPerformedAction = false  // To avoid repeated execution

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Accessibility", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            Log.d("AccessibilityService", "Event: ${event.eventType} from ${event.packageName}")
        }

        // Trigger only once when Spotify screen is opened
        if (event?.packageName == "com.spotify.music" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !hasPerformedAction) {
            Log.d("AccessibilityService", "Spotify window detected, starting automation")
            performSpotifySongSearch()
            hasPerformedAction = true  // Avoid multiple triggers
        }
    }

    override fun onInterrupt() {}

    /**
     * Perform sequence to search and play a song
     */
    private fun performSpotifySongSearch() {
        val songName = songNameToSearch ?: "Tum Sath Ho" // Default if null
        Log.d("AccessibilityService", "Starting search for song: $songName")

        // Step 1: Tap on search icon
        performTap(264, 1353)  // Search icon
        Handler(Looper.getMainLooper()).postDelayed({

            // Step 2: Tap on search bar
            performTap(264, 1353)  // Search bar
            Handler(Looper.getMainLooper()).postDelayed({

                // Step 3: Input song name using clipboard and paste
                inputTextViaClipboardWithLongPress(264, 1353, songName)
                Handler(Looper.getMainLooper()).postDelayed({

                    // Step 4: Tap on search result
                    performTap(173, 226)  // First search result
                    Handler(Looper.getMainLooper()).postDelayed({

                        // Step 5: Tap on first item
                        performTap(92, 277)  // First item from search result
                        Handler(Looper.getMainLooper()).postDelayed({

                            // Step 6: Tap on play button
                            performTap(300, 1200)  // Play button
                            Log.d("AccessibilityService", "Song play sequence completed")

                        }, 5000)  // 5 sec delay before play button tap
                    }, 5000)  // 5 sec delay before selecting first item
                }, 5000)  // 5 sec delay for pasting text
            }, 5000)  // 5 sec delay before tapping search bar
        }, 5000)  // 5 sec delay after tapping search icon
    }

    /**
     * Perform tap at given coordinates
     */
    fun performTap(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d("Accessibility", "Tap at ($x, $y) completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.e("Accessibility", "Tap at ($x, $y) cancelled")
            }
        }, null)
    }

    /**
     * Perform long press at given coordinates
     */
    fun performLongPress(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 1000)) // 1 sec long press
        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d("Accessibility", "Long press at ($x, $y) completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.e("Accessibility", "Long press at ($x, $y) cancelled")
            }
        }, null)
    }

    /**
     * Input text using clipboard, long press and paste method
     */
    fun inputTextViaClipboardWithLongPress(x: Int, y: Int, text: String) {
        copyTextToClipboard(text)  // Copy to clipboard
        performLongPress(x, y)     // Long press on search bar

        // Wait for "Paste" option to appear and click it
        Handler(Looper.getMainLooper()).postDelayed({
            pasteTextIfPossible()
        }, 1500)  // Adjust delay as per device responsiveness
    }

    /**
     * Copy text to clipboard
     */
    fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("song_name", text)
        clipboard.setPrimaryClip(clip)
        Log.d("Accessibility", "Text copied to clipboard: $text")
    }

    /**
     * Find and click "Paste" button from contextual menu
     */
    fun pasteTextIfPossible() {
        val rootNode = rootInActiveWindow ?: return
        val pasteButton = findNodeWithText(rootNode, "Paste")  // Look for 'Paste' option

        if (pasteButton != null) {
            pasteButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("Accessibility", "'Paste' clicked successfully")
        } else {
            Log.e("Accessibility", "'Paste' button not found")
        }
    }

    /**
     * Recursively search for a node containing specific text
     */
    fun findNodeWithText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true) return node
        for (i in 0 until node.childCount) {
            val result = findNodeWithText(node.getChild(i), text)
            if (result != null) return result
        }
        return null
    }
}

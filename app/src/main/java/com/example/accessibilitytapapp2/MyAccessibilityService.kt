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
        var songNameToSearch: String? = null
    }

    private var hasPerformedAction = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Accessibility", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            Log.d("AccessibilityService", "Event: ${event.eventType} from ${event.packageName}")
        }

        if (event?.packageName == "com.spotify.music" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !hasPerformedAction) {
            Log.d("AccessibilityService", "Spotify window detected, starting automation")
            performSpotifySongSearch()
            hasPerformedAction = true
        }
    }

    override fun onInterrupt() {}

    private fun performSpotifySongSearch() {
        val songName = songNameToSearch ?: "Tum Sath Ho"
        Log.d("AccessibilityService", "Starting search for song: $songName")

        performTap(264, 1353)
        Handler(Looper.getMainLooper()).postDelayed({

            performTap(264, 1353)
            Handler(Looper.getMainLooper()).postDelayed({

                inputTextViaClipboardWithLongPress(264, 1353, songName)
                Handler(Looper.getMainLooper()).postDelayed({

                    performTap(173, 226)
                    Handler(Looper.getMainLooper()).postDelayed({


                        performTap(92, 277)
                        Handler(Looper.getMainLooper()).postDelayed({

                            performTap(300, 1200)
                            Log.d("AccessibilityService", "Song play sequence completed")

                        }, 5000)
                    }, 5000)
                }, 5000)
            }, 5000)
        }, 5000)
    }

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

    fun inputTextViaClipboardWithLongPress(x: Int, y: Int, text: String) {
        copyTextToClipboard(text)
        performLongPress(x, y)

        Handler(Looper.getMainLooper()).postDelayed({
            pasteTextIfPossible()
        }, 1500)
    }

    fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("song_name", text)
        clipboard.setPrimaryClip(clip)
        Log.d("Accessibility", "Text copied to clipboard: $text")
    }

    fun pasteTextIfPossible() {
        val rootNode = rootInActiveWindow ?: return
        val pasteButton = findNodeWithText(rootNode, "Paste")

        if (pasteButton != null) {
            pasteButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("Accessibility", "'Paste' clicked successfully")
        } else {
            Log.e("Accessibility", "'Paste' button not found")
        }
    }

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

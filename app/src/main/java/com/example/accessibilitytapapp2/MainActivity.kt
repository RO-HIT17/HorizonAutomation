package com.example.accessibilitytapapp2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var socketServer: SocketServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        socketServer = SocketServer { command ->
            runOnUiThread {
                handleCommand(command)
            }
        }
        socketServer.startServer()
    }

    private fun handleCommand(command: String) {
        val parts = command.split(" ")
        when (parts[0]) {
            "tap" -> {
                val x = parts[1].toIntOrNull() ?: return
                val y = parts[2].toIntOrNull() ?: return
                Log.d("MainActivity", "👉 Performing tap at ($x, $y)")
            }
            "swipe" -> {
                val x1 = parts[1].toIntOrNull() ?: return
                val y1 = parts[2].toIntOrNull() ?: return
                val x2 = parts[3].toIntOrNull() ?: return
                val y2 = parts[4].toIntOrNull() ?: return
                Log.d("MainActivity", "👆 Performing swipe from ($x1, $y1) to ($x2, $y2)")
            }
            else -> Log.d("MainActivity", "❓ Unknown command: $command")
        }
    }
}

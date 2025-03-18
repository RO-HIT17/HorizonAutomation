package com.example.accessibilitytapapp2

import java.io.*
import java.net.*
import android.util.Log

class SocketServer(private val callback: (String) -> Unit) {
    private val PORT = 9999
    private var serverThread: Thread? = null
    private var serverSocket: ServerSocket? = null

    fun startServer() {
        serverThread = Thread {
            try {
                serverSocket = ServerSocket(PORT)
                Log.d("SocketServer", "✅ Server started on port $PORT")

                while (!Thread.currentThread().isInterrupted) {
                    val client: Socket = serverSocket!!.accept()
                    Log.d("SocketServer", "📥 Client connected")

                    val input = BufferedReader(InputStreamReader(client.getInputStream()))
                    val output = PrintWriter(client.getOutputStream(), true)

                    val message = input.readLine()
                    Log.d("SocketServer", "📩 Received command: $message")

                    // Call the callback function to process the command
                    callback(message)

                    output.println("Executed: $message")
                    client.close()
                }
            } catch (e: Exception) {
                Log.e("SocketServer", "❌ Server error: ${e.message}")
            }
        }
        serverThread?.start()
    }

    fun stopServer() {
        serverThread?.interrupt()
        serverSocket?.close()
        Log.d("SocketServer", "🛑 Server stopped")
    }
}

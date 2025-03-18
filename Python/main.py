import socket
import time

HOST = "127.0.0.1"  # Localhost (matches Kotlin server)
PORT = 9999  # Must match Kotlin port

def send_command(command):
    try:
        client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client.connect((HOST, PORT))
        client.sendall((command + "\n").encode())  # Send command
        response = client.recv(1024).decode()  # Receive response
        print("Response from Kotlin:", response)
        client.close()
    except Exception as e:
        print("Error:", e)

# 🔄 Python script running in a loop
while True:
    time.sleep(5)  # Simulate background execution
    print("Python running...")

    # 🔥 Example: Send tap command to Kotlin (tap at x=100, y=200)
    send_command("tap 100 200")

    # 🔥 Example: Send swipe command (swipe from x1=100, y1=200 to x2=300, y2=400)
    send_command("swipe 100 200 300 400")

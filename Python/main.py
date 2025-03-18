import socket

HOST = "192.168.29.1"  # This should be the correct IP
PORT = 5000

def send_command(command):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((HOST, PORT))
            s.sendall(command.encode())
            response = s.recv(1024).decode()
            print("📩 Response from Kotlin:", response)
    except Exception as e:
        print("❌ Connection error:", e)

send_command("tap 100 200")
send_command("swipe 100 200 300 400")

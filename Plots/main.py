import socket
import json

PORT = 4448

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect(('127.0.0.1', PORT))
    json.dump("<request></request>\n", s)

from model import PlotData
import socket
from pykson import Pykson


PORT = 4448


if __name__ == '__main__':
    plot_data = PlotData()
    plot_data.cowsAlive = 0
    plot_data.wolvesAlive = 0
    plot_data.dogsAlive = 0
    plot_data.minersAlive = 0
    plot_data.obstaclesAlive = 0

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', PORT))

    while True:
        req = (Pykson().to_json(plot_data) + '\r\n').encode('utf-8')

        s.sendall(req)
        res = s.recv(1024).decode('utf-8')

        plot_data = (Pykson().from_json(res, PlotData))





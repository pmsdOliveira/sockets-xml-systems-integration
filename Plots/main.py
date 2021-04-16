from model import PlotData

import socket
from pykson import Pykson
import matplotlib.pyplot as plt


PORT = 4448


if __name__ == '__main__':
    plot_data = PlotData()
    plot_data.cowsAlive = 0
    plot_data.wolvesAlive = 0
    plot_data.dogsAlive = 0
    plot_data.minersAlive = 0
    plot_data.obstaclesAlive = 0

    x = []
    cowsAlive = []

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.0.1', PORT))

    i = 0
    while True:
        req = (Pykson().to_json(plot_data) + '\r\n').encode('utf-8')

        s.sendall(req)
        res = s.recv(2048).decode('utf-8')
        print(res)

        plot_data = (Pykson().from_json(res, PlotData))

        print("\n\n\n**************************************")
        print("* Cows alive:               %d      " % (plot_data.cowsAlive))
        print("* Wolves alive:             %d      " % (plot_data.wolvesAlive))
        print("* Dogs alive:               %d      " % (plot_data.dogsAlive))
        print("* Miners alive:             %d      " % (plot_data.minersAlive))
        print("* Obstacles alive:          %d      " % (plot_data.obstaclesAlive))
        #for kill in plot_data.wolvesKills:
        #    print("* %s kills:          %d     " % (kill["killerName"], kill["kills"]))
        #for kill in plot_data.minersKills:
        #    print("* %s destructions:          %d     " % (kill["killerName"], kill["kills"]))
        print("*************************************\n\n\n")

        if i >= 15:
            plt.xlim(i - 15, i + 15)
        else:
            plt.xlim(0, 30)
        plt.ylim(0, 50)
        plt.xlabel('Ticks')
        plt.ylabel('Cows')
        plt.suptitle("Cow population over time")

        x.append(i)
        cowsAlive.append(plot_data.cowsAlive)
        plt.plot(x, cowsAlive)

        plt.draw()
        plt.pause(0.01)

        i += 1

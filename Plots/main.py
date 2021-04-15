import socket
from pykson import JsonObject, IntegerField, ObjectListField

PORT = 4448

class PlotData(JsonObject):
    cowsAlive = IntegerField()
    wolvesAlive = IntegerField()
    dogsAlive = IntegerField()
    minersAlive = IntegerField()
    obstaclesAlive = IntegerField()
    cowsKilled = IntegerField()
    obstaclesDestroyed = IntegerField()
    cowsKilledByWolves = ObjectListField()



if __name__ == '__main__':
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(('127.0.0.1', PORT))

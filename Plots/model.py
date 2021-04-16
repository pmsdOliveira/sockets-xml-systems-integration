from pykson import JsonObject, IntegerField, StringField, ObjectListField


class WolvesKills(JsonObject):
    killerName = StringField()
    kills = IntegerField()


class MinersKills(JsonObject):
    killerName = StringField()
    kills = IntegerField()


class PlotData(JsonObject):
    cowsAlive = IntegerField()
    wolvesAlive = IntegerField()
    dogsAlive = IntegerField()
    minersAlive = IntegerField()
    obstaclesAlive = IntegerField()
    wolvesKills = ObjectListField(WolvesKills)
    minersKills = ObjectListField(MinersKills)

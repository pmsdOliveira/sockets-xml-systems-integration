package Simulation;


public class KillCount {
    private String killerName;
    private int kills;
    
    public KillCount() {
        this.killerName = "";
        this.kills = 0;
    }
    
    public KillCount(String killer, int kills) {
        this.killerName = killer;
        this.kills = kills;
    }
    
    public String getKillerName() {
        return this.killerName;
    }
    
    public void setKillerName(String killer) {
        this.killerName = killer;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
}

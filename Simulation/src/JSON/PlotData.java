package JSON;


import Simulation.KillCount;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;


@Generated("IS_TP1")
public class PlotData {
    
    @SerializedName("cowsAlive")
    @Expose
    private int cowsAlive;
    
    @SerializedName("wolvesAlive")
    @Expose
    private int wolvesAlive;
    
    @SerializedName("dogsAlive")
    @Expose
    private int dogsAlive;
    
    @SerializedName("minersAlive")
    @Expose
    private int minersAlive;
    
    @SerializedName("obstaclesAlive")
    @Expose
    private int obstaclesAlive;
    
    @SerializedName("wolvesKills")
    @Expose
    private List<KillCount> wolvesKills;
    
    @SerializedName("minersKills")
    @Expose
    private List<KillCount> minersKills;
    
    public PlotData() {
        this.cowsAlive = 0;
        this.wolvesAlive = 0;
        this.dogsAlive = 0;
        this.minersAlive = 0;
        this.obstaclesAlive = 0;
        
        wolvesKills = new ArrayList<>();
        minersKills = new ArrayList<>();
    }
    
    public PlotData(int cowsAlive, int wolvesAlive, int dogsAlive, 
            int minersAlive, int obstaclesAlive, int cowsKilled, int obstaclesKilled) {
        this.cowsAlive = cowsAlive;
        this.wolvesAlive = wolvesAlive;
        this.dogsAlive = dogsAlive;
        this.minersAlive = minersAlive;
        this.obstaclesAlive = obstaclesAlive;
        
        wolvesKills = new ArrayList<>();
        minersKills = new ArrayList<>();
    }
    
    public int getCowsAlive() {
        return this.cowsAlive;
    }
    
    public void setCowsAlive(int n) {
        this.cowsAlive = n;
    }
    
    public int getWolvesAlive() {
        return this.wolvesAlive;
    }
    
    public void setWolvesAlive(int n) {
        this.wolvesAlive = n;
    }
    
    public int getDogsAlive() {
        return this.dogsAlive;
    }
    
    public void setDogsAlive(int n) {
        this.dogsAlive = n;
    }
    
    public int getMinersAlive() {
        return this.minersAlive;
    }
    
    public void setMinersAlive(int n) {
        this.minersAlive = n;
    }
    
    public int getObstaclesAlive() {
        return this.obstaclesAlive;
    }
    
    public void setObstaclesAlive(int n) {
        this.obstaclesAlive = n;
    }
    
    public int getWolfKills(int id) {
        return this.wolvesKills.get(id).getKills();
    }
    
    public void addWolfKills(String id, int n) {
        int wolfID = Integer.parseInt(String.valueOf(id.charAt(id.length() - 1)));
        
        boolean exists = false;
        if (wolfID < wolvesKills.size())
            if (wolvesKills.contains(wolvesKills.get(wolfID))) {
                exists = true;
            } 
        
        if (exists) {
            wolvesKills.get(wolfID).setKills(n);
        } else {
            KillCount killCount = new KillCount(id, n);
            this.wolvesKills.add(killCount);
        }
    }
    
    public int getMinersKills(int id) {
        return this.minersKills.get(id).getKills();
    }
    
    public void addMinerKills(String id, int n) {
        int minerID = Integer.parseInt(String.valueOf(id.charAt(id.length() - 1)));
        
        boolean exists = false;
        if (minerID < minersKills.size())
            if (minersKills.contains(minersKills.get(minerID))) {
                exists = true;
            } 
        
        if (exists) {
            minersKills.get(minerID).setKills(n);
        } else {
            KillCount killCount = new KillCount(id, n);
            this.minersKills.add(killCount);
        }
    }
}

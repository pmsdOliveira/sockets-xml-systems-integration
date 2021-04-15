package JSON;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
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
    
    @SerializedName("cowsKilled")
    @Expose
    private int cowsKilled;
    
    @SerializedName("obstaclesDestroyed")
    @Expose
    private int obstaclesDestroyed;
    
    @SerializedName("cowsKilledByWolves")
    @Expose
    private HashMap<String, Integer> cowsKilledByWolves;
    
    @SerializedName("obstaclesDestroyedByMiners")
    @Expose
    private HashMap<String, Integer> obstaclesDestroyedByMiners;
    
    public PlotData() {
        this.cowsAlive = 0;
        this.wolvesAlive = 0;
        this.dogsAlive = 0;
        this.minersAlive = 0;
        this.obstaclesAlive = 0;
        this.cowsKilled = 0;
        this.obstaclesDestroyed = 0;
        
        cowsKilledByWolves = new HashMap<>();
        obstaclesDestroyedByMiners = new HashMap<>();
    }
    
    public PlotData(int cowsAlive, int wolvesAlive, int dogsAlive, 
            int minersAlive, int obstaclesAlive, int cowsKilled, int obstaclesKilled) {
        this.cowsAlive = cowsAlive;
        this.wolvesAlive = wolvesAlive;
        this.dogsAlive = dogsAlive;
        this.minersAlive = minersAlive;
        this.obstaclesAlive = obstaclesAlive;
        this.cowsKilled = cowsKilled;
        this.obstaclesDestroyed = obstaclesKilled;
        
        cowsKilledByWolves = new HashMap<>();
        obstaclesDestroyedByMiners = new HashMap<>();
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
    
    public int getCowsKilled() {
        return this.cowsKilled;
    }
    
    public void setCowsKilled(int n) {
        this.cowsKilled = n;
    }
    
    public int getObstaclesDestroyed() {
        return this.obstaclesDestroyed;
    }
    
    public void setObstaclesDestroyed(int n) {
        this.obstaclesDestroyed = n;
    }
    
    public int getCowsKilledByWolf(String s) {
        return this.cowsKilledByWolves.get(s);
    }
    
    public void setCowsKilledByWolf(String s, int n) {
        this.cowsKilledByWolves.put(s, n);
    }
    
    public int getObstaclesDestroyedByMiner(String s) {
        return this.obstaclesDestroyedByMiners.get(s);
    }
    
    public void setObstaclesDestroyedByMiner(String s, int n) {
        this.obstaclesDestroyedByMiners.put(s, n);
    }
    
    public HashMap getCowsKilledByWolves() {
        return this.cowsKilledByWolves;
    }
    
    public void setCowsKilledByWolves(HashMap cowsKilledByWolves) {
        this.cowsKilledByWolves = cowsKilledByWolves;
    }
    
    public HashMap getObstaclesDestroyedByMiners() {
        return this.obstaclesDestroyedByMiners;
    }
    
    public void setObstaclesDestroyedByMiners(HashMap obstaclesDestroyedByMiners) {
        this.obstaclesDestroyedByMiners = obstaclesDestroyedByMiners;
    }
}

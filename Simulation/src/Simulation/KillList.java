/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Utilizador
 */
public class KillList {
    private List<String> kills;
    
    public KillList() {
        this.kills = new ArrayList<>();
    }
    
    public KillList(List<String> kills) {
        this.kills = kills;
    }
    
    public List<String> getKills() {
        return this.kills;
    }
    
    public void setKills(List<String> kills) {
        this.kills = kills;
    }
    
    public void addKill(String entityKilled) {
        this.kills.add(entityKilled);
    }
}

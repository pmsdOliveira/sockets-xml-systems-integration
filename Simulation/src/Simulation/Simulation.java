/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Common.MessageManagement;
import JSON.PlotData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import com.google.gson.Gson;
import org.netbeans.xml.schema.updateschema.TMyPlace;
import org.netbeans.xml.schema.updateschema.TPlace;
import org.netbeans.xml.schema.updateschema.TPosition;

/**
 *
 * @author adroc
 */
public class Simulation extends Thread {
    
    private static final int cowPort = 4444;
    private static final int wolfPort = 4445;
    private static final int dogPort = 4446;
    private static final int minerPort = 4447;
    private static final int plotsPort = 4448;

    private TPlace[][] myEnvironment;
    private EnvironmentGUI myGUI;
    private HashMap<String, TPosition> wolfList = new HashMap<>();
    private HashMap<String, TPosition> cowList = new HashMap<>();
    private HashMap<String, TPosition> dogList = new HashMap<>();
    private HashMap<String, TPosition> minerList = new HashMap<>();
    private HashMap<String, TPosition> obstacleList = new HashMap<>();
    private KillList[] wolvesKills;
    private KillList[] minersKills;
    private int simulationSpeed;
    private Socket cowSocket, wolfSocket, dogSocket, minerSocket, plotsSocket;
    private ServerSocket plotsServerSocket;
    private PrintStream cowSocketOutput, wolfSocketOutput, dogSocketOutput, minerSocketOutput, plotsSocketOutput;
    private BufferedReader cowSocketInput, wolfSocketInput, dogSocketInput, minerSocketInput, plotsSocketInput;
    private PlotData plotData;
    private int counter;

    public Simulation(int Cows, int Wolfs, int Dogs, int Miners, int Obstacles, boolean Plots, int speed) throws IOException {
        myEnvironment = new TPlace[15][15];
        int obstacles = Obstacles;
        int wolfs = Wolfs;
        int cows = Cows;
        int dogs = Dogs;
        int miners = Miners;
        simulationSpeed = speed;
        counter = 0;
        
        try {
            if (Cows > 0) {
                cowSocket = new Socket("localhost", cowPort);
                cowSocketOutput = new PrintStream(cowSocket.getOutputStream());
                cowSocketInput = new BufferedReader(new InputStreamReader(cowSocket.getInputStream()));
            }
            
            if (Wolfs > 0) {
                wolvesKills = new KillList[Wolfs];
                wolfSocket = new Socket("localhost", wolfPort);
                wolfSocketOutput = new PrintStream(wolfSocket.getOutputStream());
                wolfSocketInput = new BufferedReader(new InputStreamReader(wolfSocket.getInputStream()));
            }
            
            if (Dogs > 0) {
                dogSocket = new Socket("localhost", dogPort);
                dogSocketOutput = new PrintStream(dogSocket.getOutputStream());
                dogSocketInput = new BufferedReader(new InputStreamReader(dogSocket.getInputStream()));
            }
            
            if (Miners > 0) {
                minersKills = new KillList[Miners];
                minerSocket = new Socket("localhost", minerPort);
                minerSocketOutput = new PrintStream(minerSocket.getOutputStream());
                minerSocketInput = new BufferedReader(new InputStreamReader(minerSocket.getInputStream()));
            }
            
            if (Plots) {
                plotsServerSocket = new ServerSocket(plotsPort);
                plotsSocket = plotsServerSocket.accept();
                plotsSocketOutput = new PrintStream(plotsSocket.getOutputStream());
                plotsSocketInput = new BufferedReader(new InputStreamReader(plotsSocket.getInputStream()));
                
                plotData = new PlotData();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        
        generateEnvironment(obstacles, wolfs, cows, dogs, miners);
        /*
             * Start GUI
         */
        myGUI = new EnvironmentGUI();
        myGUI.updateGUI(myEnvironment);
        myGUI.setVisible(true);
    }

    private void generateEnvironment(int obstacles, int wolfs, int cows, int dogs, int miners) {
        startBase();
        putObstacles(obstacles);
        putWolfs(wolfs);
        putCows(cows);
        putDogs(dogs);
        putMiners(miners);
    }

    /*
     * Start all places with grass and position
     */
    private void startBase() {
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                myEnvironment[x][y] = new TPlace();
                Random rand = new Random();
                myEnvironment[x][y].setGrass(rand.nextInt((3 - 1) + 1) + 1);
                TPosition pos = new TPosition();
                pos.setXx(x);
                pos.setYy(y);
                myEnvironment[x][y].setPosition(pos);
            }
        }
    }

    /*
     * Put obstacles
     */
    private void putObstacles(int obstacles) {
        for (int i = 0; i < obstacles; i++) {
            int posX;
            int posY;
            do {
                Random randX = new Random();
                Random randY = new Random();
                posX = randX.nextInt((14 - 0) + 1) + 0;
                posY = randY.nextInt((14 - 0) + 1) + 0;
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() 
                    || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog() 
                    || myEnvironment[posX][posY].isMiner());
            myEnvironment[posX][posY].setObstacle(true);
            
            myEnvironment[posX][posY].setEntity("Obstacle_" + i);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);
            this.obstacleList.put("Obstacle_" + i, tPosition);
        }
    }

    /*
     * Put wolfs in the environment
     */
    private void putWolfs(int wolfs) {
        for (int i = 0; i < wolfs; i++) {
            int posX;
            int posY;
            do {
                Random randX = new Random();
                Random randY = new Random();
                posX = randX.nextInt((14 - 0) + 1) + 0;
                posY = randY.nextInt((14 - 0) + 1) + 0;
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() 
                    || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog() 
                    || myEnvironment[posX][posY].isMiner());
            myEnvironment[posX][posY].setWolf(true);

            myEnvironment[posX][posY].setEntity("Wolf_" + i);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);
            this.wolfList.put("Wolf_" + i, tPosition);
            
            this.wolvesKills[i] = new KillList();
        }
    }

    /*
     * Put cows in the environment
     */
    private void putCows(int cows) {
        int sex = 0;
        int CowID = 0;
        for (int i = 0; i < cows; i++) {
            int posX;
            int posY;
            do {
                Random randX = new Random();
                Random randY = new Random();
                posX = randX.nextInt((14 - 0) + 1) + 0;
                posY = randY.nextInt((14 - 0) + 1) + 0;
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() 
                    || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog() 
                    || myEnvironment[posX][posY].isMiner());
            myEnvironment[posX][posY].setCow(true);
            myEnvironment[posX][posY].setEntity("Cow_" + CowID);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);

            this.cowList.put("Cow_" + CowID, tPosition);
            CowID++;
        }
    }
    
    /*
     * Put dogs in the environment
     */
    private void putDogs(int dogs) {
        for (int i = 0; i < dogs; i++) {
            int posX;
            int posY;
            do {
                Random randX = new Random();
                Random randY = new Random();
                posX = randX.nextInt((14 - 0) + 1) + 0;
                posY = randY.nextInt((14 - 0) + 1) + 0;
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() 
                    || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog() 
                    || myEnvironment[posX][posY].isMiner());
            myEnvironment[posX][posY].setDog(true);
            myEnvironment[posX][posY].setEntity("Dog_" + i);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);

            this.dogList.put("Dog_" + i, tPosition);
        }
    }
    
    private void putMiners(int miner) {
        for (int i = 0; i < miner; i++) {
            int posX;
            int posY;
            do {
                Random randX = new Random();
                Random randY = new Random();
                posX = randX.nextInt((14 - 0) + 1) + 0;
                posY = randY.nextInt((14 - 0) + 1) + 0;
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() 
                    || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog() 
                    || myEnvironment[posX][posY].isMiner());
            myEnvironment[posX][posY].setMiner(true);
            myEnvironment[posX][posY].setEntity("Miner_" + i);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);

            this.minerList.put("Miner_" + i, tPosition);
            this.minersKills[i] = new KillList();
        }
    }

    /*
     * Update Grass
     */
    protected void updateGrass() {
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if (this.myEnvironment[x][y].isCow()) {
                    this.myEnvironment[x][y].setGrass(this.myEnvironment[x][y].getGrass() - 1);
                } else if (!myEnvironment[x][y].isWolf() && !this.myEnvironment[x][y].isObstacle() && this.myEnvironment[x][y].getGrass() < 3) {
                    this.myEnvironment[x][y].setGrass(this.myEnvironment[x][y].getGrass() + 1);
                }
            }
        }
    }

    /*
         * Create MyPlace for each entity (Wolf/Cow/Dog)
     */
    private TMyPlace createMyPlace(int posX, int posY) {
        TMyPlace myPlace = new TMyPlace();
        myPlace.getPlace().add(this.myEnvironment[posX][posY]);
        if (posY == 14 && posX == 0) {
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
        } else if (posY == 14 && posX == 14) {
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        } else if (posY == 0 && posX == 14) {
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        } else if (posY == 0 && posX == 0) {
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
        } else if (posY == 14) {
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        } else if (posX == 14) {
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        } else if (posY == 0) {
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        } else if (posX == 0) {
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(new TPlace());
            myPlace.getPlace().add(new TPlace());
        } else {
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY + 1]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY]);
            myPlace.getPlace().add(this.myEnvironment[posX + 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY - 1]);
            myPlace.getPlace().add(this.myEnvironment[posX - 1][posY]);
        }
        return myPlace;
    }

    @Override
    public void run() {

        while (true) {
            try {
                ArrayList<String> cowsToKill = new ArrayList<>();
                for (String cowName : this.cowList.keySet()) {
                    //call service to update Cow position
                    TMyPlace myNewPosition = updateCowPosition(createMyPlace(this.cowList.get(cowName).getXx(), this.cowList.get(cowName).getYy()));
                    TPlace myPlace = myNewPosition.getPlace().get(0); //New position
                    //updateCowPosition
                    int lastX = this.cowList.get(cowName).getXx(); //Last position Xx
                    int lastY = this.cowList.get(cowName).getYy(); //Last position Yy
                    if (!(this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf() //New position without Wolf
                            && this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].getGrass() == 0) //With grass
                            && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle() //Without obstacle
                            && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()    //Without cow
                            && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()
                            && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {  //Without dog
                        this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last postion
                        this.myEnvironment[lastX][lastY].setCow(false); //Remove from last position
                        this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(cowName); //Put in new position
                        this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setCow(true); //Put in new position
                        this.cowList.get(cowName).setXx(myPlace.getPosition().getXx()); //Put in new position
                        this.cowList.get(cowName).setYy(myPlace.getPosition().getYy()); //Put in new position
                    } else {
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) { //Wolf than eats a Cow
                            this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                            this.myEnvironment[lastX][lastY].setCow(false); //Remove from last position
                            cowsToKill.add(cowName);//Add to the list to remove later
                        }
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].getGrass() == 0) { //Without grass in position
                            this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                            this.myEnvironment[lastX][lastY].setCow(false); //Remove from last position
                            cowsToKill.add(cowName);//Add to the list to remove later
                        }
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()) { //With obstacle
                            if (this.myEnvironment[lastX][lastY].getGrass() == 0) { //No grass in last position
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                                this.myEnvironment[lastX][lastY].setCow(false);   //Remove from last position
                                cowsToKill.add(cowName);//Add to the list to remove later
                            }                                                                                      //else keep cow in the some position
                        }
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                            if (this.myEnvironment[lastX][lastY].getGrass() == 0) { //No grass in last position
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                                this.myEnvironment[lastX][lastY].setCow(false);   //Remove from last position
                                cowsToKill.add(cowName);//Add to the list to remove later
                            }    
                        }
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {
                            if (this.myEnvironment[lastX][lastY].getGrass() == 0) { //No grass in last position
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                                this.myEnvironment[lastX][lastY].setCow(false);   //Remove from last position
                                cowsToKill.add(cowName);//Add to the list to remove later
                            }    
                        }
                        if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) { //Another cow
                            if (this.myEnvironment[lastX][lastY].getGrass() == 0) {
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove from last position
                                this.myEnvironment[lastX][lastY].setCow(false);   //Remove from last position
                                cowsToKill.add(cowName);        //Add to the list to remove later
                            }                                                                                      //else keep cow in the some position
                        }
                    }
                }
                //Remove Cow from HashMap
                for (String cowID : cowsToKill) {
                    this.cowList.remove(cowID);
                }

                //send update for all wolfs
                ArrayList<String> wolfsToKill = new ArrayList<>();
                for (String wolfName : this.wolfList.keySet()) {
                    TMyPlace myNewPosition = updateWolfPosition(createMyPlace(this.wolfList.get(wolfName).getXx(), this.wolfList.get(wolfName).getYy()));
                    TPlace myPlace = myNewPosition.getPlace().get(0); //New position
                    //updateWolfPosition
                    if (this.wolfList.containsKey(wolfName)) { //Search for the name in the hashmap (Wolf)        

                        int lastX = this.wolfList.get(wolfName).getXx();   //Last position Xx
                        int lastY = this.wolfList.get(wolfName).getYy();   //Last position Yy

                        if (!(this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) //Movement to an empty position 
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                            this.myEnvironment[lastX][lastY].setEntity(null);
                            this.myEnvironment[lastX][lastY].setWolf(false);

                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(wolfName);
                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setWolf(true);
                            this.wolfList.get(wolfName).setXx(myPlace.getPosition().getXx());
                            this.wolfList.get(wolfName).setYy(myPlace.getPosition().getYy());

                        } else {
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) {
                                //Wolf in the same position
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()) {
                                //Moving to obstacle
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                                //Moving to dog
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {
                                //Moving to miner
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {
                                String cowName = this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].getEntity();
                                
                                int wolfID = Integer.parseInt(String.valueOf(wolfName.charAt(wolfName.length() - 1)));
                                wolvesKills[wolfID].addKill(cowName);
                                
                                //UpdateTable
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove wolf from last position
                                this.myEnvironment[lastX][lastY].setWolf(false);  //Remove wolf from last position
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(wolfName);
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setWolf(true);

                                this.wolfList.get(wolfName).setXx(myPlace.getPosition().getXx());
                                this.wolfList.get(wolfName).setYy(myPlace.getPosition().getYy());
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setCow(false);
                                this.cowList.remove(cowName);
                            }
                        }
                    }

                }
                //Remove Wolfs from HashMap
                for (String wolfID : wolfsToKill) {
                    this.wolfList.remove(wolfID);
                }
                
                //send update for all dogs
                ArrayList<String> dogsToKill = new ArrayList<>();
                for (String dogName : this.dogList.keySet()) {
                    TMyPlace myNewPosition = updateDogPosition(createMyPlace(this.dogList.get(dogName).getXx(), this.dogList.get(dogName).getYy()));
                    TPlace myPlace = myNewPosition.getPlace().get(0); //New position
                    //updateDogPosition
                    if (this.dogList.containsKey(dogName)) { //Search for the name in the hashmap (Wolf)        

                        int lastX = this.dogList.get(dogName).getXx();   //Last position Xx
                        int lastY = this.dogList.get(dogName).getYy();   //Last position Yy

                        if (!(this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) //Movement to an empty position 
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                            this.myEnvironment[lastX][lastY].setEntity(null);
                            this.myEnvironment[lastX][lastY].setDog(false);

                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(dogName);
                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setDog(true);
                            this.dogList.get(dogName).setXx(myPlace.getPosition().getXx());
                            this.dogList.get(dogName).setYy(myPlace.getPosition().getYy());

                        } else {
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                                //Dog in the same position
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()) {
                                //Moving to obstacle
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) {
                                //Moving to wolf
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {
                                //Moving to cow
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {
                                //Moving to miner
                            }
                        }
                    }

                }
                
                //Remove dogs from HashMap
                for (String dogID : dogsToKill) {
                    this.dogList.remove(dogID);
                }
                
                //send update for all obstacles
                ArrayList<String> obstaclesToKill = new ArrayList<>();
                for (String obstacleName : this.obstacleList.keySet()) {
                    TPosition myPosition = new TPosition();
                    myPosition.setXx(obstacleList.get(obstacleName).getXx());
                    myPosition.setYy(obstacleList.get(obstacleName).getYy());
                    if (this.myEnvironment[myPosition.getXx()][myPosition.getYy()].isMiner()) {
                        String minerName = this.myEnvironment[myPosition.getXx()][myPosition.getYy()].getEntity();
                        
                        this.myEnvironment[myPosition.getXx()][myPosition.getYy()].setEntity(null);
                        this.myEnvironment[myPosition.getXx()][myPosition.getYy()].setObstacle(false);
                        
                        obstaclesToKill.add(obstacleName);
                        
                        int minerID = Integer.parseInt(String.valueOf(minerName.charAt(minerName.length() - 2)));
                        minersKills[minerID].addKill(obstacleName);
                    }
                    myPosition.setYy(this.obstacleList.get(obstacleName).getYy());

                }
                //Remove obstacles from HashMap
                for (String obstacleID : obstaclesToKill) {
                    this.minerList.remove(obstacleID);
                }
                
                //send update for all miners
                ArrayList<String> minersToKill = new ArrayList<>();
                for (String minerName : this.minerList.keySet()) {
                    TMyPlace myNewPosition = updateMinerPosition(createMyPlace(this.minerList.get(minerName).getXx(), this.minerList.get(minerName).getYy()));
                    TPlace myPlace = myNewPosition.getPlace().get(0); //New position
                    //updateDogPosition
                    if (this.minerList.containsKey(minerName)) { //Search for the name in the hashmap (Wolf)        

                        int lastX = this.minerList.get(minerName).getXx();   //Last position Xx
                        int lastY = this.minerList.get(minerName).getYy();   //Last position Yy

                        if (!(this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) //Movement to an empty position 
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {
                            this.myEnvironment[lastX][lastY].setEntity(null);
                            this.myEnvironment[lastX][lastY].setMiner(false);

                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(minerName);
                            this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setMiner(true);
                            this.minerList.get(minerName).setXx(myPlace.getPosition().getXx());
                            this.minerList.get(minerName).setYy(myPlace.getPosition().getYy());

                        } else {
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isDog()) {
                                //Dog in the same position
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isObstacle()) {
                                String obstacleName = this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].getEntity();
                                
                                int minerID = Integer.parseInt(String.valueOf(minerName.charAt(minerName.length() - 1)));
                                minersKills[minerID].addKill(obstacleName);
                                
                                //UpdateTable
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove wolf from last position
                                this.myEnvironment[lastX][lastY].setMiner(false);  //Remove wolf from last position
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(minerName);
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setMiner(true);

                                this.minerList.get(minerName).setXx(myPlace.getPosition().getXx());
                                this.minerList.get(minerName).setYy(myPlace.getPosition().getYy());
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setObstacle(false);
                                this.obstacleList.remove(obstacleName);
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isWolf()) {
                                //Moving to wolf
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {
                                //Moving to cow
                            }
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isMiner()) {
                                //Moving to miner
                            }
                        }
                    }

                }
                //Remove miners from HashMap
                for (String minerID : minersToKill) {
                    this.minerList.remove(minerID);
                }

                this.myGUI.updateGUI(this.myEnvironment);
                if (this.counter % 2 == 0)
                    this.updateGrass();
                this.counter++;
                
                if (plotsSocket != null) {
                    Gson gson = new Gson();
                    String request = plotsSocketInput.readLine();
                    PlotData requestPlot = gson.fromJson(request, PlotData.class);
                    
                    plotData = updatePlotData(plotData);
                    
                    String response;
                    if (requestPlot.equals(plotData))
                        response = request;
                    else
                        response = gson.toJson(plotData, PlotData.class);

                    plotsSocketOutput.println(response);
                }

                Thread.sleep(simulationSpeed);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillCount.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(KillCount.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KillCount.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private TMyPlace updateCowPosition(TMyPlace currentMyPlace) throws JAXBException, IOException {
        //Lab 2:
        //Serialize and deserialize TMyPlace Object to verify if the the methods from MessageManagement are properly working
        // String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        // TMyPlace unserialized = MessageManagement.retrievePlaceStateObject(serialized);
        
        //TODO Lab 3 & 4:
        //Serialize TMyPlace object to string
        String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        
        //call server socket to update cow position
        cowSocketOutput.println(serialized);
        
        //Read content received from server (Cow Socket)
        String received = cowSocketInput.readLine().trim();

        //Deserilize result string to TMyPlace and return it
        return MessageManagement.retrievePlaceStateObject(received);
    }

    private TMyPlace updateWolfPosition(TMyPlace currentMyPlace) throws JAXBException, IOException {
        //TODO Lab 2:
        //Serialize and deserialize TMyPlace Object to verify if the the methods from MessageManagement are properly working
        
        // String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        // TMyPlace unserialized = MessageManagement.retrievePlaceStateObject(serialized);
        
        //TODO Lab 3 & 4:
        //Serialize TMyPlace object to string
        String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        //call server socket to update cow position
        wolfSocketOutput.println(serialized);
        
        //Read content received from client (Simulation)      
        String received = wolfSocketInput.readLine().trim();
        
        //Deserilize result string to TMyPlace and return it
        return MessageManagement.retrievePlaceStateObject(received);
    }
    
    private TMyPlace updateDogPosition(TMyPlace currentMyPlace) throws JAXBException, IOException {
        //TODO Lab 2:
        //Serialize and deserialize TMyPlace Object to verify if the the methods from MessageManagement are properly working
        
        // String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        // TMyPlace unserialized = MessageManagement.retrievePlaceStateObject(serialized);
        
        //TODO Lab 3 & 4:
        //Serialize TMyPlace object to string
        String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        //call server socket to update cow position
        dogSocketOutput.println(serialized);
        
        //Read content received from client (Simulation)      
        String received = dogSocketInput.readLine().trim();
        
        //Deserilize result string to TMyPlace and return it
        return MessageManagement.retrievePlaceStateObject(received);
    }
    
    private TMyPlace updateMinerPosition(TMyPlace currentMyPlace) throws JAXBException, IOException {
        //TODO Lab 2:
        //Serialize and deserialize TMyPlace Object to verify if the the methods from MessageManagement are properly working
        
        // String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        // TMyPlace unserialized = MessageManagement.retrievePlaceStateObject(serialized);
        
        //TODO Lab 3 & 4:
        //Serialize TMyPlace object to string
        String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        //call server socket to update cow position
        minerSocketOutput.println(serialized);
        
        //Read content received from client (Simulation)      
        String received = minerSocketInput.readLine().trim();
        
        //Deserilize result string to TMyPlace and return it
        return MessageManagement.retrievePlaceStateObject(received);
    }
    
    private PlotData updatePlotData(PlotData plotData) {
        plotData.setCowsAlive(cowList.size());
        plotData.setWolvesAlive(wolfList.size());
        plotData.setDogsAlive(dogList.size());
        plotData.setMinersAlive(minerList.size());
        plotData.setObstaclesAlive(obstacleList.size());
        
        
        for (int i = 0; i < wolvesKills.length; i++) {
            plotData.addWolfKills("Wolf_" + i, wolvesKills[i].getKills().size());
        }
        
        for (int i = 0; i < minersKills.length; i++) {
            plotData.addMinerKills("Miner_" + i, minersKills[i].getKills().size());
        }
        
        return plotData;
    }
}

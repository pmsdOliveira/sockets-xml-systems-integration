/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Common.MessageManagement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
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

    private TPlace[][] myEnvironment;
    private EnvironmentGUI myGUI;
    private HashMap<String, TPosition> wolfList = new HashMap<>();
    private HashMap<String, TPosition> cowList = new HashMap<>();
    private int simulationSpeed;
    private static Socket cowSocket, wolfSocket;
    private static PrintStream cowSocketOutput, wolfSocketOutput;

    public Simulation(int Cows, int Wolfs, int Obstacles, int speed) {
        myEnvironment = new TPlace[15][15];
        int obstacles = Obstacles;
        int wolfs = Wolfs;
        int cows = Cows;
        simulationSpeed = speed;
        
        try {
            cowSocket = new Socket("localhost", cowPort);
            wolfSocket = new Socket("localhost", wolfPort);
            cowSocketOutput = new PrintStream(cowSocket.getOutputStream());
            wolfSocketOutput = new PrintStream(wolfSocket.getOutputStream());
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        
        generateEnvironment(obstacles, wolfs, cows);
        /*
             * Start GUI
         */
        myGUI = new EnvironmentGUI();
        myGUI.updateGUI(myEnvironment);
        myGUI.setVisible(true);
    }

    private void generateEnvironment(int obstacles, int wolfs, int cows) {
        startBase();
        putObstacles(obstacles);
        putWolfs(wolfs);
        putCows(cows);
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
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog());
            myEnvironment[posX][posY].setObstacle(true);
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
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog());
            myEnvironment[posX][posY].setWolf(true);

            myEnvironment[posX][posY].setEntity("Wolf_" + i);
            TPosition tPosition = new TPosition();
            tPosition.setXx(posX);
            tPosition.setYy(posY);
            this.wolfList.put("Wolf_" + i, tPosition);
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
            } while (myEnvironment[posX][posY].isObstacle() || myEnvironment[posX][posY].isCow() || myEnvironment[posX][posY].isWolf() || myEnvironment[posX][posY].isDog());
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
                            && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {  //Without cow
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
                                && !this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {
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
                            if (this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].isCow()) {
                                String lastEntity = this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].getEntity(); //Entity of Cow in the new positin

                                //UpdateTable
                                this.myEnvironment[lastX][lastY].setEntity(null); //Remove wolf from last position
                                this.myEnvironment[lastX][lastY].setWolf(false);  //Remove wolf from last position
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setEntity(wolfName);
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setWolf(true);

                                this.wolfList.get(wolfName).setXx(myPlace.getPosition().getXx());
                                this.wolfList.get(wolfName).setYy(myPlace.getPosition().getYy());
                                this.myEnvironment[myPlace.getPosition().getXx()][myPlace.getPosition().getYy()].setCow(false);
                                this.cowList.remove(lastEntity);
                            }
                        }
                    }

                }
                //Remove Wolfs from HashMap
                for (String wolfID : wolfsToKill) {
                    this.wolfList.remove(wolfID);
                }

                this.myGUI.updateGUI(this.myEnvironment);
                this.updateGrass();

                Thread.sleep(simulationSpeed);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
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
        // String serialized = MessageManagement.createPlaceStateContent(currentMyPlace);
        //call server socket to update cow position
        // cowSocketOutput.println(serialized);
        System.out.println(cowSocket);
        
        //cowSocketOutput.println("SIMULATION INSIDE ANOTHER SIMULATION </tMyPlace>");
        //Read content received from client (Simulation)
        //BufferedReader received = new BufferedReader(new InputStreamReader(cowSocket.getInputStream()));
        
//        String line, message = "";
//        while (!(line = received.readLine()).equals(""))
//            message += line;
        //String message = received.readLine();
        //System.out.println(message);
        
        //Deserilize result string to TMyPlace and return it
        //return MessageManagement.retrievePlaceStateObject(message);
        return currentMyPlace;
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
        BufferedReader received = new BufferedReader(new InputStreamReader(wolfSocket.getInputStream()));
        
        String line, message = "";
        while (!(line = received.readLine()).equals(""))
            message += line;
        
        //Deserilize result string to TMyPlace and return it
        return MessageManagement.retrievePlaceStateObject(message);
    }
}

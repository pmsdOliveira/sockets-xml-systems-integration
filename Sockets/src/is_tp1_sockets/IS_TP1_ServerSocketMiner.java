/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is_tp1_sockets;

import Common.MessageManagement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.netbeans.xml.schema.updateschema.TMyPlace;
import org.netbeans.xml.schema.updateschema.TPlace;
import org.netbeans.xml.schema.updateschema.TPosition;

/**
 *
 * @author utilizador
 */
public class IS_TP1_ServerSocketMiner {
    
    private static final int portServer = 4447;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    
    
    public static void main(String[] args) throws Exception {
        IS_TP1_ServerSocketMiner server = new IS_TP1_ServerSocketMiner();
        server.run();
    }
    
//    private static double euclidianDistance(TPosition a, TPosition b) {
//        return Math.sqrt(Math.pow(a.getXx() - b.getXx(), 2) + Math.pow(a.getYy() - b.getYy(), 2));
//    }
//    
    private static TPosition randomTPositionFromList(List<TPosition> positions) {
        return positions.get(new Random().nextInt(positions.size()));
    }
    
    private static TMyPlace updateMinerPosition(TMyPlace currentMyPlace){
        
        TMyPlace nextMyPlace = currentMyPlace;

            List<TPlace> places = nextMyPlace.getPlace();

            List<TPosition> obstaclesPositions = places.stream().filter(place -> place.isObstacle())
                .map(validPlace -> validPlace.getPosition()).collect(Collectors.toList());

            if (obstaclesPositions.size() > 0)
            {
                nextMyPlace.getPlace().get(0).setPosition(randomTPositionFromList(obstaclesPositions));
            }
            else
            {
                List<TPosition> validPositions = places.stream()
                    .filter(place -> // borders, miners, empty grass, wolves and cows filtered out
                            place.getPosition() != null && !place.isObstacle() 
                                    && !place.isWolf()  && !place.isCow()
                                    && !place.isDog() && !place.isMiner() 
                    ).map(validPlace -> validPlace.getPosition())
                    .collect(Collectors.toList());
                validPositions.add(places.get(0).getPosition());
               
                nextMyPlace.getPlace().get(0).setPosition(randomTPositionFromList(validPositions));
            }

            return nextMyPlace;
    }
    
     

    public void run() throws Exception {

        //Start your server socket
        ServerSocket server = new ServerSocket(portServer);
        System.out.println("Server open on port " + portServer);
        
        Socket connection = server.accept();
        BufferedReader received = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
        while (!connection.isClosed()) {
            //Read content received from client (Simulation)
            
            String message = received.readLine();
            
            TMyPlace currentMyPlace = MessageManagement.retrievePlaceStateObject(message);
            
            TMyPlace nextMyPlace = updateMinerPosition(currentMyPlace);
            
            //Send new miner position to the client (Simulation)
            String serialized = MessageManagement.createPlaceStateContent(nextMyPlace);
            
            
            PrintStream output = new PrintStream(connection.getOutputStream(), true);
            output.println(serialized);
        }
    }
    
}

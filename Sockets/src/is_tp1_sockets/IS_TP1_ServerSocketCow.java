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
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.netbeans.xml.schema.updateschema.TMyPlace;
import org.netbeans.xml.schema.updateschema.TPlace;
import org.netbeans.xml.schema.updateschema.TPosition;

/**
 *
 * @author adroc
 */
public class IS_TP1_ServerSocketCow {

    private static final int portServer = 4444;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        IS_TP1_ServerSocketCow server = new IS_TP1_ServerSocketCow();
        server.run();
    }
    
    private double cityBlock(TPosition a, TPosition b) {
        return Math.abs(a.getXx() - b.getXx()) + Math.abs(a.getYy() - b.getYy());
    }
    
    private TPosition randomTPositionFromList(List<TPosition> positions) {
        return positions.get(new Random().nextInt(positions.size()));
    }

    public void run() throws Exception {

        //TODO - Lab3
        //Start your server socket
        ServerSocket server = new ServerSocket(portServer);
        System.out.println("Server open on port " + portServer);
        
        Socket connection = server.accept();
        BufferedReader received = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
        while (!connection.isClosed()) {
            //Read content received from client (Simulation)
            String line, message = "";
            while (!(line = received.readLine()).equals("")) {
                message += line;
            }

            TMyPlace currentMyPlace = MessageManagement.retrievePlaceStateObject(message);

            //Calculate new Cow position
            //Lab 1:
            //Update the position of the cow directly in this method

            TMyPlace nextMyPlace = currentMyPlace;

            List<TPlace> places = currentMyPlace.getPlace();
            TPlace currentPlace = places.remove(0); // center (0) treated different because isCow == true
            List<TPlace> neighbours = new ArrayList<>(places);

            //TODO: IMPLEMENT A STAMINA SYSTEM, EXAMPLE:
                // Stamina starts at 100
                // Each movement spends 5 stamina
                // Staying restores 10 stamina

            //TODO: GET OPPOSITE SEX COWS POSITIONS FOR LATER BREEDING

            List<TPosition> wolvesPositions = neighbours.stream()
                    .filter(neighbour -> neighbour.isWolf())
                    .map(validNeighbour -> validNeighbour.getPosition())
                    .collect(Collectors.toList());

            List<TPosition> validPositions = neighbours.stream()
                    .filter(neighbour -> // borders, obstacles, empty grass, wolves and cows filtered out
                            neighbour.getPosition() != null && !neighbour.isObstacle() 
                                    && neighbour.getGrass() > 0 && !neighbour.isWolf()
                                    && !neighbour.isCow()
                    ).map(validNeighbour -> validNeighbour.getPosition())
                    .collect(Collectors.toList());       

            if (currentPlace.getGrass() > 0) {  // check if staying is valid
                validPositions.add(currentPlace.getPosition());
            }

            if (validPositions.size() > 0) {    // if there is any valid position
                //TODO: CHOOSE POSITION FOR BREEDING

                double maxDistance = 0;
                TPosition maxDistancePosition = new TPosition();
                if (wolvesPositions.size() > 0) {
                    for (TPosition validPosition : validPositions) {
                        double distance = 0;
                        for (TPosition wolfPosition : wolvesPositions) {
                            distance += cityBlock(validPosition, wolfPosition);
                        }

                        double averageDistance = distance / wolvesPositions.size();
                        if (averageDistance > maxDistance) {
                            maxDistance = averageDistance;
                            maxDistancePosition = validPosition;
                        }
                    }

                    nextMyPlace.getPlace().get(0).setPosition(maxDistancePosition);
                } else {
                    TPosition selectedValidPosition = randomTPositionFromList(validPositions);
                    nextMyPlace.getPlace().get(0).setPosition(selectedValidPosition);
                }
            }

            //Send new cow position to the client (Simulation)
            String serialized = MessageManagement.createPlaceStateContent(nextMyPlace);

            PrintStream output = new PrintStream(connection.getOutputStream(), true);
            output.println(serialized);
        }
    }
}

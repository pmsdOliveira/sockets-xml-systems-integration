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
public class IS_TP1_ServerSocketWolf {

    private static final int portServer = 4445;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        IS_TP1_ServerSocketWolf server = new IS_TP1_ServerSocketWolf();
        server.run();
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
            
            //Update the position of the wolf directly in this method
        
            // Wolf looks for cows. If found, chooses the closest valid position
            // If no cows found, go to a random valid position (inside borders, no obstacle or wolf)

            TMyPlace nextMyPlace = currentMyPlace;
            
            List<TPlace> places = nextMyPlace.getPlace();     

            //TODO: IMPLEMENT A STAMINA SYSTEM, EXAMPLE:
                // Stamina starts at 100
                // Each movement spends 1 stamina
                // Staying restores 1 stamina
                // Eating a cow restores 50 stamina

            //TODO: GET DOGS POSITIONS TO LATER RUN

            List<TPosition> cowsPositions = places.stream()
                    .filter(place -> place.isCow())
                    .map(cow -> cow.getPosition())
                    .collect(Collectors.toList());

            if (cowsPositions.size() > 0) { // choose random cow and move to it
                TPosition selectedCowPosition = randomTPositionFromList(cowsPositions);
                nextMyPlace.getPlace().get(0).setPosition(selectedCowPosition);
            }

            List<TPosition> validPositions = places.stream()
                    .filter(place -> // borders, obstacles and wolves filtered out
                            place.getPosition() != null && !place.isObstacle() && !place.isWolf()
                    ).map(validPlace -> validPlace.getPosition())
                    .collect(Collectors.toList());
            validPositions.add(places.get(0).getPosition());

            TPosition selectedValidPosition = randomTPositionFromList(validPositions);
            nextMyPlace.getPlace().get(0).setPosition(selectedValidPosition);
            
            //Send new wolf position to the client (Simulation)
            String serialized = MessageManagement.createPlaceStateContent(nextMyPlace);

            PrintStream output = new PrintStream(connection.getOutputStream(), true);
            output.println(serialized);
        }
    }
}

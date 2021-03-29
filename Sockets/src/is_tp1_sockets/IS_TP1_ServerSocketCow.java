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
import java.util.Random;
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

    public void run() throws Exception {

        //TODO - Lab3
        //Start your server socket
        ServerSocket server = new ServerSocket(portServer);
        System.out.println("Server open on port " + portServer);
        
        Socket client = server.accept();
        BufferedReader received = new BufferedReader(new InputStreamReader(client.getInputStream()));
        
        String line, message = "";
        while (!(line = received.readLine()).equals("")) {
            message += line;
        }
        
        TMyPlace unserialized = MessageManagement.retrievePlaceStateObject(message);
        System.out.println(unserialized.getPlace().get(0).getEntity());
        
        //Read content received from client (Simulation)
        //Calculate new Cow position
        //Send new cow position to the client (Simulation)
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import JSON.PlotData;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Utilizador
 */
public class SocketTest {
    private static final int portServer = 4448;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        SocketTest server = new SocketTest();
        server.run();
    }

    public void run() throws Exception {

        ServerSocket server = new ServerSocket(portServer);
        System.out.println("Server open on port " + portServer);
        
        Socket connection = server.accept();
        
        BufferedReader socketInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        PrintStream socketOutput = new PrintStream(connection.getOutputStream());
        
        while (!connection.isClosed()) {
            String request = socketInput.readLine();
            System.out.println(request);
            
            Gson gson = new Gson();
            PlotData requestPlot = gson.fromJson(request, PlotData.class);
            
            String response = gson.toJson(requestPlot, PlotData.class);
            System.out.println(response);
            
            socketOutput.println(response);
        }
    }

}

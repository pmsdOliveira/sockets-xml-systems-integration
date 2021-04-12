/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Utilizador
 */
public class Test {
    private static final int cowPort = 4444;
    
    private static Socket cowSocket;
    private static PrintStream cowSocketOutput;
    
    Test() {
        try {
            cowSocket = new Socket("localhost", cowPort);
            cowSocketOutput = new PrintStream(cowSocket.getOutputStream());
            
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public static void main(String[] args) throws IOException {
        Test test = new Test();
        
        cowSocketOutput.println("MENSAGEM </tMyPlace>");
        
        BufferedReader received = new BufferedReader(new InputStreamReader(cowSocket.getInputStream()));
        
        String message = received.readLine();
        System.out.println(message);
    }
}

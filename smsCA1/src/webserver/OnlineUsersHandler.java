/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class OnlineUsersHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        
        String response; //String needed for the printwriter

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>En to tre</title>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        
        sb.append("Users online:" + onlineUsers());

        sb.append("</body>\n");
        sb.append("</html>\n");
        response = sb.toString();
        he.sendResponseHeaders(200, response.length());
        try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
            pw.print(response); //What happens if we use a println instead of print --> Explain
        }

    }

    private int onlineUsers() throws UnknownHostException, IOException {
        
        String result = "";
        
        byte[] bytesToSend = new byte[(int) "whosonline#".length()];
        byte[] bytesToRecive = new byte[(int) "whosonline#".length()];
        try {
            DatagramSocket socket = new DatagramSocket();

            InetAddress IPAddress = InetAddress.getByName("localhost");

            DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, IPAddress, 9999);

            socket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(bytesToRecive, bytesToRecive.length);

            socket.receive(receivePacket);
            
            
            result = new String(receivePacket.getData());
        } catch (SocketException ex) {
            Logger.getLogger(OnlineUsersHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Integer.parseInt(result);
    }

}

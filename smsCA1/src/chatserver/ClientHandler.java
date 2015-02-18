/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author MS
 */
public class ClientHandler extends Thread {

    Socket socket;
    Scanner input;
    String username;
    PrintWriter output;
    Server server;

    public ClientHandler(String username, Socket socket, Server server) {
        try {
            this.username = username;
            this.socket = socket;
            this.server = server;
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void send(String message) {
        output.println(message);
    }

    public void run() {

        try {
            String message = input.nextLine(); //IMPORTANT blocking call
            Logger.getLogger(Server.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {

                String[] protocols = message.split(ProtocolStrings.separator);

                String index = protocols[0];

                System.out.println("view over array: "+Arrays.toString(protocols));

                switch (index) {
                    case "SEND":
                        server.sendTo(protocols[1], username, protocols[2]);
                        break;
                    case "CLOSE":
                        server.closeUser(username);
                        server.removeHandler(username);
                        socket.close();
                }

                message = input.nextLine(); //IMPORTANT blocking call

                Logger.getLogger(Server.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));

            }
            output.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown

//            socket.close();
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.removeHandler(username);
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Closed a Connection");
            try {
                socket.close();
            } catch (IOException ex) {

            }
        }

    }

}

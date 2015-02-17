/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
/**
 *
 * @author Søren
 */
public class Server {

    private boolean serverRunning = true;
    private ServerSocket serverSocket;
    private HashMap<String, ClientHandler> clientMap;

    public Server(String ip, int port) {

        clientMap = new HashMap<>();

        //opretter en ny server
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void usersOnline() {

        String online = ProtocolStrings.online + ProtocolStrings.separator;

        for (String client : clientMap.keySet()) {
            //Spørg om dette, komma tilsidst. 
            online += client + ",";
        }

        for (ClientHandler clientHandler : clientMap.values()) {
            clientHandler.send(online);
        }
    }

    public void sendTo(String modtager, String afsender, String msg) {

        //splits the user String, to see if there are more than one.
        String[] users = modtager.split(",");
        //MESSAGE#
        String type = ProtocolStrings.message + ProtocolStrings.separator;
        //user#
        String from = afsender + ProtocolStrings.separator;
        //MESSAGE#user#msg
        String message = type + from + msg;

        for (String user : users) {
            if (modtager.equals("*")) {
                send(message);

            } else {
                System.out.println(user + "user");
                ClientHandler clientHandler = clientMap.get(user);
                if (clientHandler != null) {
                    clientHandler.send(message);
                } else {
                    removeHandler(modtager);
                    usersOnline();
                }
            }
        }

    }

    public void send(String msg) {
        for (ClientHandler clientHandler : clientMap.values()) {
            clientHandler.send(msg);
        }
    }

    public void removeHandler(String username) {
        clientMap.remove(username);
        usersOnline();
    }

    private void startServer() {

        PrintWriter output;
        Scanner input;
        Socket socket;

        while (serverRunning) {

            try {
                //Blocking call, waits on clients to connect.
                socket = serverSocket.accept();
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Connected to a client");

                //Blocking call, waits for the connect protocol string and username.
                input = new Scanner(socket.getInputStream());
                String incomingUser = input.nextLine();

                //Splits the incoming msg, checks for the first part equals connect.
                String[] connectUser = incomingUser.split(ProtocolStrings.separator);

                String connectionAllowed = connectUser[0];

                String username = connectUser[1];

                if (connectionAllowed.equalsIgnoreCase("connect")) {
                    ClientHandler clientHandler = new ClientHandler(username, socket, this);
                    clientMap.put(username, clientHandler);
                    clientHandler.start();
                    usersOnline();
                } else {
                    output = new PrintWriter(socket.getOutputStream());
                    output.println("Fik ikke " + ProtocolStrings.connect);
                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public static void main(String[] args) {

        //Hardcoded skal laves om.
        String ip = "localhost";
        int port = 8999;

        //Starts the server.
        Server server = new Server(ip, port);
        server.startServer();
    }

}

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
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;

/**
 *
 * @author Søren
 */
public class Server {

    private static final Properties properties = Utils.initProperties("server.properties");
    private boolean serverRunning = true;
    private ServerSocket serverSocket;
    private HashMap<String, ClientHandler> clientMap;
    private UDPSocket udp;

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

        String online = ProtocolStrings.ONLINE + ProtocolStrings.SEPERATOR;

        String cleanedUsers = "";

        for (String client : clientMap.keySet()) {
            online += client + ",";
        }
        if (clientMap.size() >= 1) {
            cleanedUsers = online.substring(0, online.lastIndexOf(","));
        }

        udp.updateOnline(cleanedUsers);

        for (ClientHandler clientHandler : clientMap.values()) {
            clientHandler.send(cleanedUsers);
        }
    }

    public void sendTo(String modtager, String afsender, String msg) {

        //splits the user String, to see if there are more than one.
        String[] users = modtager.split(",");
        //MESSAGE#
        String type = ProtocolStrings.MESSAGE + ProtocolStrings.SEPERATOR;
        //user#
        String from = afsender + ProtocolStrings.SEPERATOR;
        //MESSAGE#user#msg
        String message = type + from + msg;

        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Message send: {0} to: {1}", new Object[]{message, modtager});

        for (String user : users) {
            if (modtager.equals("*")) {
                send(message);

            } else {
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

    public void closeUser(String user) {

        ClientHandler client = clientMap.get(user);

        String closeMsg = ProtocolStrings.CLOSE + ProtocolStrings.SEPERATOR;

        client.send(closeMsg);

    }

    public void send(String msg) {
        for (ClientHandler clientHandler : clientMap.values()) {
            clientHandler.send(msg);
        }
    }

    public void removeHandler(String username) {
        clientMap.remove(username);
        Logger.getLogger(Server.class.getName()).log(Level.INFO, "{0} removed from the server!", username);
        usersOnline();
    }

    private void startServer() {

        udp = new UDPSocket();
        udp.start();
        PrintWriter output;
        Scanner input;
        Socket socket;

        while (serverRunning) {

            try {
                String username = "";
                //Blocking call, waits on clients to connect.
                socket = serverSocket.accept();
//                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Connected to a client");

                //Blocking call, waits for the connect protocol string and username.
                input = new Scanner(socket.getInputStream());
                String incomingUser = input.nextLine();

                //Splits the incoming msg, checks for the first part equals connect.
                String[] connectUser = incomingUser.split(ProtocolStrings.SEPERATOR);

                String connectionAllowed = connectUser[0];

                if (connectUser.length > 1) {
                    username = connectUser[1];
                }

                if (connectionAllowed.equalsIgnoreCase("connect")) {
                    ClientHandler clientHandler = new ClientHandler(username, socket, this);
                    clientMap.put(username, clientHandler);
                    clientHandler.start();
                    usersOnline();
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "{0} - Connected!", username);
                } else {
                    output = new PrintWriter(socket.getOutputStream());
                    output.println("Fik ikke " + ProtocolStrings.CONNECT);
                    socket.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        Utils.closeLogger(Server.class.getName());
    }

    public static void main(String[] args) {

        //Getting ip and port from properties 
        String ip = properties.getProperty("serverIp");
        int port = Integer.parseInt(properties.getProperty("port"));

        //setting up Log file
        String logFile = properties.getProperty("logFile");
        Utils.setLogFile(logFile, Server.class.getName());

        //Starts the server.
        Server server = new Server(ip, port);
        server.startServer();

    }

}

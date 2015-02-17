/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import shared.*;

/**
 *
 * @author Søren
 */
public class Client extends Thread {

    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private List<ClientObserver> listeners = new ArrayList();
    private String userName;

    public void connect(String userName, String host, int port) throws UnknownHostException, IOException {
        this.userName = userName;
        //Method for establishing a a connection and creating the socket used for I/O.
        InetAddress serverAddress = InetAddress.getByName(host);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);//Autoflush is set to true

        //First message to the server is the CONNECT#Username
        String outputMes = ProtocolStrings.connect + ProtocolStrings.separator + this.userName;
        output.println(outputMes);
        //Then the first expected message is a ONLINE#users with the currently online users
        String inputMsg = input.nextLine();//This is a blocking call, waiting for the verified connection approval from the server
        String[] inputMsgSplit = inputMsg.split(ProtocolStrings.separator);
        if (inputMsgSplit.length > 0) {//If the array is larger than zero, the client can handle the recieved message
            if (inputMsgSplit[0].equals(ProtocolStrings.online)) {
                String users = inputMsgSplit[1]; //A string with the online users should be on index 1
                notifyListenersOnlineUsers(users);//Notifies observers that the user list is updated
            }
        }
        //INPUT HANDLECODE FOR INVALD INPUT/CONNECTION

    }

    @Override
    public void run() {

        String msg = input.nextLine();//Blocking call
        while (!msg.equals(ProtocolStrings.STOP)) {
            handleMessage(msg);
            msg = input.nextLine();//Blocking call
        }
        try {
            socket.close();
        } catch (IOException ex) {
            //INSERT LOG HERE
        }
        //INSERT CODE FOR STOP

    }

    public String getUserName() {
        return userName;
    }

    public void send(String mes, String users) {

        String outputMsg = ProtocolStrings.send + ProtocolStrings.separator + users + ProtocolStrings.separator + mes;
        output.println(outputMsg);

    }

    private void handleMessage(String inputMsg) {

        String[] inputMsgSplit = inputMsg.split(ProtocolStrings.separator);
        if (inputMsgSplit.length > 0) {//If the array is larger than zero, the client can handle the recieved message
            if (inputMsgSplit[0].equals(ProtocolStrings.online)) {//Code for handling a protocol that dictates an updated userlist
                String users = inputMsgSplit[1];
                notifyListenersOnlineUsers(users);//Notifies observers that the user list is updated
            } else if (inputMsgSplit[0].equals(ProtocolStrings.message)) {//Code for handling a protocol that dictates an incomming message
                String user = inputMsgSplit[1];
                String message = inputMsgSplit[2];//Notifies observes that a message has arrived from a certain user
                notifyListenersMessageArrived(user + ": " + message);
            }
        }
        //INPUT HANDLECODE FOR INVALD INPUT/CONNECTION

    }

    public void registerListener(ClientObserver l) {

        listeners.add(l);

    }

    void notifyListenersOnlineUsers(String users) {
        //Method will notify listeners with an updated list of online users.
        for (ClientObserver listener : listeners) {
            listener.usersUpdated(users);
        }

    }

    void notifyListenersMessageArrived(String mes) {
        //Method will notify listeners with the newly arrived message.
        for (ClientObserver listener : listeners) {
            listener.messageArrived(mes);
        }

    }

}

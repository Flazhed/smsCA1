/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import chatclient.*;
import chatserver.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Søren
 */
public class ClientTest implements ClientObserver {

    private String host = "localhost";
    private int port = 8999;
    private String resMsg = "";
    private String onlineUsers = "";
    private boolean conClosed = false;

    public ClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Server.main(null);

            }
        }).start();
    }

    @Test
    public void send() throws IOException {

        String mes = "Hello friends";
        Client client = new Client();
        client.registerListener(this);
        client.connect("Jannik", host, port);
        client.start();
        client.send(mes, "*");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals("Jannik: " + mes, resMsg);
        client.close();
    }

    @Test
    public void onlineUsers() throws IOException {

        Client client = new Client();
        client.connect("Per", host, port);
        client.start();

        
        
        Client client2 = new Client();
        client.registerListener(this);
        client.connect("Hans", host, port);
        client2.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals("Per,Hans", onlineUsers);
        
    }
    
    
    @Test
    public void disconnectClient() throws IOException, InterruptedException{
        String mes = "Hello friends";
        Client client = new Client();
        client.registerListener(this);
        client.connect("John", host, port);
        client.start();
        client.registerListener(this);
        
        client.close();
        
        Thread.sleep(1000);
        
        
        
        assertTrue(conClosed);
    }

    @Override
    public void messageArrived(String mes) {

        resMsg = mes;

    }

    @Override
    public void usersUpdated(String users) {
        
        onlineUsers = users;
        
    }

    @Override
    public void closedConnection(Boolean close) {
        
        conClosed = close;
    }
}

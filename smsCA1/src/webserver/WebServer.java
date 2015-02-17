/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class WebServer {
    
    private final String ip;
    private final int port;
    private final String contentFolder;

    public WebServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        //Chosen by default
        this.contentFolder = "public/";
    }

    public WebServer(String ip, int port, String contentFolder) {
        this.ip = ip;
        this.port = port;
        this.contentFolder = contentFolder;
        try {
            startServer(ip, port, contentFolder);
        } catch (IOException ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startServer(String ip, int port, String contentFolder) throws IOException{
        //Making java HttpServer on a new socket (IP, port)
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
        
        //Making / the root directory of the webserver
        server.createContext("/", new Handler(contentFolder));
        server.setExecutor(null);
        server.start();
        
        //Needing loggin info here!
    }
    
    
}

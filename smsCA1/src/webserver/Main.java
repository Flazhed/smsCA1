/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class Main {
    
    public static void main(String[] args) {
        
        int port = 1337;
        String ip = "127.0.0.1";
        String contentFolder = "public/";
        
        if (args.length == 3) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            contentFolder = args[2];
        }
        
        WebServer serv = new WebServer(ip, port, contentFolder);
    }
    
}

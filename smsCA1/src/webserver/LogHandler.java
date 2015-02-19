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
import java.util.Scanner;

/**
 *
 * @author Søren
 */
public class LogHandler implements HttpHandler {

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
        sb.append("<h2>Chat log:</h2>\n");
        sb.append("<table border='1px'\n");
        sb.append("<thead>\n");
        sb.append("<th>Date/Class</th>\n");
        sb.append("<th>Message</th>\n");
        sb.append("<tbody>\n");

        Scanner scan = new Scanner(new File("chatLog.txt"));

        String temp = scan.nextLine();
        while (scan.hasNext()) {

            if (temp.matches("^([A-z]{3}\\.).*")) {
                sb.append("<tr>\n");
                sb.append("<td>\n");
                sb.append(temp + "\n");
                sb.append("</td>\n");
                sb.append("<td>\n");
                System.out.println("COL1: " + temp);
                if (scan.hasNext()) {
                    temp = scan.nextLine();
                }
            } else {

                sb.append(temp + "<br>");
                if (scan.hasNext()) {
                    temp = scan.nextLine();
                    if (temp.matches("^([A-z]{3}\\.).*")) {
                        sb.append("</td>\n");
                        sb.append("</tr>\n");
                    }

                }

            }
        }

        sb.append(temp + "\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        response = sb.toString();
        he.sendResponseHeaders(200, response.length());
        try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
            pw.print(response); //What happens if we use a println instead of print --> Explain
        }
    }
}

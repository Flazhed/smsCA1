/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class Handler implements HttpHandler {

    String contentFolder;

    public Handler(String contentPath) {
        this.contentFolder = contentPath;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {

        //Need hashmap for contentTypes!
        ContentTypes contentTypesClass = ContentTypes.getInstance();

        Headers h = he.getResponseHeaders();
        String requestedPage = he.getRequestURI().toString();

        //Serving the index.html file if it is present in the
        // requested directory
        if (!requestedPage.equals("/")) {
            //CRIME
            requestedPage = requestedPage.split("/")[1];
        } else {
            requestedPage = "index.html";
        }

        File requestedFile = new File(contentFolder + requestedPage);
        String contentType;

        contentType = contentTypesClass.getContentType(requestedPage);

        if (!requestedFile.exists()) {
            //error.html must exist else this will FAIL!

            requestedFile = new File(contentFolder + "error.html");
            contentType = contentTypesClass.getContentType("error.html");

            //Log error page was sent instead of the requested page
        }

        //Set contentType HERE --------
        //We dont add a content type if the file format is unknown
        //This is according to HTML 1.0
        if (contentType != null) {
            h.add("Contents-Type", contentType);
        }

        //Sending the requested file back to the user
        System.out.println("Trying: " + contentFolder + requestedPage);
        byte[] bytesToSend = new byte[(int) requestedFile.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(requestedFile));
            bis.read(bytesToSend, 0, bytesToSend.length);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        he.sendResponseHeaders(200, bytesToSend.length);
        try (OutputStream os = he.getResponseBody()) {
            os.write(bytesToSend, 0, bytesToSend.length);
        }

    }

}

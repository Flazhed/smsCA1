/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.util.HashMap;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class ContentTypes {

    private HashMap<String, String> contentTypeMap;

    public ContentTypes() {

        this.contentTypeMap = new HashMap();
        // Future load them from file
        easyAdd();
    }

    private void loadDefaultContentTypes() {

        //Load content types from file
    }

    private void easyAdd() {
        //Adding maps to known file formats
        contentTypeMap.put("jpg", "imgage/jpeg");
        contentTypeMap.put("html", "text/html");
        contentTypeMap.put("jar", "application/zip");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("css", "text/css");
    }

    public String getContentType(String contentType) {

        
        //Try block to prevent Array out of bounds
        try {
            contentType = "error.html".split("\\.")[1];
        } catch (Exception e) {
        }

        contentType = contentTypeMap.get(contentType);

        //If null is returned from the map the contentType dont exist
        if (contentType == null) {
            //Setting default text/plain if format is unknown
            contentType = "text/plain";
        }

        return contentType;

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
/**
 * Uses singleton pattern so we prevent initializing the class every time
 * we start a new handler. This is preferred because we plan to make the
 * web server using threads for each Connection made to it.
 * 
 * This way of making a singleton class should be threadsafe (according to wiki)
 */
public class ContentTypes {

    private static HashMap<String, String> contentTypeMap;
    private static Properties properties;

    private ContentTypes() {

        //Setting the properties file
        properties = webserver.Utils.Utils.contentTypesProperties("properties/webServerContentTypes.properties");

        contentTypeMap = new HashMap();

        //Adding all contentTypes to hashmap from the properties file
        System.out.println("Loading contentTypes");
        for (final String cType : properties.stringPropertyNames()) {
            contentTypeMap.put(cType, properties.getProperty(cType));
            System.out.println(cType + " " + properties.getProperty(cType));
        }
    }
    
    //Singleton holder class
    private static class SingletonHolder {

        private static final ContentTypes INSTANCE = new ContentTypes();
    }
    
    //

    /**
     *
     * @return the instance of the Singleton ContentTypes class.
     */
        public static ContentTypes getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     *
     * @param contentType String of a filename with ending! eg. index.html
     * @return The content Type for a given file determined by the ending of
     * the file.
     * 
     * Return null if the file format is not listed in the hashmap.
     */
        public String getContentType(String contentType) {

        //Fix logic and names in this method
        //Try block to prevent Array out of bounds
        try {
            contentType = contentType.split("\\.")[1];
        } catch (Exception e) {
        }

        contentType = contentTypeMap.get(contentType);

        return contentType;

    }

}

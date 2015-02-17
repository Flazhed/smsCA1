/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Stefan Duro <stefduro@gmail.com>
 */
public class Utils {

    /**
     *
     * @param propertyFile Filename of the properties file.
     * @return Properties object, that contains the properties in the requested file. 
     */
    public static Properties contentTypesProperties(String propertyFile) {

        Properties properties = new Properties();

        try (InputStream is = new FileInputStream(propertyFile)) {
            properties.load(is);
        } catch (IOException ex) {

            System.out.println(String.format("Could not locate the %1$s file.", propertyFile));

            return null;
        }
        return properties;
    }

    //Make logger here too
}

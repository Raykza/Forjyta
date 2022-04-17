package me.tyza.utils;

import org.slf4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesManager {
    HashMap<String, String> propertiesMap;
    private final Logger LOGGER;

    public PropertiesManager(Logger logger) {
        LOGGER = logger;
        propertiesMap = new HashMap<>();
    }

    public void createProperties(File propertiesFile) {
        Properties properties = new Properties();
        OutputStream output;

        try {
            output = new FileOutputStream(propertiesFile);

            properties.setProperty("api","key");
            properties.setProperty("prefix", "x! ");
            properties.setProperty("status_channel","id");
            properties.setProperty("embed_channel","id");
            properties.setProperty("embed","id");
            properties.setProperty("mod_role","id");
            properties.setProperty("guild","id");


            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int load(File propertiesFile) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFile);
        } catch (FileNotFoundException filex) {
            createProperties(propertiesFile);
            LOGGER.warn("Properties file is missing, a new properties file has been created.");
            return 1;
        } finally {
            if (inputStream != null) {
                try {
                    properties.load(inputStream);
                    inputStream.close();

                    for(String key : properties.stringPropertyNames()) {
                        String value = properties.getProperty(key);
                        this.propertiesMap.put(key,value);
                    }

                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        }
        return 0;
    }

    public String getProperty(String name) {
        return this.propertiesMap.get(name);
    }

    public PropertiesManager setProperty(String key, String value) {
        this.propertiesMap.put(key, value);
        return this;
    }

    public void save(File propertiesFile) {
        Properties properties = new Properties();
        OutputStream output;

        try {
            output = new FileOutputStream(propertiesFile);
            propertiesMap.forEach(properties::setProperty);
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

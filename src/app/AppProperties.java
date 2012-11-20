package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    
    public static final String PORT = "port";

    public AppProperties() {
        super();
    }
    
    /**
     * A property exists if it has been set, or in case of 
     * a value-less property, if it has been seen.
     * 
     * @param property  Property name to check.
     * @return          true, if property has been set or seen.
     */
    public boolean exists(String property) {
        return properties.containsKey(property);
    }

    public String get(String property) {
        return properties.getProperty(property);
    }
    
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
    
    private final Properties defaults;
    private final Properties properties;
    
    {
        defaults = new Properties();
        defaults.setProperty(PORT, "8080");
        
        properties = new Properties(defaults);
        try {
            InputStream file = new FileInputStream(CONFIG_FILE);
            properties.load(file);
        } 
        catch (FileNotFoundException e) {} 
        catch (IOException e) {}
    }
    
    private static final String CONFIG_FILE = 
            System.getProperty("user.dir")
            .concat(System.getProperty("file.separator"))
            .concat("app.config");
}

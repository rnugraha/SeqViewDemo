package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    
    public static final String PORT = "port";

    // The path to a json file from which we load gene names.
    public static final String GENEDB = "genedb";

    // The path to a json file from which we load variant data.
    public static final String VARIANTDB = "variantdb";

    // Use an in-memory, or embedded, database.
    public static final String DB_MEMORY = "memory";
    
    // Use MongoDB as database.
    public static final String DB_MONGO = "mongo";

    // Select a directory to serve static files from.
    public static final String WWWDIR = "wwwdir";

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

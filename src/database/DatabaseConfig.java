package database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {
    
    public DatabaseConfig() {
        this.properties = new HashMap<String, String>();
    }
    
    public DatabaseConfig set(String name, String value) {
        this.properties.put(name, value);
        return this;
    }
    
    public String get(String name) {
        if (this.properties.containsKey(name)) {
            return this.properties.get(name);
        }
        return null;
    }

    private final Map<String, String> properties;
}

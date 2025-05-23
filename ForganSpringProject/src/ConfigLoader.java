import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for the Risk Register application
 * Loads configuration from properties file
 */
public class ConfigLoader {
    private static Properties properties;
    private static ConfigLoader instance;
    
    private ConfigLoader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                // Fall back to direct file path if not found in classpath
                try (InputStream fileInput = getClass().getClassLoader().getResourceAsStream("/src/config.properties")) {
                    if (fileInput != null) {
                        properties.load(fileInput);
                    } else {
                        System.err.println("Unable to find config.properties");
                        loadDefaults();
                    }
                }
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            loadDefaults();
        }
    }
    
    private void loadDefaults() {
        // Default database settings
        properties.setProperty("db.host", "127.0.0.1:3306");
        properties.setProperty("db.name", "risks");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "chelmsford");
        
        // Default API settings
        properties.setProperty("api.port", "8080");
        
        // Default security settings
        properties.setProperty("admin.password", "547");
    }
    
    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
    
    public static String getProperty(String key) {
        getInstance();
        return properties.getProperty(key);
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
}
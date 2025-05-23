import java.sql.*;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * DATABASE CONNECTION MANAGER
 * 
 * Handles database connections for the Risk Register application
 * Creates and provides database connections using connection pooling
 */
public class ConnectionManager {
    
    private static DataSource dataSource;
    
    /**
     * Initialize the connection pool
     */
    private static synchronized void initDataSource() {
        if (dataSource == null) {
            try {
                MysqlDataSource mysqlDS = new MysqlDataSource();
                mysqlDS.setURL("jdbc:mysql://" + ConfigLoader.getProperty("db.host") + "/" + 
                               ConfigLoader.getProperty("db.name"));
                mysqlDS.setUser(ConfigLoader.getProperty("db.user"));
                mysqlDS.setPassword(ConfigLoader.getProperty("db.password"));
                
                // Configure connection pool settings
                mysqlDS.setMaxReconnects(3);
                mysqlDS.setAutoReconnect(true);
                
                dataSource = mysqlDS;
                
                // Test connection
                try (Connection conn = dataSource.getConnection()) {
                    System.out.println("Database connection established successfully");
                }
            } catch (SQLException e) {
                System.err.println("Failed to initialize connection pool: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Database connection initialization failed", e);
            }
        }
    }
    
    /**
     * Get a connection to the default database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }
    
    /**
     * Get a connection to a specific database
     * @param database the database name
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection(String database) throws SQLException {
        try {
            MysqlDataSource mysqlDS = new MysqlDataSource();
            mysqlDS.setURL("jdbc:mysql://" + ConfigLoader.getProperty("db.host") + "/" + database);
            mysqlDS.setUser(ConfigLoader.getProperty("db.user"));
            mysqlDS.setPassword(ConfigLoader.getProperty("db.password"));
            return mysqlDS.getConnection();
        } catch(SQLException e) {
            System.err.println("Failed to connect to database '" + database + "': " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
}
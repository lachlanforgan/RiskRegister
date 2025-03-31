import java.sql.*;

/**
 * DATABASE CONNECTION MANAGER
 * 
 * Handles database connections for the Risk Register application
 * Creates and provides database connections
 */
public class ConnectionManager {
    
    private static final String HOST = "127.0.0.1:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "chelmsford";
    private static final String DEFAULT_DATABASE = "risks";
    
    /**
     * Get a connection to the default database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + "/" + DEFAULT_DATABASE,
                    USER,
                    PASSWORD);
            return connection;
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get a connection to a specific database
     * @param database the database name
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection(String database) throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + "/" + database,
                    USER,
                    PASSWORD);
            return connection;
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
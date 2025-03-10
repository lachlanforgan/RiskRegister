/******************
 * SERVICES CLASS
 *
 * THIRD layer of risk architecture
 * Performs operations on the database using OPERATIONS CLASS
 *      implements logic and checks to perform operations
 *
 */

import java.sql.*;

public class RiskManager
{

    public Risk createRisk(int riskID, int projectID, String title, String description, String mitigationPlan, String owner, String status,
                              Risk.Likelihood likelihood, Risk.Impact impact)
    {
        Risk r = new Risk(riskID, projectID, title, description, mitigationPlan, owner, status, likelihood, impact);
        return r;
    }

    public boolean deleteAll(int pw)
    {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (pw == 547)
        {
            Operations.deleteAll(connection);
        }
        return true;
    }

    public boolean addRisk(Risk risk)
    {
        try
        {
            Connection connection = getConnection();

            if (this.riskExists(connection, risk))
            {
                System.out.println("Risk already exists.");
                return false;
            }
            System.out.println("Adding risk to database...");
            Operations.addRisk(connection, risk);
        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteRisk(Risk risk)
    {
        try
        {
            Connection connection = getConnection();

            if (this.riskExists(connection, risk))
            {
                if (Operations.deleteRisk(connection, risk)) {
                    System.out.println("Risk deleted from database: RISKID - " + risk.getRiskID());
                    return true;
                }
                else {
                    System.out.println("FAILED to delete risk form database.");
                    return false;
                }
            }
            else
            {
                System.out.println("No risk found: RISKID - " + risk.getRiskID());
                return true;
            }
        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return true;
    }

    private boolean riskExists(Connection connection, Risk risk) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT riskID FROM risk WHERE riskID = ?")) {
            stmt.setInt(1, (int) risk.getRiskID());

            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
        return false;
    }

    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/risks", "root", "chelmsford");
        return connection;
    }

}

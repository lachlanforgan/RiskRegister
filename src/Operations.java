/*******************
 * OPERATIONS CLASS
 *
 * SECOND layer of risk architecture
 * Contains simple operations like ADD / DELETE
 *      does NOT contain logic or checks on operations
 *
 */

import java.sql.*;

public class Operations
{


    public static boolean addRisk(Connection connection, Risk risk)
    {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title, description, likelihood, impact, mitigation_plan, owner, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"))
        {
            stmt.setInt(1, (int)risk.getRiskID());
            stmt.setInt(2, (int)risk.getProjectID());
            stmt.setString(3, risk.getTitle());
            stmt.setString(4, risk.getDescription());
            stmt.setString(5, risk.generateLikelihood());
            stmt.setString(6, risk.generateImpact());
            stmt.setString(7, risk.getMitigationPlan());
            stmt.setString(8, risk.getOwner());
            stmt.setString(9, risk.getStatus());
            stmt.executeUpdate();

            System.out.println("Data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Print the stack trace for detailed error information
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static boolean deleteRisk(Connection connection, Risk risk) {
        try {
            Statement statement = connection.createStatement();

///
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk WHERE riskID = ?")) {
                stmt.setInt(1, (int) risk.getRiskID());
                stmt.execute();

                System.out.println("Delete statement executed successfully.");
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace(); // Print the stack trace for detailed error information
                return false;
            }

        } catch (SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

        public static boolean deleteAll(Connection connection)
        {
            try
            {
                Statement statement = connection.createStatement();

///
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk"))
                {
                    stmt.execute();
                    System.out.println("Delete statement executed successfully.");
                } catch (SQLException e) {
                    System.err.println("SQL Exception: " + e.getMessage());
                    System.err.println("SQL State: " + e.getSQLState());
                    System.err.println("Error Code: " + e.getErrorCode());
                    e.printStackTrace(); // Print the stack trace for detailed error information
                    return false;
                }

            } catch(SQLException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
                return false;
        }
        return true;
    }



    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/risks", "root", "chelmsford");
        return connection;
    }

}
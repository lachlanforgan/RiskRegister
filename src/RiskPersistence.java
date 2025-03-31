/*******************
 * OPERATIONS CLASS
 *
 * SECOND layer of risk architecture
 * Contains simple operations like ADD / DELETE
 *      does NOT contain logic or checks on operations
 *
 */

import java.lang.module.FindException;
import java.sql.*;

public class RiskPersistence
{

    public static RiskEntity getRisk(long riskID, long projectID)
    {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        RiskEntity r = null;
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM risk WHERE riskID = ? AND projectID = ?")) {
            stmt.setInt(1, (int) riskID);
            stmt.setInt(2, (int) projectID);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next())
            {
                r = new RiskEntity();
                r.setRiskID((long)resultSet.getInt("riskID"));
                r.setProjectID((long)resultSet.getInt("projectID"));
                r.setDescription(resultSet.getString("description"));
            }
            else
            {
                throw new FindException("Couldn't find risk.");
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
        return r;
    }

    public static boolean addRisk(RiskEntity riskEntity)
    {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title, description, likelihood, impact, mitigation_plan, owner, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"))
        {
            stmt.setInt(1, (int) riskEntity.getRiskID());
            stmt.setInt(2, (int) riskEntity.getProjectID());
            stmt.setString(3, riskEntity.getTitle());
            stmt.setString(4, riskEntity.getDescription());
            stmt.setString(5, riskEntity.generateLikelihood());
            stmt.setString(6, riskEntity.generateImpact());
            stmt.setString(7, riskEntity.getMitigationPlan());
            stmt.setString(8, riskEntity.getOwner());
            stmt.setString(9, riskEntity.getStatus());
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

    public static boolean deleteRisk(RiskEntity riskEntity) {
        try {
            Connection connection = ConnectionManager.getConnection();
            Statement statement = connection.createStatement();

///
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk WHERE riskID = ?")) {
                stmt.setInt(1, (int) riskEntity.getRiskID());
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

    public static boolean deleteRisk(long riskID, long projectID) {
        try {
            Connection connection = ConnectionManager.getConnection();
            Statement statement = connection.createStatement();

///
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk WHERE riskID = ? AND projectID = ?")) {
                stmt.setInt(1, (int) riskID);
                stmt.setInt(2, (int) projectID);

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

        public static boolean deleteAll()
        {
            try
            {
                Connection connection = ConnectionManager.getConnection();
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

    public static boolean riskExists(RiskEntity riskEntity) {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT riskID FROM risk WHERE riskID = ?")) {
            stmt.setInt(1, (int) riskEntity.getRiskID());

            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
        return false;
    }

    public static boolean riskExists(long riskID, long projectID) {
        Connection connection;
        try {
            connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT riskID FROM risk WHERE riskID = ? AND projectID = ?")) {
            stmt.setInt(1, (int) riskID);
            stmt.setInt(2, (int) projectID);

            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Print the stack trace for detailed error information
        }
        return false;
    }



}
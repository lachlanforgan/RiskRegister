/******************
 * SERVICES CLASS
 *
 * THIRD layer of risk architecture
 * Performs operations on the database using OPERATIONS CLASS
 *      implements logic and checks to perform operations
 *
 */

import java.sql.*;

public class RiskService
{

    public boolean createRisk(int riskID, int projectID, String title, String description, String mitigationPlan, String owner, String status,
                                 RiskEntity.Likelihood likelihood, RiskEntity.Impact impact)
    {
        RiskEntity r = new RiskEntity(riskID, projectID, title, description, mitigationPlan, owner, status, likelihood, impact);
        return this.createRisk(r);
    }

    public RiskEntity getRisk(long riskID, long projectID)
    {
        try {
            return RiskPersistence.getRisk(getConnection(), riskID, projectID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            RiskPersistence.deleteAll(connection);
        }
        return true;
    }

    public boolean createRisk(RiskEntity riskEntity)
    {
        try
        {
            Connection connection = getConnection();

            if (RiskPersistence.riskExists(connection, riskEntity))
            {
                System.out.println("Risk already exists.");
                return false;
            }
            System.out.println("Adding risk to database...");
            RiskPersistence.addRisk(connection, riskEntity);
        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteRisk(RiskEntity riskEntity)
    {
        try
        {
            Connection connection = getConnection();

            if (RiskPersistence.riskExists(connection, riskEntity))
            {
                if (RiskPersistence.deleteRisk(connection, riskEntity)) {
                    System.out.println("Risk deleted from database: RISKID - " + riskEntity.getRiskID());
                    return true;
                }
                else {
                    System.out.println("FAILED to delete risk form database.");
                    return false;
                }
            }
            else
            {
                System.out.println("No risk found: RISKID - " + riskEntity.getRiskID());
                return true;
            }
        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return true;
    }



    private static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/risks",
                    "root",
                    "chelmsford");
            return connection;
        }
        catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

}

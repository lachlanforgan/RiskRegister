/******************
 * SERVICES CLASS
 *
 * THIRD layer of risk architecture
 * Performs operations on the database using OPERATIONS CLASS
 *      implements logic and checks to perform operations
 *
 */

import java.sql.*;

public class RiskService {

    // Load admin password from configuration
    private static final int ADMIN_PASSWORD = ConfigLoader.getIntProperty("admin.password");

    // create risk given all parameters
    public boolean createRisk(int riskID, int projectID, String title, String description, String mitigationPlan, String owner, String status,
                              RiskEntity.Likelihood likelihood, RiskEntity.Impact impact) {
        RiskEntity r = new RiskEntity(riskID, projectID, title, description, mitigationPlan, owner, status, likelihood, impact);
        return this.createRisk(r);
    }

    // get risk entity by riskID and projectID
    public RiskEntity getRisk(long riskID, long projectID) {
        return RiskPersistence.getRisk(riskID, projectID);
    }

    // delete all risks from database
    public boolean deleteAll(int pw) {
        if (pw == ADMIN_PASSWORD) {    // check for passkey
            return RiskPersistence.deleteAll();
        }
        System.out.println("Invalid admin password");
        return false;
    }

    // create risk given risk entity
    public boolean createRisk(RiskEntity riskEntity) {
        if (RiskPersistence.riskExists(riskEntity))     // check if it already exists
        {
            System.out.println("Risk already exists.");
            return false;
        }
        System.out.println("Adding risk to database...");
        RiskPersistence.addRisk(riskEntity);
        return true;
    }

    // delete a risk given an entity
    public boolean deleteRisk(RiskEntity riskEntity) {
        if (RiskPersistence.riskExists(riskEntity)) {
            if (RiskPersistence.deleteRisk(riskEntity)) {
                System.out.println("Risk deleted from database: RISKID - " + riskEntity.getRiskID());
                return true;
            } else {
                System.out.println("FAILED to delete risk form database.");
                return false;
            }
        } else {
            System.out.println("No risk found: RISKID - " + riskEntity.getRiskID());
            return true;
        }
    }


    // delete risk given riskID and projectID
    public boolean deleteRisk(long riskID, long projectID) {
        if (RiskPersistence.riskExists(riskID, projectID)) {
            if (RiskPersistence.deleteRisk(riskID, projectID)) {
                System.out.println("Risk deleted from database: RISKID - " + riskID);
                return true;
            } else {
                System.out.println("FAILED to delete risk form database.");
                return false;
            }
        } else {
            System.out.println("No risk found: RISKID - " + riskID);
            return true;
        }
    }
}

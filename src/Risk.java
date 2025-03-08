import java.sql.*;

public class Risk
{
    private long riskID;        // Unique identifier for the risk
    private long projectID;     // Identifier for the associated project
    private String title;       // Short description of the risk
    private String description; // Detailed description of the risk
    enum Impact { LOW, MEDIUM, HIGH, CRITICAL } // Impact of the risk
    enum Likelihood { LOW, MEDIUM, HIGH } // Likelihood of the risk
    private Impact impact;
    private Likelihood likelihood;
    private String mitigationPlan; // Steps to reduce risk impact
    private String owner;       // Person responsible for managing the risk
    private String status;      // Status of the risk

    // General Constructor
    public Risk()
    {
        this.riskID = 0;
        this.projectID = 0;
        this.title = "";
        this.description = "";
        this.impact = Impact.LOW;
        this.likelihood = Likelihood.LOW;
        this.mitigationPlan = "";
        this.owner = "";
        this.status = "";
    }

    public Risk(int riskID, int projectID, String title, String description, String mitigationPlan, String owner, String status,
                Likelihood likelihood, Impact impact)
    {
        this.riskID = riskID;
        this.projectID = projectID;
        this.title = title;
        this.description = description;
        this.impact = impact;
        this.likelihood = likelihood;
        this.mitigationPlan = mitigationPlan;
        this.owner = owner;
        this.status = status;
    }

    // get and set methods

    public long getRiskID() {
        return riskID;
    }

    public void setRiskID(long riskID) {
        this.riskID = riskID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMitigationPlan() {
        return mitigationPlan;
    }

    public void setMitigationPlan(String mitigationPlan) {
        this.mitigationPlan = mitigationPlan;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImpact(Impact impact) {
        this.impact = impact;
    }
    public Impact getImpact() {
        return impact;
    }

    public void setLikelihood(Likelihood likelihood) {
        this.likelihood = likelihood;
    }

    public Likelihood getLikelihood() {
        return likelihood;
    }

    public boolean addRisk(Risk risk)
    {
        try
        {
            Connection connection = getConnection();

            if (this.riskExists(connection, risk))
            {
                return false;
            }
///
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title, description, likelihood, impact, mitigation_plan, owner, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                    stmt.setInt(1, (int)riskID);
                    stmt.setInt(2, (int)projectID);
                    stmt.setString(3, title);
                    stmt.setString(4, description);
                    stmt.setString(5, generateLikelihood(likelihood));
                    stmt.setString(6, generateImpact(impact));
                    stmt.setString(7, mitigationPlan);
                    stmt.setString(8, owner);
                    stmt.setString(9, status);
                    stmt.executeUpdate();

                System.out.println("Data inserted successfully.");
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace(); // Print the stack trace for detailed error information
            }

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
            Statement statement = connection.createStatement();

///
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM risk WHERE riskID = ?"))
            {
                stmt.setInt(1, (int)risk.getRiskID());
                stmt.execute();

                System.out.println("Delete statement executed successfully.");
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace(); // Print the stack trace for detailed error information
            }

        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return true;
    }

    private boolean riskExists(Connection connection, Risk risk)
    {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT riskID FROM risk WHERE riskID = ?"))
        {
            stmt.setInt(1, (int)risk.getRiskID());

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

    public String generateImpact(Impact impact)
    {
        switch (impact)
        {
            case LOW:
                return "LOW";
            case MEDIUM:
                return "MEDIUM";
            case HIGH:
                return "HIGH";
            case CRITICAL:
                return "CRITICAL";
            default:
                return "INVALID";
        }
    }

    public String generateLikelihood(Likelihood likelihood)
    {
        switch (likelihood)
        {
            case LOW:
                return "LOW";
            case MEDIUM:
                return "MEDIUM";
            case HIGH:
                return "HIGH";
            default:
                return "INVALID";
        }
    }

}

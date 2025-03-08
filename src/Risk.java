import java.sql.*;

public class Risk
{
    private long riskID;        // Unique identifier for the risk
    private long projectID;     // Identifier for the associated project
    private String title;       // Short description of the risk
    private String description; // Detailed description of the risk
    enum Impact { LOW, MEDIUM, HIGH, CRITICAL }; // Impact of the risk
    enum Likelihood { LOW, MEDIUM, HIGH }; // Likelihood of the risk
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

    public void writeRisk(Risk risk)
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/risks",
                    "root",
                    "chelmsford");

            /*String query = String.format("INSERT INTO risks (riskID, projectID, title, description, impact, likelihood, mitigationPlan, owner, status) VALUES (%d, %d, %s, %s, %s, %s, %s, %s, %s)",
                    risk.getRiskID(),
                    risk.getProjectID(),
                    risk.getTitle(),
                    risk.getDescription(),
                    risk.generateImpact(getImpact()),
                    risk.generateLikelihood(getLikelihood()),
                    risk.getMitigationPlan(),
                    risk.getOwner(),
                    risk.getStatus());*/
           // String query = "SELECT * FROM users";
            Statement statement = connection.createStatement();
           // PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title, description, likelihood, impact, mitigation_plan, owner, status) VALUES (riskID, projectID, title, description, generateLikelihood(), generateImpact(), mitigation_plan, owner, status)");
            //PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title, description, likelihood, impact, mitigation_plan, owner, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
        ///
            /*stmt.setInt(1, (int)riskID);
            stmt.setInt(2, (int)projectID);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, generateLikelihood(likelihood));
            stmt.setString(6, generateImpact(impact));
            stmt.setString(7, mitigationPlan);
            stmt.setString(8, owner);
            stmt.setString(9, status);*/

           /* PreparedStatement stmt = connection.prepareStatement("INSERT INTO risk(riskID, projectID, title) VALUES (?, ?, ?)");
            stmt.setInt(1, (int)riskID);
            stmt.setInt(2, (int)projectID);
            String bob = "xyzx";
            stmt.setString(3,bob);*/









            // stmt.setString(1, email);
           // stmt.setString(2, password);
           // stmt.setDate(3, new Date());

            //stmt.executeUpdate();

           /* while (resultSet.next())
            {
                System.out.println(resultSet.getString("projectID"));
                //System.out.println(resultSet.getString("password"));
            }*/

            // Connection con=null;
           // PreparedStatement p=null;

           // p =connection.prepareStatement(query);
           // p.execute();

          //  Statement statement = connection.createStatement();
          //  ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

        } catch(SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
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

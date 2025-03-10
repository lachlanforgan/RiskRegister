/******************
 * RISK CLASS
 *
 * Most BASIC layer of risk architecture
 * Simple risk entity w/ components
 *
 */

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

    public String generateImpact()
    {
        switch (this.impact)
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

    public String generateLikelihood()
    {
        switch (this.likelihood)
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

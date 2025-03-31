// RISKTEST CLASS
// Purpose: Test the different layers of the risk architecture

public class RiskTest {

    // main method
    public static void main(String[] args)
    {
        RiskService riskService = new RiskService();    // create risk object for creating risks and
                                                        // interacting with database

        riskService.deleteAll(RiskService.PASSWORD);     // delete all risks from database

        Boolean created = riskService.createRisk(2, 3, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", RiskEntity.Likelihood.LOW, RiskEntity.Impact.LOW);
        assert created : "Risk was not created.";
        RiskEntity r = riskService.getRisk(2, 3);
        assert (r != null) : "Risk is null.";
        assert r.getRiskID() == 1 : String.format("Expected 1, got riskID = %d", r.getRiskID());
        assert r.getProjectID() == 1 : String.format("Expected 1, got projectID = %d", r.getProjectID());



    }
}

public class RiskTest {
    public static void main(String[] args)
    {
        //Risk r1 = new Risk();

        RiskService riskService = new RiskService();

        riskService.deleteAll(547);

        Boolean created = riskService.createRisk(2, 3, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", RiskEntity.Likelihood.LOW, RiskEntity.Impact.LOW);
        assert created : "Risk was not created.";
        RiskEntity r = riskService.getRisk(2, 3);
        assert (r != null) : "Risk is null.";
        assert r.getRiskID() == 1 : String.format("Expected 1, got riskID = %d", r.getRiskID());
        assert r.getProjectID() == 1 : String.format("Expected 1, got projectID = %d", r.getProjectID());



    }
}

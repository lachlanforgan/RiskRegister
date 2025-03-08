public class RiskTest {
    public static void main(String[] args)
    {
        //Risk r1 = new Risk();

        Risk r1 = new Risk(1, 1, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", Risk.Likelihood.LOW, Risk.Impact.LOW);

        r1.setRiskID(7);
        r1.setProjectID(8);
        r1.setTitle("Risk 1");
        r1.setDescription("Risk 1 Description");
        r1.setMitigationPlan("Risk 1 Mitigation Plan");
        r1.setOwner("Risk 1 Owner");
        r1.setStatus("Risk 1 Status");
        r1.setLikelihood(Risk.Likelihood.LOW);
        r1.setImpact(Risk.Impact.LOW);

        r1.writeRisk(r1);
    }
}

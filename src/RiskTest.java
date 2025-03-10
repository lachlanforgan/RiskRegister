public class RiskTest {
    public static void main(String[] args)
    {
        //Risk r1 = new Risk();

        RiskManager rm = new RiskManager();

        Risk r1 = rm.createRisk(1, 1, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", Risk.Likelihood.LOW, Risk.Impact.LOW);
       // rm.deleteAll(547);
        rm.addRisk(r1);
       // rm.deleteRisk(r1);


    }
}

public class RiskTest {
    public static void main(String[] args)
    {
        //Risk r1 = new Risk();


        Risk r1 = new Risk(1, 1, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", Risk.Likelihood.LOW, Risk.Impact.LOW);


        r1.deleteRisk(r1);

        if (r1.addRisk(r1))
            System.out.println("Added risk: " + r1.getRiskID());
        else
            System.out.println("Risk already exists.");


        Risk r2 = new Risk(10, 12, "Risk 1", "Risk 1 Description",
                "Risk 1 Mitigation Plan", "Bob Owner", "Status", Risk.Likelihood.LOW, Risk.Impact.LOW);



        if (r2.addRisk(r2))
            System.out.println("Added risk.");
        else
            System.out.println("Risk already exists.");
    }
}

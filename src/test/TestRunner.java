package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("===== LANCEMENT DE TOUS LES TESTS =====");

        Class[] tests = {
            MinMaxStrategieTest.class,
            AlphaBetaStrategieTest.class
            
        };

        int totalTests = 0;
        int totalFailures = 0;

        for (Class testClass : tests) {
            System.out.println("→ Exécution des tests de " + testClass.getSimpleName());
            Result result = JUnitCore.runClasses(testClass);

            for (Failure failure : result.getFailures()) {
                System.out.println(" Échec : " + failure.toString());
            }

            int passed = result.getRunCount() - result.getFailureCount();
            totalTests += result.getRunCount();
            totalFailures += result.getFailureCount();

            System.out.println("Résultat : " + passed + "/" + result.getRunCount() + " tests réussis\n");
        }

        System.out.println(" FIN DES TESTS");
        System.out.println("Total : " + (totalTests - totalFailures) + "/" + totalTests + " tests réussis");

        if (totalFailures == 0) {
            System.out.println("All tests passed!");
        } else {
            System.out.println(  totalFailures + " At least one test failed");
        }
    }
}

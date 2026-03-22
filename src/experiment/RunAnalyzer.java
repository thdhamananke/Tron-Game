package experiment;

public class RunAnalyzer {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java RunAnalyzer <csvFile>");
            System.exit(1);
        }
        String csvFile = args[0];
        ExperimentAnalyzer.generateAllCharts(csvFile);
    }
}

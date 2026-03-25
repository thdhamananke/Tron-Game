package experiment;

public class RunAnalyzer {
    public static void main(String[] args) {
         // String csvFile = "/home/khelalf241/Documents/projet2-jeuxdetorne-khelalfa-hadj-benabdelmoula-diallo-bah/csv/duel_20260325_161933.csv";
        //System.out.println("THe name of the csv file is : "+csvFile);
        //ExperimentAnalyzer.generateAllCharts2("/home/khelalf241/Documents/projet2-jeuxdetorne-khelalfa-hadj-benabdelmoula-diallo-bah/csv/duel_20260325_161933.csv");
        if (args.length < 1) {
            System.err.println("Usage: java RunAnalyzer <csvFile>");
            System.exit(1);
        }
      
        String csvFile = args[0];
        System.out.println("THe name of the csv file is : "+csvFile);
        ExperimentAnalyzer.generateAllCharts2(csvFile);
    }
}

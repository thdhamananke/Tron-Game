package experiment;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChartGenerator2 {
   public static JFreeChart createGlobalWinPieChartByHeuristic(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerHeuristic = new HashMap<>();
    Map<String, String> teamToHeuristic = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;
       // String currentConfig = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

           
            if (line.startsWith("# Stratégies:")) {
                teamToHeuristic.clear();
                inDataSection = false;

                
                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Heuristic = strategies[0].split("\\(")[1].split("\\)")[0];
                    String team2Heuristic = strategies[1].split("\\(")[1].split("\\)")[0];

                    teamToHeuristic.put("Equipe_1", team1Heuristic);
                    teamToHeuristic.put("Equipe_2", team2Heuristic);
                }
                continue;
            }

            
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        String heuristic = teamToHeuristic.get(team);
                        if (heuristic != null) {
                            winsPerHeuristic.put(heuristic, winsPerHeuristic.getOrDefault(heuristic, 0) + 1);
                        }
                    }
                    totalMatches++;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

   
    for (Map.Entry<String, Integer> entry : winsPerHeuristic.entrySet()) {
        String heuristic = entry.getKey();
        int wins = entry.getValue();
        double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
        dataset.setValue(heuristic + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
    }

    if (drawCount != 0 && totalMatches != 0) {
        double drawPercentage = (double) drawCount / totalMatches * 100;
        dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
    }

    JFreeChart chart = ChartFactory.createPieChart(
            "Global Win Distribution by Heuristic",
            dataset,
            true,
            true,
            false
    );

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setSectionPaint("Match Nul", Color.GRAY);
    plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
    plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

    return chart;
}

public static JFreeChart createGlobalWinPieChartByStrategyOnly(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerStrategy = new HashMap<>();
    Map<String, String> teamToStrategy = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;
        String currentConfig = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            
            if (line.startsWith("# Stratégies:")) {
                teamToStrategy.clear();
                inDataSection = false;

                
                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Strategy = strategies[0].split("\\(")[0].trim();
                    String team2Strategy = strategies[1].split("\\(")[0].trim();

                    teamToStrategy.put("Equipe_1", team1Strategy);
                    teamToStrategy.put("Equipe_2", team2Strategy);
                }
                continue;
            }

            
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        String strategy = teamToStrategy.get(team);
                        if (strategy != null) {
                            winsPerStrategy.put(strategy, winsPerStrategy.getOrDefault(strategy, 0) + 1);
                        }
                    }
                    totalMatches++;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    
    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
        String strategy = entry.getKey();
        int wins = entry.getValue();
        double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
        dataset.setValue(strategy + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
    }

    if (drawCount != 0 && totalMatches != 0) {
        double drawPercentage = (double) drawCount / totalMatches * 100;
        dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
    }

    JFreeChart chart = ChartFactory.createPieChart(
            "Global Win Distribution by Strategy",
            dataset,
            true,
            true,
            false
    );

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setSectionPaint("Match Nul", Color.GRAY);
    plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
    plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

    return chart;
}

    public static JFreeChart createGlobalWinPieChartByStrategy(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerStrategy = new HashMap<>();
    Map<String, String> teamToStrategy = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;
        String currentConfig = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("# CONFIG:")) {
                currentConfig = line;
                teamToStrategy.clear();
                inDataSection = false;

                
                String[] parts = currentConfig.split("_VS_");
                if (parts.length >= 2) {
                    
                    String team1Strategy = parts[0].split("Strategie_")[1].split("_")[0];
                    
                    String team2Strategy = parts[1].split("Strategie_")[1].split("_")[0];

                    
                   // System.out.println("Current Config: " + currentConfig);
                   // System.out.println("Team 1 Strategy: " + team1Strategy);
                   // System.out.println("Team 2 Strategy: " + team2Strategy);

                    teamToStrategy.put("Equipe_1", team1Strategy);
                    teamToStrategy.put("Equipe_2", team2Strategy);
                }
                continue;
            }

           
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        String strategy = teamToStrategy.get(team);
                        if (strategy != null) {
                            winsPerStrategy.put(strategy, winsPerStrategy.getOrDefault(strategy, 0) + 1);
                        }

                        
                      //  System.out.println("Team: " + team + ", Strategy: " + strategy);
                    }
                    totalMatches++;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    
    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
      //  System.out.println(entry.getKey() + ": " + entry.getValue() + " wins");
    }
   // System.out.println("Draw Count: " + drawCount);
   
 //   System.out.println("Total Matches is : " + totalMatches);

    
    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
        String strategy = entry.getKey();
        int wins = entry.getValue();
        double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
        dataset.setValue(strategy + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
    }

    if (drawCount != 0 && totalMatches != 0) {
        double drawPercentage = (double) drawCount / totalMatches * 100;
        dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
    }

    JFreeChart chart = ChartFactory.createPieChart(
            "Global Win Distribution by Strategy",
            dataset,
            true,
            true,
            false
    );

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setSectionPaint("Match Nul", Color.GRAY);
    plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
    plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

    return chart;
}
  public static JFreeChart createGlobalWinPieChartByStrategy2(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerStrategy = new HashMap<>();
    Map<String, String> teamToStrategy = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;
        String currentConfig = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            
            if (line.startsWith("# CONFIG:")) {
                currentConfig = line;
                teamToStrategy.clear();
                inDataSection = false;

                
                String[] parts = currentConfig.split("_VS_");
                if (parts.length >= 2) {
                    
                    String team1Strategy = parts[0].split("Strategie_")[1].split("_")[0];
                    
                    String team2Strategy = parts[1].split("Strategie_")[1].split("_")[0];

                    teamToStrategy.put("Equipe_1", team1Strategy);
                    teamToStrategy.put("Equipe_2", team2Strategy);
                }
                continue;
            }

           
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

          
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        String strategy = teamToStrategy.get(team);
                        if (strategy != null) {
                            winsPerStrategy.put(strategy, winsPerStrategy.getOrDefault(strategy, 0) + 1);
                        }
                    }
                    totalMatches++;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    
    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
        String strategy = entry.getKey();
        int wins = entry.getValue();
        double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
        dataset.setValue(strategy + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
    }

    if (drawCount != 0 && totalMatches != 0) {
        double drawPercentage = (double) drawCount / totalMatches * 100;
        dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
    }

    JFreeChart chart = ChartFactory.createPieChart(
            "Global Win Distribution by Strategy",
            dataset,
            true,
            true,
            false
    );

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setSectionPaint("Match Nul", Color.GRAY);
    plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
    plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

    return chart;
}

    public static JFreeChart createGlobalWinPieChart(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerTeam = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;
    int nombreEquipes = 2; 
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                    }
                    totalMatches++;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

  
    for (int i = 1; i <= nombreEquipes; i++) {
        String team = "Equipe_" + i;
        if (!winsPerTeam.containsKey(team)) {
            winsPerTeam.put(team, 0);
        }
    }

   
    for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
        String team = entry.getKey();
        int wins = entry.getValue();
        double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
        dataset.setValue(team + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
    }

    if (drawCount != 0 && totalMatches != 0) {
        double drawPercentage = (double) drawCount / totalMatches * 100;
        dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
    }

    JFreeChart chart = ChartFactory.createPieChart(
            "Global Win and Draw Distribution",
            dataset,
            true,
            true,
            false
    );

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setSectionPaint("Match Nul", Color.GRAY);
    plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
    plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

    return chart;
}

    public static JFreeChart createGlobalTimeChart(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTimePerTeam = new HashMap<>();
    Map<String, Integer> winsPerTeam = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

           
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String team = values[1].trim();
                    double time = Double.parseDouble(values[2].trim());

                    if (team.startsWith("Equipe_")) {
                        totalWinTimePerTeam.put(team, totalWinTimePerTeam.getOrDefault(team, 0.0) + time);
                        winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

  
    for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
        String team = entry.getKey();
        int wins = entry.getValue();
        double totalTime = totalWinTimePerTeam.getOrDefault(team, 0.0);
        double averageTime = totalTime / wins;
        dataset.addValue(averageTime, "Average Time", team);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Time per Team",
            "Team",
            "Average Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}

    public static JFreeChart createGlobalTurnsChart(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTurnPerTeam = new HashMap<>();
    Map<String, Integer> winsPerTeam = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

           
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

           
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String team = values[1].trim();
                    double turn = Double.parseDouble(values[3].trim());

                    if (team.startsWith("Equipe_")) {
                        totalWinTurnPerTeam.put(team, totalWinTurnPerTeam.getOrDefault(team, 0.0) + turn);
                        winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    
    for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
        String team = entry.getKey();
        int wins = entry.getValue();
        double totalTurns = totalWinTurnPerTeam.getOrDefault(team, 0.0);
        double averageTurns = totalTurns / wins;
        dataset.addValue(averageTurns, "Average Turns", team);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Turns per Team",
            "Team",
            "Average Turns",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}

    public static JFreeChart createWinPieChart(String csvFilePath) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> winsPerTeam = new HashMap<>();
        int drawCount = 0;
        int totalMatches = 0;
        int nombreEquipes = 2; 
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean inDataSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

               
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

               
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

               
                if (inDataSection) {
                    String[] values = line.split(",");
                    if (values.length >= 2) {
                        String team = values[1].trim();
                        if ("Match Nul".equals(team)) {
                            drawCount++;
                        } else if (team.startsWith("Equipe_")) {
                            winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                        }
                        totalMatches++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        
        for (int i = 1; i <= nombreEquipes; i++) {
            String team = "Equipe_" + i;
            if (!winsPerTeam.containsKey(team)) {
                winsPerTeam.put(team, 0);
            }
        }

       
        for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
            String team = entry.getKey();
            int wins = entry.getValue();
            double winPercentage = (totalMatches != 0) ? (double) wins / totalMatches * 100 : 0;
            dataset.setValue(team + " (" + String.format("%.2f", winPercentage) + "%)", winPercentage);
        }

        if (drawCount != 0 && totalMatches != 0) {
            double drawPercentage = (double) drawCount / totalMatches * 100;
            dataset.setValue("Match Nul (" + String.format("%.2f", drawPercentage) + "%)", drawPercentage);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Win and Draw Distribution",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Match Nul", Color.GRAY);
        plot.setSectionOutlinePaint("Match Nul", Color.GRAY);
        plot.setSectionOutlineStroke("Match Nul", new BasicStroke(0));

        return chart;
    }

    public static JFreeChart createTimeChart(String csvFilePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> totalWinTimePerTeam = new HashMap<>();
        Map<String, Integer> winsPerTeam = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean inDataSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

              
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

                
                if (inDataSection) {
                    String[] values = line.split(",");
                    if (values.length >= 3) {
                        String team = values[1].trim();
                        double time = Double.parseDouble(values[2].trim());

                        if (team.startsWith("Equipe_")) {
                            totalWinTimePerTeam.put(team, totalWinTimePerTeam.getOrDefault(team, 0.0) + time);
                            winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

       
        for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
            String team = entry.getKey();
            int wins = entry.getValue();
            double totalTime = totalWinTimePerTeam.getOrDefault(team, 0.0);
            double averageTime = totalTime / wins;
            dataset.addValue(averageTime, "Average Time", team);
        }

        return ChartFactory.createBarChart(
                "Average Winning Time per Team",
                "Team",
                "Average Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    public static JFreeChart createTurnsChart(String csvFilePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> totalWinTurnPerTeam = new HashMap<>();
        Map<String, Integer> winsPerTeam = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean inDataSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

               
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

                
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

                
                if (inDataSection) {
                    String[] values = line.split(",");
                    if (values.length >= 4) {
                        String team = values[1].trim();
                        double turn = Double.parseDouble(values[3].trim());

                        if (team.startsWith("Equipe_")) {
                            totalWinTurnPerTeam.put(team, totalWinTurnPerTeam.getOrDefault(team, 0.0) + turn);
                            winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

       
        for (Map.Entry<String, Integer> entry : winsPerTeam.entrySet()) {
            String team = entry.getKey();
            int wins = entry.getValue();
            double totalTurns = totalWinTurnPerTeam.getOrDefault(team, 0.0);
            double averageTurns = totalTurns / wins;
            dataset.addValue(averageTurns, "Average Turns", team);
        }

        return ChartFactory.createBarChart(
                "Average Winning Turns per Team",
                "Team",
                "Average Turns",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }
    public static JFreeChart createGlobalTimeChartByStrategy(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTimePerStrategy = new HashMap<>();
    Map<String, Integer> winsPerStrategy = new HashMap<>();
    Map<String, String> teamToStrategy = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("# Stratégies:")) {
                teamToStrategy.clear();
                inDataSection = false;

              
                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Strategy = strategies[0].split("\\(")[0].trim();
                    String team2Strategy = strategies[1].split("\\(")[0].trim();

                    teamToStrategy.put("Equipe_1", team1Strategy);
                    teamToStrategy.put("Equipe_2", team2Strategy);
                }
                continue;
            }

            
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

         
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

           
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String team = values[1].trim();
                    double time = Double.parseDouble(values[2].trim());

                    if (team.startsWith("Equipe_")) {
                        String strategy = teamToStrategy.get(team);
                        if (strategy != null) {
                            totalWinTimePerStrategy.put(strategy, totalWinTimePerStrategy.getOrDefault(strategy, 0.0) + time);
                            winsPerStrategy.put(strategy, winsPerStrategy.getOrDefault(strategy, 0) + 1);
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
        String strategy = entry.getKey();
        int wins = entry.getValue();
        double totalTime = totalWinTimePerStrategy.getOrDefault(strategy, 0.0);
        double averageTime = totalTime / wins;
        dataset.addValue(averageTime, "Average Time", strategy);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Time by Strategy",
            "Strategy",
            "Average Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}
public static JFreeChart createGlobalTimeChartByHeuristic(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTimePerHeuristic = new HashMap<>();
    Map<String, Integer> winsPerHeuristic = new HashMap<>();
    Map<String, String> teamToHeuristic = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("# Stratégies:")) {
                teamToHeuristic.clear();
                inDataSection = false;

                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Heuristic = strategies[0].split("\\(")[1].split("\\)")[0];
                    String team2Heuristic = strategies[1].split("\\(")[1].split("\\)")[0];

                    teamToHeuristic.put("Equipe_1", team1Heuristic);
                    teamToHeuristic.put("Equipe_2", team2Heuristic);
                }
                continue;
            }

           
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String team = values[1].trim();
                    double time = Double.parseDouble(values[2].trim());

                    if (team.startsWith("Equipe_")) {
                        String heuristic = teamToHeuristic.get(team);
                        if (heuristic != null) {
                            totalWinTimePerHeuristic.put(heuristic, totalWinTimePerHeuristic.getOrDefault(heuristic, 0.0) + time);
                            winsPerHeuristic.put(heuristic, winsPerHeuristic.getOrDefault(heuristic, 0) + 1);
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

  
    for (Map.Entry<String, Integer> entry : winsPerHeuristic.entrySet()) {
        String heuristic = entry.getKey();
        int wins = entry.getValue();
        double totalTime = totalWinTimePerHeuristic.getOrDefault(heuristic, 0.0);
        double averageTime = totalTime / wins;
        dataset.addValue(averageTime, "Average Time", heuristic);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Time by Heuristic",
            "Heuristic",
            "Average Time (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}
public static JFreeChart createGlobalTurnsChartByStrategy(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTurnPerStrategy = new HashMap<>();
    Map<String, Integer> winsPerStrategy = new HashMap<>();
    Map<String, String> teamToStrategy = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

           
            if (line.startsWith("# Stratégies:")) {
                teamToStrategy.clear();
                inDataSection = false;

               
                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Strategy = strategies[0].split("\\(")[0].trim();
                    String team2Strategy = strategies[1].split("\\(")[0].trim();

                    teamToStrategy.put("Equipe_1", team1Strategy);
                    teamToStrategy.put("Equipe_2", team2Strategy);
                }
                continue;
            }

            
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

           
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

           
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String team = values[1].trim();
                    double turn = Double.parseDouble(values[3].trim());

                    if (team.startsWith("Equipe_")) {
                        String strategy = teamToStrategy.get(team);
                        if (strategy != null) {
                            totalWinTurnPerStrategy.put(strategy, totalWinTurnPerStrategy.getOrDefault(strategy, 0.0) + turn);
                            winsPerStrategy.put(strategy, winsPerStrategy.getOrDefault(strategy, 0) + 1);
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

    
    for (Map.Entry<String, Integer> entry : winsPerStrategy.entrySet()) {
        String strategy = entry.getKey();
        int wins = entry.getValue();
        double totalTurns = totalWinTurnPerStrategy.getOrDefault(strategy, 0.0);
        double averageTurns = totalTurns / wins;
        dataset.addValue(averageTurns, "Average Turns", strategy);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Turns by Strategy",
            "Strategy",
            "Average Turns",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}
public static JFreeChart createGlobalTurnsChartByHeuristic(String csvFilePath) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    Map<String, Double> totalWinTurnPerHeuristic = new HashMap<>();
    Map<String, Integer> winsPerHeuristic = new HashMap<>();
    Map<String, String> teamToHeuristic = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

           
            if (line.startsWith("# Stratégies:")) {
                teamToHeuristic.clear();
                inDataSection = false;

               
                String[] strategies = line.split(":")[1].trim().split("vs");
                if (strategies.length >= 2) {
                    String team1Heuristic = strategies[0].split("\\(")[1].split("\\)")[0];
                    String team2Heuristic = strategies[1].split("\\(")[1].split("\\)")[0];

                    teamToHeuristic.put("Equipe_1", team1Heuristic);
                    teamToHeuristic.put("Equipe_2", team2Heuristic);
                }
                continue;
            }

           
            if (line.startsWith("#") || line.isEmpty() || line.startsWith("Nombre d'Equipes") || line.startsWith("Joueurs Par Equipe")) {
                inDataSection = false;
                continue;
            }

            
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

           
            if (inDataSection) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String team = values[1].trim();
                    double turn = Double.parseDouble(values[3].trim());

                    if (team.startsWith("Equipe_")) {
                        String heuristic = teamToHeuristic.get(team);
                        if (heuristic != null) {
                            totalWinTurnPerHeuristic.put(heuristic, totalWinTurnPerHeuristic.getOrDefault(heuristic, 0.0) + turn);
                            winsPerHeuristic.put(heuristic, winsPerHeuristic.getOrDefault(heuristic, 0) + 1);
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading CSV file: " + e.getMessage());
    }

   
    for (Map.Entry<String, Integer> entry : winsPerHeuristic.entrySet()) {
        String heuristic = entry.getKey();
        int wins = entry.getValue();
        double totalTurns = totalWinTurnPerHeuristic.getOrDefault(heuristic, 0.0);
        double averageTurns = totalTurns / wins;
        dataset.addValue(averageTurns, "Average Turns", heuristic);
    }

    return ChartFactory.createBarChart(
            "Global Average Winning Turns by Heuristic",
            "Heuristic",
            "Average Turns",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );
}
}

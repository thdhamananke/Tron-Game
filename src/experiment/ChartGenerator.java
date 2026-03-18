
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

public class ChartGenerator {

    public static JFreeChart createWinPieChart(String csvFilePath) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> winsPerTeam = new HashMap<>();
        int drawCount = 0;
        int totalMatches = 0;
        int nombreEquipes = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Nombre d'Equipes = ")) {
                    String[] parts = line.split("=");
                    nombreEquipes = Integer.parseInt(parts[1].trim());
                    for (int i = 0; i < nombreEquipes; i++) {
                        winsPerTeam.put("Equipe_" + (i + 1), 0);
                    }
                }

                if (line.startsWith("Nombre Parties")) {
                    String[] parts = line.split("=");
                    if (parts.length > 1) {
                        totalMatches = Integer.parseInt(parts[1].trim());
                    }
                }

                if (line.startsWith("TailleGrille") || line.startsWith("Parties") || line.isEmpty()) {
                    continue;
                }

                String[] values = line.split(",");
                if (values.length > 1) {
                    String team = values[1].trim();
                    if ("Match Nul".equals(team)) {
                        drawCount++;
                    } else if (team.startsWith("Equipe_")) {
                        winsPerTeam.put(team, winsPerTeam.get(team) + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
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

        return chart; // retourne le chart
    }


    public static JFreeChart createTimeChart(String csvFilePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> totalWinTimePerTeam = new HashMap<>();
        Map<String, Integer> winsPerTeam = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TailleGrille")) {
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("TailleGrille") || line.startsWith("Parties") || line.isEmpty()) continue;

                        String[] values = line.split(",");
                        if (values.length >2) {
                            String team = values[1].trim();
                            double time = Double.parseDouble(values[2].trim());

                            if (team.startsWith("Equipe_")) {
                                totalWinTimePerTeam.put(team, totalWinTimePerTeam.getOrDefault(team, 0.0) + time);
                                winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                            }
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
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TailleGrille")) {
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("TailleGrille") || line.startsWith("Parties") || line.isEmpty()) continue;

                        String[] values = line.split(",");
                        if (values.length >3) {
                            String team = values[1].trim();
                            double turn = Double.parseDouble(values[3].trim());

                            if (team.startsWith("Equipe_")) {
                                totalWinTurnPerTeam.put(team, totalWinTurnPerTeam.getOrDefault(team, 0.0) + turn);
                                winsPerTeam.put(team, winsPerTeam.getOrDefault(team, 0) + 1);
                            }
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
}

// javac -cp "lib/*" -d build/classes $(find src -type f -name "*.java" ! -path "src/test/*")
// java -cp "build/classes:lib/*" experiment.ExperimentMain

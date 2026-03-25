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

    public static JFreeChart createGlobalWinPieChart(String csvFilePath) {
    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    Map<String, Integer> winsPerTeam = new HashMap<>();
    int drawCount = 0;
    int totalMatches = 0;
    int nombreEquipes = 2; // Par défaut

    try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
        String line;
        boolean inDataSection = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Ignorer les lignes de configuration et de métadonnées
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

            // Détecter le début d'une section de données
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            // Traiter uniquement les lignes de données
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

    // Initialiser les équipes si elles ne sont pas déjà présentes
    for (int i = 1; i <= nombreEquipes; i++) {
        String team = "Equipe_" + i;
        if (!winsPerTeam.containsKey(team)) {
            winsPerTeam.put(team, 0);
        }
    }

    // Ajouter les données au dataset
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

            // Ignorer les lignes de configuration et de métadonnées
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

            // Détecter le début d'une section de données
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            // Traiter uniquement les lignes de données
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

    // Ajouter les données au dataset
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

            // Ignorer les lignes de configuration et de métadonnées
            if (line.startsWith("#") || line.isEmpty()) {
                inDataSection = false;
                continue;
            }

            // Détecter le début d'une section de données
            if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                inDataSection = true;
                continue;
            }

            // Traiter uniquement les lignes de données
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

    // Ajouter les données au dataset
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
        int nombreEquipes = 2; // Par défaut

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean inDataSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Ignorer les lignes de configuration et de métadonnées
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

                // Détecter le début d'une section de données
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

                // Traiter uniquement les lignes de données
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

        // Initialiser les équipes si elles ne sont pas déjà présentes
        for (int i = 1; i <= nombreEquipes; i++) {
            String team = "Equipe_" + i;
            if (!winsPerTeam.containsKey(team)) {
                winsPerTeam.put(team, 0);
            }
        }

        // Ajouter les données au dataset
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

                // Ignorer les lignes de configuration et de métadonnées
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

                // Détecter le début d'une section de données
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

                // Traiter uniquement les lignes de données
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

        // Ajouter les données au dataset
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

                // Ignorer les lignes de configuration et de métadonnées
                if (line.startsWith("#") || line.isEmpty()) {
                    inDataSection = false;
                    continue;
                }

                // Détecter le début d'une section de données
                if (line.startsWith("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours")) {
                    inDataSection = true;
                    continue;
                }

                // Traiter uniquement les lignes de données
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

        // Ajouter les données au dataset
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

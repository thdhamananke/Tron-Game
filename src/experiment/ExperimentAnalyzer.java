package experiment;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import model.*; 

public class ExperimentAnalyzer {

    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static JPanel chartPanel;
    private static JPanel configPanel;
    private static boolean isConfigWindowVisible = false;

   
    private static JSpinner plateauSizeSpinner;
    private static JSpinner teamsCountSpinner;
    private static JSpinner playersPerTeamSpinner;
    private static JSpinner numOfGamesSpinner;
    private static JRadioButton manualStrategy;
    private static JRadioButton randomStrategy;
    private static JPanel teamsConfigPanel;

    private static java.util.List<JComboBox<String>> stratCombos = new ArrayList<>();
    private static java.util.List<JComboBox<String>> heurCombos = new ArrayList<>();
    private static java.util.List<JSpinner> depthSpinners = new ArrayList<>();


    private static JPanel createConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Spinners globaux
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Taille du plateau :"), gbc);
        gbc.gridx = 1;
        plateauSizeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 100, 1));
        panel.add(plateauSizeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre d'équipes :"), gbc);
        gbc.gridx = 1;
        teamsCountSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        panel.add(teamsCountSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nombre de joueurs par équipe :"), gbc);
        gbc.gridx = 1;
        playersPerTeamSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        panel.add(playersPerTeamSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Nombre de parties :"), gbc);
        gbc.gridx = 1;
        numOfGamesSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        panel.add(numOfGamesSpinner, gbc);

      
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Configuration des stratégies :"), gbc);
        manualStrategy = new JRadioButton("Manuelle");
        randomStrategy = new JRadioButton("Random");
        ButtonGroup strategyGroup = new ButtonGroup();
        strategyGroup.add(manualStrategy);
        strategyGroup.add(randomStrategy);
        gbc.gridx = 1;
        panel.add(manualStrategy, gbc);
        gbc.gridy = 5;
        panel.add(randomStrategy, gbc);
        manualStrategy.setSelected(true);

      
        teamsConfigPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(teamsConfigPanel, gbc);

       
        Runnable updateTeamsConfig = () -> {
            teamsConfigPanel.removeAll();
            stratCombos.clear();
            heurCombos.clear();
            depthSpinners.clear();

            int numTeams = (Integer) teamsCountSpinner.getValue();
            int playersPerTeam = (Integer) playersPerTeamSpinner.getValue();
            GridBagConstraints tGbc = new GridBagConstraints();
            tGbc.insets = new Insets(3, 3, 3, 3);

            for (int i = 0; i < numTeams; i++) {
                int yOffset = i * 4;

                
                tGbc.gridx = 0;
                tGbc.gridy = yOffset;
                teamsConfigPanel.add(new JLabel("Équipe " + (i + 1) + " - Stratégie ?"), tGbc);
                tGbc.gridx = 1;
                JComboBox<String> stratCombo = new JComboBox<>(new String[]{"MinMax","AlphaBeta","MaxN","Paranoid","SOS"});
                stratCombos.add(stratCombo);
                teamsConfigPanel.add(stratCombo, tGbc);

               
                tGbc.gridx = 0;
                tGbc.gridy = yOffset + 1;
                teamsConfigPanel.add(new JLabel("Heuristique ?"), tGbc);
                tGbc.gridx = 1;
                JComboBox<String> heurCombo = new JComboBox<>(new String[]{"FreeSpace","Voronoi","TreeOfChambers"});
                heurCombos.add(heurCombo);
                teamsConfigPanel.add(heurCombo, tGbc);

                
                tGbc.gridx = 0;
                tGbc.gridy = yOffset + 2;
                teamsConfigPanel.add(new JLabel("Profondeur IA :"), tGbc);
                tGbc.gridx = 1;
                JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
                depthSpinners.add(depthSpinner);
                teamsConfigPanel.add(depthSpinner, tGbc);
            }

            teamsConfigPanel.revalidate();
            teamsConfigPanel.repaint();
        };

        updateTeamsConfig.run();
        teamsCountSpinner.addChangeListener(e -> updateTeamsConfig.run());
        playersPerTeamSpinner.addChangeListener(e -> updateTeamsConfig.run());

       
        gbc.gridx = 0; gbc.gridy = 100;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Démarrer l'expérience");
        startButton.addActionListener(e -> runExperiment());
        panel.add(startButton, gbc);

        return panel;
    }
   public static void generateAllCharts(String filePath) {
    
    mainFrame = new JFrame("Global Experiment Charts");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(1200, 800);
    mainFrame.setLocationRelativeTo(null);

   
    JTabbedPane tabbedPane = new JTabbedPane();

    
    JFreeChart heuristicPieChart = ChartGenerator.createGlobalWinPieChartByHeuristic(filePath);
    tabbedPane.addTab("Global Win Pie Chart by Heuristic", new ChartPanel(heuristicPieChart));

    JFreeChart strategyPieChart = ChartGenerator.createGlobalWinPieChartByStrategyOnly(filePath);
    tabbedPane.addTab("Global Win Pie Chart by Strategy", new ChartPanel(strategyPieChart));

    
    JFreeChart timeChartByStrategy = ChartGenerator.createGlobalTimeChartByStrategy(filePath);
    tabbedPane.addTab("Global Average Time by Strategy", new ChartPanel(timeChartByStrategy));

    JFreeChart timeChartByHeuristic = ChartGenerator.createGlobalTimeChartByHeuristic(filePath);
    tabbedPane.addTab("Global Average Time by Heuristic", new ChartPanel(timeChartByHeuristic));

   
    JFreeChart turnsChartByStrategy = ChartGenerator.createGlobalTurnsChartByStrategy(filePath);
    tabbedPane.addTab("Global Average Turns by Strategy", new ChartPanel(turnsChartByStrategy));

    JFreeChart turnsChartByHeuristic = ChartGenerator.createGlobalTurnsChartByHeuristic(filePath);
    tabbedPane.addTab("Global Average Turns by Heuristic", new ChartPanel(turnsChartByHeuristic));

    
    chartPanel = new JPanel(new BorderLayout());
    chartPanel.add(tabbedPane, BorderLayout.CENTER);

    
    configPanel = createConfigPanel();

   
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(chartPanel, BorderLayout.CENTER);

    
    JButton switchButton = new JButton("Switch to Config");
    switchButton.addActionListener(e -> switchWindow());

    JPanel topPanel = new JPanel();
    topPanel.add(switchButton);

    
    mainFrame.add(topPanel, BorderLayout.NORTH);
    mainFrame.add(mainPanel, BorderLayout.CENTER);

   
    mainFrame.setVisible(true);
}
    private static void runExperiment() {
        try {
            int plateauSize = (Integer) plateauSizeSpinner.getValue();
            int nbEquipes = (Integer) teamsCountSpinner.getValue();
            int nbJoueurs = (Integer) playersPerTeamSpinner.getValue();
            int nbGames = (Integer) numOfGamesSpinner.getValue();
            boolean modeRandom = randomStrategy.isSelected();

            java.util.List<Strategie> strategies = new java.util.ArrayList<>();
            Random random = new Random();

            for (int i = 0; i < nbEquipes; i++) {
                JComboBox<String> stratCombo = stratCombos.get(i);
                JComboBox<String> heurCombo = heurCombos.get(i);
                JSpinner depthSpinner = depthSpinners.get(i);

                String stratChoice = modeRandom ? java.util.List.of("MinMax","AlphaBeta","MaxN","Paranoid","SOS")
                                                 .get(random.nextInt(5))
                                               : (String) stratCombo.getSelectedItem();
                String heurChoice = modeRandom ? java.util.List.of("FreeSpace","Voronoi","TreeOfChambers")
                                               .get(random.nextInt(3))
                                             : (String) heurCombo.getSelectedItem();
                int profondeur = (Integer) depthSpinner.getValue();

                Heuristic heuristic = switch (heurChoice) {
                    case "Voronoi" -> new VoronoiHeuristic();
                    case "TreeOfChambers" -> new TreeOfChambersHeuristic();
                    default -> new FreeSpaceHeuristic();
                };

                Strategie strat = switch (stratChoice) {
                    case "AlphaBeta" -> new AlphaBetaStrategie(heuristic, profondeur);
                    case "MaxN" -> new MaxNStrategie(heuristic, profondeur);
                    case "Paranoid" -> new ParanoidStrategie(heuristic, profondeur);
                    case "SOS" -> new MinMaxStrategie(heuristic, profondeur); // temporaire
                    default -> new MinMaxStrategie(heuristic, profondeur);
                };

                for (int j = 0; j < nbJoueurs; j++) strategies.add(strat);
            }

            ExperimentConfig config = new ExperimentConfig(
                    plateauSize, plateauSize, nbEquipes, nbJoueurs, 0, nbGames, strategies
            );

            GameRunner runner = new GameRunner();
            ExperimentRunner experiment = new ExperimentRunner(runner);

            File csvDir = new File("csv");
            File pdfDir = new File("pdf");
            if (!csvDir.exists()) csvDir.mkdirs();
            if (!pdfDir.exists()) pdfDir.mkdirs();

            
            String uniqueId = System.currentTimeMillis() + "_" + (int)(Math.random()*1000);
            String filePath = "csv/Exp_" + uniqueId + ".csv";
            String pdfPath = "pdf/Exp_" + uniqueId + ".pdf";

            ExperimentResult result = experiment.run(config);

            PDFExporter.export(config, result, strategies, pdfPath);
            CSVExporter.export(config, result, strategies, filePath);

            JOptionPane.showMessageDialog(mainFrame, "Expérience terminée ! CSV et PDF générés.");

          

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static void switchWindow() {
        if (isConfigWindowVisible) {
            mainPanel.remove(configPanel);
            mainPanel.add(chartPanel, BorderLayout.CENTER);
            isConfigWindowVisible = false;
        } else {
            mainPanel.remove(chartPanel);
            mainPanel.add(configPanel, BorderLayout.CENTER);
            isConfigWindowVisible = true;
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
// javac -cp "lib/*" -d build/classes $(find src -type f -name "*.java" ! -path "src/test/*")
// java -cp "build/classes:lib/*" experiment.ExperimentMain
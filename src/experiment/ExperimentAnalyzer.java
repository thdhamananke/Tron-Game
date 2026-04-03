package experiment;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.*;
import java.awt.*;

public class ExperimentAnalyzer {

    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static JPanel chartPanel;
    

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

    
   

   
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(chartPanel, BorderLayout.CENTER);

    

    

    mainFrame.add(mainPanel, BorderLayout.CENTER);

   
    mainFrame.setVisible(true);
}
   
}
// javac -cp "lib/*" -d build/classes $(find src -type f -name "*.java" ! -path "src/test/*")
// java -cp "build/classes:lib/*" experiment.ExperimentMain
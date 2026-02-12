package experiment;

import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFactory;

import java.awt.*;
import java.util.Map;

import model.Team;
import experiment.ExperimentResult; 


public class ChartGenerator {

    public static void showWinPieChart(ExperimentResult result) {

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (Map.Entry<Team, Integer> entry : result.getWinsPerTeam().entrySet()) {
            dataset.setValue(entry.getKey().getName(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Win Distribution",
                dataset,
                true,
                true,
                false
        );

        ChartFrame frame = new ChartFrame("Results - Win Distribution", chart);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
// javac -cp "lib/*" -d build/classes $(find src -type f -name "*.java" ! -path "src/test/*")
// java -cp "build/classes:lib/*" experiment.ExperimentMain

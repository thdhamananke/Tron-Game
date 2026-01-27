package experiment;

import java.io.*;
import java.util.*;

import model.*;

public class CSVExporter {
    public static void export(ExperimentConfig config, ExperimentResult result, String filename) throws IOException {

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {

            for (Map.Entry<Team, Integer> entry : result.getWinsPerTeam().entrySet()) {

                Team team = entry.getKey();
                int wins = entry.getValue();

                double winRate = (double) wins / result.getNumero();

                pw.println(
                        config.getGridSize() + "," +
                        team + "," +
                        winRate + "," +
                        result.getAverageTimeMs() + "," +
                        result.getAverageTurns()
                );
            }
        }
    }
}

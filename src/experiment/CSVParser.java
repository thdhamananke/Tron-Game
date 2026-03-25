package experiment;

import java.io.*;
import java.util.*;

public class CSVParser {
    public static List<Map<String, Object>> parseNewCSV(String filePath) throws IOException {
        List<Map<String, Object>> configs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        Map<String, Object> currentConfig = null;
        List<String> dataLines = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("# CONFIG:")) {
                if (currentConfig != null) {
                    currentConfig.put("data", dataLines);
                    configs.add(currentConfig);
                }
                currentConfig = new HashMap<>();
                currentConfig.put("configLine", line);
                dataLines = new ArrayList<>();
            } else if (line.startsWith("TailleGrille")) {
                // Skip header line for data
            } else if (line.trim().isEmpty()) {
                // Skip empty lines
            } else if (line.matches("\\d+,.+")) {
                dataLines.add(line);
            }
        }
        if (currentConfig != null) {
            currentConfig.put("data", dataLines);
            configs.add(currentConfig);
        }
        reader.close();
        return configs;
    }
}
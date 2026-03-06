package experiment;

import java.util.*;
import java.io.*;
import model.*;

import javax.swing.SwingUtilities;

public class ExperimentConfigwithChartsmain {

    public static void main(String[] args) {

    
        SwingUtilities.invokeLater(() -> {
           
            ExperimentAnalyzer.generateAllCharts(""); 
           //ExperimentAnalyzer.launchGUI();
        });

    }
}
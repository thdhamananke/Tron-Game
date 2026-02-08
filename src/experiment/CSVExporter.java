package experiment;

import java.io.*;
import java.util.*;

import model.*;

public class CSVExporter {

    public static void export(ExperimentConfig config,ExperimentResult result,String filename) throws IOException {

        boolean fileExists = new File(filename).exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {

            if (!fileExists) {
                pw.println("NombreEquipes = " + config.getNbEquipes());
                pw.println("JoueursParEquipe = " + config.getJoueurs());
                pw.println("ProfondeurIA = " + config.getDepth());
                pw.println("NombreParties = " + config.getNbGames());
                pw.println();
            }
            
            pw.println("Equipe,Joueur,Strategie,Heuristique,VictoiresTotal,TauxDeVictoire");
            
            for (Player player : result.getPlayers()) {
                int totalWins = result.getWins(player.getTeam());
                double winRate = result.getWinRate(player.getTeam());

                pw.print(player.getTeam().getName() + ",");
                pw.print(player.getName() + ",");
                pw.print(player.getStrategie().getClass().getSimpleName() + ",");
                pw.print(player.getHeuristic().getClass().getSimpleName() + ",");
                pw.print(totalWins + ",");
                pw.printf(Locale.US, "%.2f%%%n", winRate);
            }
            pw.println();
            
            pw.println("TailleGrille,EquipeGagnante,Point,Note,TempsMoyen,NbMoyenTours");
            for (GameResult gr : result.getHistory()) {
                String winnerName = (gr.getWinner() != null) ? gr.getWinner().getName() : "Match Nul";
                pw.printf(Locale.US, "%d,%s,1,1.0,%.2f,%.2f%n",
                    (config.getNbLignes() * config.getNbColonnes()),
                    winnerName,
                    result.getAverageTimeMs(),
                    result.getAverageTurns()
                );
            }
        }
        
    }
}

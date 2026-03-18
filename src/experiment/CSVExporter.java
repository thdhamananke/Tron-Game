package experiment;

import java.io.*;
import java.util.*;
import model.*;

public class CSVExporter {

    public static void export(ExperimentConfig config, ExperimentResult result, List<Strategie> strategies, String filename) throws IOException {

        boolean fileExists = new File(filename).exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            if (!fileExists) {
                pw.println("Nombre d'Equipes = " + config.getNbEquipes());
                pw.println("Joueurs Par Equipe = " + config.getJoueurs());
                pw.println("Profondeur IA = " + config.getDepth());
                pw.println("Nombre Parties = " + config.getNbGames());
                pw.println();
            }
            
            // debut du resumé
            List<ExperimentResult.ResumeSession> sessions = result.getResumesSession();
            if (!sessions.isEmpty()) {
                List<Team> teams = result.getPlayers().stream().map(Player::getTeam)
                                        .distinct().sorted(Comparator.comparing(Team::getName)).toList();

                
                // En-tête
                pw.print("Parties");
                for (int i = 0; i < teams.size(); i++) pw.print(",T" + (i + 1));
                pw.println(",Conf %");

                pw.print("NbParties");
                for (int i = 0; i < config.getNbEquipes(); i++) {
                    int indexTeam = i * config.getJoueurs(); 
                    Strategie s = strategies.get(indexTeam);
                    
                    pw.print("," + nomCourt(s) + "|" + nomCourt(s.getHeuristic()));
                }
                pw.println(",");

                // boucle sur les sessions
                for (ExperimentResult.ResumeSession session : sessions) {
                    pw.print(session.nbParties); 
                    for (Team team : teams) {
                        pw.printf(Locale.US, ",%.1f%%", session.winRates.getOrDefault(team, 0.0));
                    }
                    pw.printf(Locale.US, ",±%.2f%%%n", session.confidence);
                }

                // Total globale
                pw.print("Total");
                for (Team team : teams) {
                    pw.printf(Locale.US, ",%.1f%%", result.getWinRate(team));
                }

                // la confiance
                double maxConfiance = 0.0;
                for (Team team : teams) {
                    double confEquipe = result.getConfidence(team, 1.96);
                    if (confEquipe > maxConfiance) {
                        maxConfiance = confEquipe;
                    }
                }
                pw.printf(Locale.US, ",±%.2f%%", maxConfiance);
                pw.println();
            }
            pw.println();

            pw.println("TailleGrille,EquipeGagnante,TempsMoyen,NbMoyenTours");
            
            for (GameResult gr : result.getHistory()) {
                String winnerName = (gr.getWinner() != null) ? gr.getWinner().getName() : "Match Nul";
                

                // Ancienne version 
                // pw.printf(Locale.US, "%d,%s,1,1.0,%.2f,%.2f%n",
                //     (config.getNbLignes() * config.getNbColonnes()),
                //     winnerName,
                //     result.getAverageTimeMs(),
                //     result.getAverageTurns()
                // );
                
                // new
                pw.printf(Locale.US, "%d,%s,%d,%d%n",
                    (config.getNbLignes() * config.getNbColonnes()),
                    winnerName,
                    gr.getTimeMs(),      // Temps INDIVIDUEL
                    gr.getTurns()        // Tours INDIVIDUELS
                );
            }
        }
    }

    private static String nomCourt(Object obj) {
        String name = obj.getClass().getSimpleName();
        return switch (name) {
            // Stratégies
            case "MinMaxStrategie" -> "MnS";
            case "AlphaBetaStrategie" -> "ABS";
            case "MaxNStrategie" -> "MaxN";
            case "ParanoidStrategie" -> "PaS";
            case "SOSStrategie" -> "SOS";
            // Heuristiques
            case "FreeSpaceHeuristic" -> "FSH";
            case "VoronoiHeuristic" -> "VoH";
            case "TreeOfChambersHeuristic" -> "TOC";
            default -> name.length() > 3 ? name.substring(0, 3) : name;
        };
    }
}
package experiment;

import java.util.*;
import java.io.*;
import model.*;

public class ExperimentMain {

    public static void main(String[] args) {
        
        // MODE BATCH 
        if (args.length >= 8) {
            runBatchMode(args);
            return;
        }
        
        Scanner sc = new Scanner(System.in);

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         ALLEZ C'EST PARTIE POUR L'EXPERIMENTATION !!      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        GameRunner runner = new GameRunner();
        ExperimentRunner experiment = new ExperimentRunner(runner);

        int taille = Main.entier(sc, "Taille du plateau : ");
        int nbEquipes = Main.entier(sc, "Nombre d'équipes : ");
        int nbJoueurs = Main.entier(sc, "Nombre de nbJoueurs par équipe : ");
        int profondeur = Main.entier(sc, "Profondeur de l'IA : ");
        int nbGames = Main.entier(sc, "Nombre de partie : ");

        List<Strategie> strategies = new ArrayList<>();
        Random random = new Random();

        System.out.println("Configuration des stratégies :");
        int modeConfig;
        do {
            System.out.println("1 - Manuel");
            System.out.println("2 - Random");
            modeConfig = Main.entier(sc, "Votre choix : ");
            if (modeConfig < 1 || modeConfig > 2) System.out.println("Choix invalide ! Recommencez.");
        } while (modeConfig < 1 || modeConfig > 2);

        for (int i = 0; i < nbEquipes; i++) {
            int choixStrat, choixHeur;

            if (modeConfig == 2) {
                choixStrat = random.nextInt(5) + 1;
                choixHeur = random.nextInt(3) + 1;
            } else {
                System.out.println("\nConf de l'EQUIPE " + (i + 1));
                
                do {
                    System.out.println("Stratégie ?");
                    System.out.println("1 - MinMax");
                    System.out.println("2 - AlphaBeta");
                    System.out.println("3 - MaxN");
                    System.out.println("4 - Paranoid");
                    System.out.println("5 - SOS");

                    choixStrat = Main.entier(sc, "Votre choix de stratégie : ");
                    if (choixStrat < 1 || choixStrat > 5) System.out.println("Choix invalide ! Recommencez.");
                } while (choixStrat < 1 || choixStrat > 5);

                do {
                    System.out.println("Heuristique ?");
                    System.out.println("1 - FreeSpace");
                    System.out.println("2 - Voronoi");
                    System.out.println("3 - TreeOfChambers");

                    choixHeur = Main.entier(sc, "Votre choix de l'Heuristique : ");
                    if (choixHeur < 1 || choixHeur > 3) System.out.println("Choix invalide ! Recommencez.");
                } while (choixHeur < 1 || choixHeur > 3);
            }

            Heuristic heuristic = switch (choixHeur) {
                case 2 -> new VoronoiHeuristic();
                case 3 -> new TreeOfChambersHeuristic();
                default -> new FreeSpaceHeuristic();
            };

            Strategie strat;
            if (choixStrat == 5) { // SOS
                strat = new SOSStrategie( heuristic, profondeur, new ArrayList<>());
            } else {
                strat = switch (choixStrat) {
                    case 1 -> new MinMaxStrategie(heuristic, profondeur);
                    case 2 -> new AlphaBetaStrategie(heuristic, profondeur);
                    case 3 -> new MaxNStrategie(heuristic, profondeur);
                    case 4 -> new ParanoidStrategie(heuristic, profondeur);
                    default -> new MinMaxStrategie(heuristic, profondeur);
                };
            }

            for (int jouer = 0; jouer < nbJoueurs; jouer++) {
                strategies.add(strat);
            }
            
            if (modeConfig == 2) {
                System.out.println("Equipe " + (i + 1) + " "+ strat.getClass().getSimpleName() + " avec " + heuristic.getClass().getSimpleName());
            }
        }

        ExperimentConfig config = new ExperimentConfig(taille, taille, nbEquipes, nbJoueurs, profondeur, nbGames, strategies);
        ExperimentResult globalResult = new ExperimentResult();
        boolean continuer = true;
        int partiesPourCetteSession = nbGames;

        while (continuer) {
            ExperimentConfig sessionConfig = new ExperimentConfig(taille, taille, nbEquipes, nbJoueurs, profondeur, partiesPourCetteSession, strategies);
           
            System.out.println("\nExpérimentation de " + partiesPourCetteSession + " parties en cours.... !");
            ExperimentResult sessionRes = experiment.run(sessionConfig); 

            System.out.println("Expérimentation terminée !");
            System.out.println("Tours joués dans cette session : " + sessionRes.getTotalTurns());

            Map<Team, Integer> winners = sessionRes.getWinsPerTeam();
            if (winners != null && !winners.isEmpty()) {
                System.out.print("Gagnant(s) de la session : ");
                for (Team team : winners.keySet()) {
                    System.out.print(team.getName() + " ");
                }
                System.out.println();
            } else {
                System.out.println("Match nul sur cette session");
            }

            Map<Team, Double> rates = new HashMap<>();
            for(Team team : sessionRes.getWinsPerTeam().keySet()) {
                rates.put(team, sessionRes.getWinRate(team));
            }

            double maxConfSession = 0.0;
            for (Team team : sessionRes.getWinsPerTeam().keySet()) {
                double conf = sessionRes.getConfidence(team, 1.96);
                if (conf > maxConfSession) maxConfSession = conf;
            }
            globalResult.recordSession(partiesPourCetteSession, rates, maxConfSession);

            for(GameResult gr : sessionRes.getHistory()) {
                globalResult.record(gr);
            }

            System.out.println("\nSession terminée. Total cumulé : " + globalResult.getNbGames() + " parties.");
            
            System.out.println("Voulez-vous relancer une session avec la même config ? ");
            System.out.println("1: OUI ");
            System.out.println("2: NON");
            int choixRelance = Main.entier(sc, "Votre choix : ");
            
            while (choixRelance != 1 && choixRelance != 2) {
                choixRelance = Main.entier(sc, "Choix invalide. Tapez 1 pour OUI ou 2 pour NON : ");
            }

            if (choixRelance == 1) {
                partiesPourCetteSession = Main.entier(sc, "Nombre de parties pour la nouvelle session : ");
                continuer = true;
            } else {
                continuer = false;
            }
        }

        String defaultName = "Exp_" + nbEquipes + "eq_" + profondeur + "depth";
        System.out.println("\nLe nom du fichier ou taper entrer pour garder le (nom par défaut : " + defaultName + ") : ");
        String input = sc.nextLine().trim();
        String csv = input.isEmpty() ? defaultName : input;
        String pdfPath = "pdf/" + csv + ".pdf";

        File csvDir = new File("csv");
        File pdfDir = new File("pdf");
        if (!csvDir.exists() && !pdfDir.exists()) {
            csvDir.mkdirs();
            pdfDir.mkdirs();
        }

        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        try {
            PDFExporter.export(config, globalResult, strategies, pdfPath);
            System.out.println("PDF généré avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur PDF : " + e.getMessage());
        }
         String filePath = "";
        try {
            filePath = "csv/" + csv + ".csv";
            CSVExporter.export(config, globalResult, strategies, filePath);
            System.out.println("Résultats exportés avec succès dans csv !");
            ExperimentAnalyzer.generateAllCharts(filePath); 
            System.out.println("succès!");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV : " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // ExperimentResult result = experiment.run(config);

        // ExperimentAnalyzer.generateAllCharts(result);
        // ExperimentResult result1 = experiment.run(config);

        // ChartGenerator.showWinPieChart(globalResult);

    }

    // ========== MODE BATCH ==========
   // ========== MODE BATCH ==========
private static void runBatchMode(String[] args) {
    try {
        int taille = Integer.parseInt(args[0]);
        int nbEquipes = Integer.parseInt(args[1]);
        int nbJoueurs = Integer.parseInt(args[2]);
        int profondeur = Integer.parseInt(args[3]);
        
        // ICI : séparer les stratégies et heuristiques si elles contiennent une virgule
        String[] strategiesList = args[4].contains(",") ? args[4].split(",") : new String[]{args[4]};
        String[] heuristicsList = args[5].contains(",") ? args[5].split(",") : new String[]{args[5]};
        
        int nbGames = Integer.parseInt(args[6]);
        boolean isMix = Boolean.parseBoolean(args[7]);
        String csvFile = args.length > 8 ? args[8] : "csv/resultat.csv";
        String pdfFile = args.length > 9 ? args[9] : "pdf/resultat.pdf";

        System.out.println("Mode batch - Configuration:");
        System.out.println("  Plateau: " + taille + "x" + taille);
        System.out.println("  Équipes: " + nbEquipes);
        System.out.println("  Joueurs/équipe: " + nbJoueurs);
        System.out.println("  Profondeur: " + profondeur);
        System.out.println("  Stratégies: " + args[4]);
        System.out.println("  Heuristiques: " + args[5]);
        System.out.println("  Parties: " + nbGames);
        System.out.println("  CSV: " + csvFile);
        System.out.println("  PDF: " + pdfFile);

        List<Strategie> strategies = new ArrayList<>();
        
        // Pour chaque équipe, utiliser la stratégie et heuristique correspondante
        for (int i = 0; i < nbEquipes; i++) {
            String stratName = strategiesList[i % strategiesList.length];
            String heurName = heuristicsList[i % heuristicsList.length];
            
            Heuristic heuristic = createHeuristic(heurName);
            Strategie strat = createStrategie(stratName, heuristic, profondeur);
            
            // Ajouter les joueurs de cette équipe
            for (int j = 0; j < nbJoueurs; j++) {
                strategies.add(strat);
            }
        }

        ExperimentConfig config = new ExperimentConfig(
            taille, taille, nbEquipes, nbJoueurs, 
            profondeur, nbGames, strategies
        );

        GameRunner runner = new GameRunner();
        ExperimentRunner experiment = new ExperimentRunner(runner);
        ExperimentResult result = experiment.run(config);

        PDFExporter.export(config, result, strategies, pdfFile);
        CSVExporter.export(config, result, strategies, csvFile);
        
        System.out.println("Expérience terminée avec succès!");
        
    } catch (Exception e) {
        System.err.println(" ERREUR: " + e.getMessage());
        e.printStackTrace();
    }
}
    private static Heuristic createHeuristic(String name) {
        switch(name) {
            case "FreeSpaceHeuristic": return new FreeSpaceHeuristic();
            case "VoronoiHeuristic": return new VoronoiHeuristic();
            case "TreeOfChambersHeuristic": return new TreeOfChambersHeuristic();
            default: 
                System.out.println("Heuristique inconnue: " + name + ", utilisation FreeSpace");
                return new FreeSpaceHeuristic();
        }
    }

    private static Strategie createStrategie(String name, Heuristic h, int depth) {
        switch(name) {
            case "MinMaxStrategie": return new MinMaxStrategie(h, depth);
            case "AlphaBetaStrategie": return new AlphaBetaStrategie(h, depth);
            case "MaxNStrategie": return new MaxNStrategie(h, depth);
            case "ParanoidStrategie": return new ParanoidStrategie(h, depth);
            case "SOSStrategie": return new SOSStrategie( h, depth ,new ArrayList<>());
            default:
                System.out.println("Stratégie inconnue: " + name + ", utilisation MinMax");
                return new MinMaxStrategie(h, depth);
        }
    }
}
package experiment;

import java.util.*;
import java.io.*;
import model.*;
public class ExperimentMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Mode d'expérimentation :");
        System.out.println("1 - Console");
        System.out.println("2 - Graphique");

        int mode = Main.entier(sc, "Votre choix : ");

        GameRunner runner = (mode == 2) ? new GraphicGameRunner() : new ConsoleGameRunner();
        ExperimentRunner experiment = new ExperimentRunner(runner);

        int taille = Main.entier(sc, "Taille du plateau : ");
        int equipes = Main.entier(sc, "Nombre d'équipes : ");
        int joueurs = Main.entier(sc, "Nombre de joueurs par équipe : ");
        int profondeur = Main.entier(sc, "Profondeur de l'IA : ");
        int nbGames = Main.entier(sc, "Nombre de partie : ");

        List<Strategie> strategies = new ArrayList<>();
        int totalPlayers = equipes * joueurs;

        for (int i = 0; i < totalPlayers; i++) {
            System.out.println("Stratégie et Heuristique du joueur " + (i + 1));

            System.out.println("Stratégie ?");
            System.out.println("1 - MinMax");
            System.out.println("2 - AlphaBeta");
            int choix = Main.entier(sc, "Votre choix : ");

            System.out.println("Heuristique ?");
            System.out.println("1 - FreeSpace");
            System.out.println("2 - Advanced");
            int heuris = Main.entier(sc, "Votre choix : ");

            Heuristic heuristic = (heuris == 2) ? new VoronoiHeuristic() : new FreeSpaceHeuristic();

            Strategie strat = (choix == 2)
                    ? new AlphaBetaStrategie(heuristic, profondeur)
                    : new MinMaxStrategie(heuristic, profondeur);

            strategies.add(strat);
        }

        ExperimentConfig config = new ExperimentConfig(taille, taille, equipes, joueurs, profondeur, nbGames, strategies);
       
        System.out.println("Expérimentation en cours.... !");
        ExperimentResult result = experiment.run(config);

        System.out.println("Expérimentation terminée !");
        System.out.println("Tours joués : " + result.getTotalTurns());

        Map<Team, Integer> winners = result.getWinsPerTeam();
        if (winners != null && !winners.isEmpty()) {
            System.out.print("Gagnant(s) : ");
            for (Team t : winners.keySet()) {
                System.out.print(t.getName() + " ");
            }
            System.out.println();
        } else {
            System.out.println("Match nul");
        }

        System.out.println("\nDonnez le nom du fichier CSV pour l'export : ");
        String csv = sc.nextLine().trim();

        File csvDir = new File("csv");
        if (!csvDir.exists()) {
            csvDir.mkdirs();
        }

        String filePath = "csv/" + csv + ".csv";
        try {
            CSVExporter.export(config, result, filePath);
            System.out.println("Résultats exportés dans avec succès dans csv/ !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV : " + e.getMessage());
        }

    }
}

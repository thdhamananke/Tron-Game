package experiment;

import java.util.*;
import java.io.*;
import model.*;

public class ExperimentMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========== Configuration de l'expérimentation ==========");
        int gridSize = entier(scanner, "Taille de la grille (ex: 10 pour 10x10) : ");
        int teamSize = entier(scanner, "Nombre de joueurs par équipe : ");
        int depth = entier(scanner, "Profondeur MinMax / Alpha-Beta : ");
        int nbGames = entier(scanner, "Nombre de parties à jouer : ");

        // les noms des équipes
        System.out.print("Nom de l'équipe A : ");
        String teamAName = scanner.nextLine().trim();
        System.out.print("Nom de l'équipe B : ");
        String teamBName = scanner.nextLine().trim();

        // Création du plateau vide
        Plateau plateau = new Plateau(gridSize, gridSize);

        // Crée la liste des joueurs pour chaque équipe
        List<Player> playersA = playersForTeam(teamAName, teamSize, plateau, Color.RED);
        List<Player> playersB = playersForTeam(teamBName, teamSize, plateau, Color.BLUE);

        // Création des équipes avec leurs joueurs
        Team teamA = new Team(teamAName, playersA, Color.RED);
        Team teamB = new Team(teamBName, playersB, Color.BLUE);

        List<Team> teams = List.of(teamA, teamB);

        // Création de la configuration d'expérimentation
        ExperimentConfig config = new ExperimentConfig(gridSize, teamSize, depth, nbGames, teams);

        // Lancer l'expérimentation
        ExperimentRunner runner = new ExperimentRunner();
        ExperimentResult result = runner.run(config);

        System.out.print("Nom du fichier CSV de sortie : ");
        String filename = scanner.nextLine().trim();

        try {
            CSVExporter.export(config, result, filename);
            System.out.println("Résultats exportés dans " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV : " + e.getMessage());
        }

        // Affichage rapide des résultats dans la console
        System.out.println("=== Résultats ===");
        for (Team team : teams) {
            System.out.printf("Taux de victoire %s : %.2f%%%n", team.getName(), result.getWinRate(team) * 100);
        }
        System.out.printf("Durée moyenne par partie : %.2f ms%n", result.getAverageTimeMs());
        System.out.printf("Nombre moyen de tours : %.2f%n", result.getAverageTurns());

        scanner.close();
    }

    /** Lecture sécurisée d'un entier positif */
    private static int entier(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                if (value > 0) break; 
                else System.out.println("Merci d'entrer un entier positif.");
            } else {
                System.out.println("Erreur : veuillez entrer un entier.");
                scanner.nextLine();
            }
        }
        return value;
    }

    private static List<Player> playersForTeam(String teamName, int teamSize, Plateau plateau, Color color) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            Position pos = findEmptyPosition(plateau);
            Player player = new Player(teamName + "_" + i, null, pos);
            players.add(player);
            plateau.placerJoueur(pos, player);
        }
        return players;
    }

    private static Position findEmptyPosition(Plateau plateau) {
        Random rand = new Random();
        int row, col;
        Position pos;
        do {
            row = rand.nextInt(plateau.getNbLignes());
            col = rand.nextInt(plateau.getNbColonnes());
            pos = new Position(row, col);
        } while (!plateau.estLibre(pos));
        return pos;
    }
}

package experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import controller.*;
import model.*;

public class ExperimentRunner {
    private GameResult runSingleGame(ExperimentConfig config) {

        Plateau plateau = new Plateau(config.getGridSize(), config.getGridSize());
        // GameController controller = new GameController(plateau);

        // Création des équipes
        Map<Team, List<Player>> teams = new HashMap<>();

        for (Team team : config.getTeams()) {
            List<Player> players = createTeam(team, config.getTeamSize(), plateau);
            teams.put(team, players);
            //Strategie strategy = new MinMaxStrategy(
             //       new AdvancedHeuristic(), config.getDepth()
            //);
            for (Player player : players) {
                // controller.addPlayer(player, strategy);
            }
        }

        long start = System.nanoTime();
        // int turns = controller.runGame();
        long end = System.nanoTime();

        // Team winningTeam = controller.getWinningTeam();

        // return new GameResult(winningTeam, end - start, turns);
        return null;
    }

    public ExperimentResult run(ExperimentConfig config) {

        ExperimentResult result = new ExperimentResult();

        for (int i = 0; i < config.getNbGames(); i++) {
            GameResult game = runSingleGame(config);
            result.recordGame(
                    game.getWinningTeam(),
                    game.getGameTimeMs(),
                    game.getTurns()
            );
        }
        return result;
    }

    private List<Player> createTeam(Team team, int teamSize, Plateau plateau) {
        List<Player> players = new ArrayList<>();
        
        for (int i = 0; i < teamSize; i++) {
            // Position initiale on peux la randomiser ou la définir comme tu veux
            Position startPos = findEmptyPosition(plateau);
            
            Player player = new Player(team.getName() + "_" + i, team, startPos);
            players.add(player);
            plateau.placerJoueur(startPos, player);
        }
        return players;
    }

    /** Trouve une cellule libre sur le plateau pour placer un joueur */
    private Position findEmptyPosition(Plateau plateau) {
        Random rand = new Random();
        int row, col;
        
        do {
            row = rand.nextInt(plateau.getNbLignes());
            col = rand.nextInt(plateau.getNbColonnes());
        } while (!plateau.estLibre(new Position(row, col)));
        
        return new Position(row, col);
    }

}

package experiment;

import model.*;
import java.util.*;

public class ConsoleGameRunner implements GameRunner {

    @Override
    public GameResult runGame(ExperimentConfig config) {

        Plateau plateau = new Plateau(config.getNbLignes(),config.getNbColonnes());
        List<Strategie> strategies = config.getStrategies();
        List<Player> joueurs = new ArrayList<>();

        List<Team> equipes = new ArrayList<>();

         Color[] colors = couleursAleatoires(config.getNbEquipes());

         for (int i = 0; i < config.getNbEquipes(); i++) {
            Color color = colors[i];
            Team team = new Team("Equipe_" + (i + 1), new ArrayList<>(), color);

            for (int j = 0; j < config.getJoueurs(); j++) {
                Position pos = randomEmptyPosition(plateau);
                Player player = new Player(team.getName() + "_" + (j + 1), team, pos);

                // assigner la stratégie et l'heuristique
                Strategie strat = strategies.get(i); 
                player.setStrategie(strat);
                player.setHeuristic(strat.getHeuristic()); 
                
                team.getMembers().add(player);
                joueurs.add(player);
                plateau.placerJoueur(pos, player);

            }

            equipes.add(team);
        }

        ModeleJeu modele = new ModeleJeu(config.getNbLignes(), config.getNbColonnes(), joueurs );
        modele.demarrer();

        int maxTours = calculerMaxTours(config.getNbLignes(), config.getNbColonnes(), joueurs.size());

        long start = System.currentTimeMillis();

        int tour = 0;
        while (!modele.estTermine() && tour < maxTours) {
            tour++;
            List<Direction> coups = new ArrayList<>();

            for (int i = 0; i < joueurs.size(); i++) {
                Player player = joueurs.get(i);
                if (player.isAlive()) {
                    coups.add(
                        player.getStrategie().calculerMouvement(player, plateau)
                    );
                } else {
                    coups.add(Direction.HAUT);
                }
            }

            modele.tourSuivant(coups);
        }

        long time = System.currentTimeMillis() - start;

        return new GameResult(modele.getEquipeGagnante(), time, modele.getTour(), joueurs);
    }


    private int calculerMaxTours(int ligne, int colonne, int joueurs) {
        return (int) (ligne * colonne * Math.max(0.5, 1.0 - 0.1 * (joueurs - 1)));
    }

    /**
     * Determine une coleur aleatroire pour les equipes
     * @param nbEquipes nombre d'equipe
     * @return  la couleur
    */
    private static Color[] couleursAleatoires(int nbEquipes) {
        Color[] result = new Color[nbEquipes];
        List<Color> available = new ArrayList<>(Arrays.asList(Color.values()));
        Random rand = new Random();

        for (int i = 0; i < nbEquipes; i++) {
            int index = rand.nextInt(available.size());
            result[i] = available.get(index);
            available.remove(index);
        }
        return result;
    }

    private Position randomEmptyPosition(Plateau plateau) {
        Random random = new Random();
        Position position;
        int essais = 0;
        do {
            position = new Position(
                random.nextInt(plateau.getNbLignes()),
                random.nextInt(plateau.getNbColonnes())
            );
            essais++;
            if (essais > plateau.getNbLignes() * plateau.getNbColonnes()) {
                throw new IllegalStateException("Impossible de trouver une case libre");
            }
        } while (!plateau.estLibre(position));
        return position;
    }

}

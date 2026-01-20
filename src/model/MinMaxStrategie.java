package model;

import java.util.*;

/**
 * Stratégie utilisant l'algorithme Minimax.
 * 
 * Cette stratégie explore récursivement les coups possibles jusqu'à une profondeur maximale
 * et choisit le mouvement qui maximise la valeur retournée par l'heuristique.
*/
public class MinMaxStrategie implements Strategie {
    /** Profondeur maximale de recherche */
    private static final int DEPTH_MAX = 10;

    /** Heuristique utilisée pour évaluer le plateau */
    private final Heuristic heuristic;

    /**
     * Crée une stratégie Minimax.
     *
     * @param heuristic heuristique utilisée pour évaluer le plateau
     */
    public MinMaxStrategie(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Calcule le meilleur mouvement pour le joueur courant
     * en utilisant l'algorithme Minimax.
     *
     * @param player joueur courant
     * @param plateau plateau du jeu
     * @return direction choisie
     */
    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {

        int bestValue = Integer.MIN_VALUE;
        Direction bestDirection = null;

        List<Direction> coups = plateau.getCoupsPossibles(player.getPosition());

        for (Direction dir : coups) {

            Plateau copiePlateau = Plateau.copierPlateau(plateau);

            Player copiePlayer = new Player(
                    player.getColor(),
                    new Position(
                            player.getPosition().getRow(),
                            player.getPosition().getCol()
                    )
            );

            // Simulation du coup
            deplacer(copiePlayer, dir, copiePlateau);

            // Minimax
            int valeur = minimax(copiePlateau, copiePlayer, DEPTH_MAX - 1, false);

            if (valeur > bestValue) {
                bestValue = valeur;
                bestDirection = dir;
            }
        }

        return bestDirection;
    }

    /**
     * Implémentation récursive de l'algorithme Minimax.
     *
     * @param plateau état du plateau
     * @param player joueur simulé
     * @param depth profondeur restante
     * @param maximisant true si on maximise, false sinon
     * @return valeur évaluée
     */
    private int minimax(Plateau plateau, Player player, int depth, boolean maximisant) {

        if (depth == 0 || plateau.getCoupsPossibles(player.getPosition()).isEmpty()) {
            return heuristic.evaluate(plateau, player);
        }

        if (maximisant) {
            int bestValue = Integer.MIN_VALUE;
            for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {

                Plateau copiePlateau = Plateau.copierPlateau(plateau);
                Player copiePlayer = new Player(
                        player.getColor(),
                        new Position(player.getPosition().getRow(),
                                     player.getPosition().getCol())
                );

                deplacer(copiePlayer, dir, copiePlateau);

                int value = minimax(copiePlateau, copiePlayer, depth - 1, false);
                bestValue = Math.max(bestValue, value);
            }
            return bestValue;

        } else {
            int worstValue = Integer.MAX_VALUE;
            for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {

                Plateau copiePlateau = Plateau.copierPlateau(plateau);
                Player copiePlayer = new Player(
                        player.getColor(),
                        new Position(player.getPosition().getRow(),
                                     player.getPosition().getCol())
                );

                deplacer(copiePlayer, dir, copiePlateau);

                int value = minimax(copiePlateau, copiePlayer, depth - 1, true);
                worstValue = Math.min(worstValue, value);
            }
            return worstValue;
        }
    }

    /**
     * Simule le déplacement d’un joueur sur le plateau.
     * L'ancienne position devient un mur et la nouvelle est occupée par le joueur.
     *
     * @param player joueur à déplacer
     * @param dir direction du déplacement
     * @param plateau plateau concerné
    */
    private void deplacer(Player player, Direction dir, Plateau plateau) {

        Position ancienne = player.getPosition();
        Position nouvelle = ancienne.move(dir);


        plateau.placerMur(ancienne, player);

        player.setPosition(nouvelle);
        plateau.placerJoueur(nouvelle, player);
    }

    /**
     * @return nom de la stratégie
    */
    @Override
    public String getNom() {
        return "Stratégie MINMAX";
    }
}

package model;

import java.util.*;
/**
 * Stratégie d'IA basée sur l'algorithme Minimax avec élagage Alpha-Beta pour le jeu Tron.
*/
public class AlphaBetaStrategie extends AbstractStrategie {

    public AlphaBetaStrategie(Heuristic heuristic, int depth) {
        super(heuristic , depth);
    }

    /**
     * Calcul le mouvement du joueur sur le plateau du jeu
     * @param me le joueur concerné
     * @param plateau le plateau du jeu
     * @return une direction donnée
    */
    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDirection = Direction.HAUT;

        Player opponent = findOpponent(me, plateau);

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Direction dir : plateau.getCoupsPossibles(me.getPosition())) {

            MoveBackup backup = applyMove(plateau, me, dir);
            double value = minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta, false);
            undoMove(plateau, me, backup);

            if (value > bestValue) {
                bestValue = value;
                bestDirection = dir;
            }

            alpha = Math.max(alpha, bestValue);
        }

        return bestDirection;
    }

    /**
     * Implémentation récursive de l'algorithme Minimax avec élagage Alpha-Beta.
     * @param plateau état courant du plateau
     * @param me joueur maximisant (joueur initial)
     * @param opponent adversaire du joueur maximisant
     * @param depth profondeur restante de recherche
     * @param alpha borne inférieure (meilleure valeur garantie pour MAX)
     * @param beta borne supérieure (meilleure valeur garantie pour MIN)
     * @param maximizing indique si le joueur courant maximise ou minimise
     * @return valeur heuristique de l'état exploré
    */
    private double minimaxAlphaBeta(Plateau plateau, Player me, Player opponent,
                                    int depth, double alpha, double beta, boolean maximizing) {

        Player currentPlayer = maximizing ? me : opponent;
        List<Direction> coups = plateau.getCoupsPossibles(currentPlayer.getPosition());

        if (depth == 0 || coups.isEmpty() || !currentPlayer.isAlive()) {
            return heuristic.evaluate(plateau, me);
        }

        if (maximizing) {
            double value = Double.NEGATIVE_INFINITY;
            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, currentPlayer, dir);
                value = Math.max(value,
                        minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta, false));
                undoMove(plateau, currentPlayer, backup);

                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;
            }
            return value;

        } else {
            double value = Double.POSITIVE_INFINITY;
            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, currentPlayer, dir);
                value = Math.min(value,
                        minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta, true));
                undoMove(plateau, currentPlayer, backup);

                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
            return value;
        }
    }

    /**
     * Elle permet de trouver l'adversaire
     * @param me    le joueur courant
     * @param plateau   le plateau de jeu
     * @return  l'adversaire
    */
    private Player findOpponent(Player me, Plateau plateau) {
        for (int ligne = 0; ligne < plateau.getNbLignes(); ligne++) {
            for (int colonne = 0; colonne < plateau.getNbColonnes(); colonne++) {
                Player player = plateau.getCellule(new Position(ligne, colonne)).getOwner();
                if (player != null && player != me && player.isAlive()) {
                    return player;
                }
            }
        }
        return null;
    }


    @Override
    public String getNom() {
        return "MINMAX Alpha-Beta";
    }
}

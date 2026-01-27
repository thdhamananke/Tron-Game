package model;

import java.util.List;

/**
 * Stratégie d'IA basée sur l'algorithme Minimax avec élagage Alpha-Beta
 * pour le jeu Tron.
 */
public class MinMaxStrategy extends AbstractStrategie {


    public MinMaxStrategy(Heuristic heuristic, int depth) {
        super(heuristic , depth);
        
    }

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

package model;

import java.util.List;

public class ParanoidStrategie extends AbstractStrategie {
    private final List<Player> joueurs;

    public ParanoidStrategie(Heuristic heuristic, List<Player> joueurs, int depth) {
        super(heuristic, depth);
        this.joueurs = joueurs;
    }

    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());
        if (coups.isEmpty()) return Direction.NONE; 

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDir = Direction.NONE;

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Direction dir : coups) {
            MoveBackup backup = applyMove(plateau, me, dir);
            double value = paranoid(plateau, me, depth - 1, alpha, beta, true);
            undoMove(plateau, me, backup);

            if (value > bestValue) {
                bestValue = value;
                bestDir = dir;
            }

            alpha = Math.max(alpha, bestValue);
        }

        return bestDir;
    }

    private double paranoid(Plateau plateau, Player me, int depth,
                            double alpha, double beta, boolean maximiserJoueur) {

        if (depth == 0 || !me.isAlive()) {
            return heuristic.evaluate(plateau, me);
        }

        if (maximiserJoueur) { 
            double maxVal = Double.NEGATIVE_INFINITY;
            List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());

            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, me, dir);
                double val = paranoid(plateau, me, depth - 1, alpha, beta, false);
                undoMove(plateau, me, backup);

                maxVal = Math.max(maxVal, val);
                alpha = Math.max(alpha, maxVal);

                if (beta <= alpha) break; 
            }
            return maxVal;
        }

        double minVal = Double.POSITIVE_INFINITY;

        for (Player player : joueurs) {
            if (player == me || !player.isAlive()) continue;

            List<Direction> coups = plateau.getCoupsPossibles(player.getPosition());
            if (coups.isEmpty()) continue; 

            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, player, dir);
                double val = paranoid(plateau, me, depth - 1, alpha, beta, true);
                undoMove(plateau, player, backup);

                minVal = Math.min(minVal, val);
                beta = Math.min(beta, minVal);

                if (beta <= alpha) break; 
            }
        }

        return minVal;
    }

    @Override
    public String getName() {
        return "Stratégie Paranoid";
    }
}

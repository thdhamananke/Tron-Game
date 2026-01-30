package model;

import java.util.*;

public class ParanoidStrategie extends AbstractStrategie {
    private final List<Player> joueurs;

    public ParanoidStrategie(Heuristic heuristic, List<Player> joueurs, int depth) {
        super(heuristic, depth);
        this.joueurs = joueurs;
    }

    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDir = null;

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Direction dir : plateau.getCoupsPossibles(me.getPosition())) {

            MoveBackup backup = applyMove(plateau, me, dir);
            double value = paranoidMin(plateau, me, depth-1, alpha, beta);
            undoMove(plateau, me, backup);

            if (value > bestValue) {
                bestValue = value;
                bestDir = dir;
            }

            alpha = Math.max(alpha, bestValue);
        }

        return bestDir;
    }

    /* ================= ALGORITHME ================= */
    // MAX : joueur paranoïaque
    private double paranoidMax(Plateau plateau, Player me, int depth, double alpha, double beta) {
        if (depth == 0 || !me.isAlive()) {
            return heuristic.evaluate(plateau, me);
        }

        double value = Double.NEGATIVE_INFINITY;

        for (Direction dir : plateau.getCoupsPossibles(me.getPosition())) {
            MoveBackup backup = applyMove(plateau, me, dir);
            value = Math.max(value,
                    paranoidMin(plateau, me, depth - 1, alpha, beta));
            undoMove(plateau, me, backup);

            alpha = Math.max(alpha, value);
            if (alpha >= beta) break;
        }

        return value;
    }

    // MIN : coalition des adversaires
    private double paranoidMin(Plateau plateau, Player me, int depth,
                               double alpha, double beta) {

        if (depth == 0) {
            return heuristic.evaluate(plateau, me);
        }

        double value = Double.POSITIVE_INFINITY;

        for (Player adv : joueurs) {
            if (adv == me || !adv.isAlive()) continue;

            for (Direction dir : plateau.getCoupsPossibles(adv.getPosition())) {
                MoveBackup backup = applyMove(plateau, adv, dir);
                value = Math.min(value,
                        paranoidMax(plateau, me, depth - 1, alpha, beta));
                undoMove(plateau, adv, backup);

                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
        }

        return value;
    }

    @Override
    public String getName() {
        return "Stratégie Paranoid";
    }
}

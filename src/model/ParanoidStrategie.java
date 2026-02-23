package model;

import java.util.List;

public class ParanoidStrategie extends AbstractStrategie {

    private List<Player> joueurs;

    public ParanoidStrategie(Heuristic heuristic, int depth) {
        super(heuristic, depth);
    }

    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        joueurs = getAlivePlayers(plateau);

        List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());
        if (coups.isEmpty()) {
            return Direction.HAUT; 
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDir = coups.get(0);

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Direction dir : coups) {

            Plateau plateauCopie = plateau.copierPlateau();

            Position oldPos = me.getPosition();
            Position newPos = oldPos.move(dir);

            plateauCopie.placerMur(oldPos, me);
            Player playerCopie = new Player(me.getName(), me.getTeam(), newPos);
            plateauCopie.placerJoueur(newPos, playerCopie);

            double value = paranoid(plateauCopie, me, depth - 1, alpha, beta, false);

            if (value > bestValue) {
                bestValue = value;
                bestDir = dir;
            }

            alpha = Math.max(alpha, bestValue);

            if (beta <= alpha) break;
        }

        return bestDir;
    }

    private double paranoid(Plateau plateau,Player me, int depth, double alpha, double beta, boolean maximiserJoueur) {

        if (depth == 0 || !me.isAlive()) {
            return heuristic.evaluate(plateau, me);
        }

        if (maximiserJoueur) {

            double maxVal = Double.NEGATIVE_INFINITY;
            List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());

            if (coups.isEmpty()) {
                return heuristic.evaluate(plateau, me);
            }

            for (Direction dir : coups) {

                Plateau plateauCopie = plateau.copierPlateau();

                Position oldPos = me.getPosition();
                Position newPos = oldPos.move(dir);

                plateauCopie.placerMur(oldPos, me);
                Player playerCopie = new Player(me.getName(), me.getTeam(), newPos);
                plateauCopie.placerJoueur(newPos, playerCopie);

                double val = paranoid(plateauCopie, me, depth - 1, alpha, beta, false);

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

                Plateau plateauCopie = plateau.copierPlateau();

                Position oldPos = player.getPosition();
                Position newPos = oldPos.move(dir);

                plateauCopie.placerMur(oldPos, player);
                Player playerCopie = new Player(player.getName(), player.getTeam(), newPos);
                plateauCopie.placerJoueur(newPos, playerCopie);

                double val = paranoid(plateauCopie, me, depth - 1, alpha, beta, true);

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

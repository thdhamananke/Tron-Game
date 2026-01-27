package model;

import java.util.List;

/**
 * Stratégie utilisant l'algorithme Minimax simple
 */
public class MinMaxStrategie extends AbstractStrategie {

    public MinMaxStrategie(Heuristic heuristic , int depth) {
        super(heuristic , depth);
    }

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {

        int bestValue = Integer.MIN_VALUE;
        Direction bestDirection = Direction.HAUT;

        List<Direction> coups = plateau.getCoupsPossibles(player.getPosition());

        for (Direction dir : coups) {

            Plateau copiePlateau = Plateau.copierPlateau(plateau);

            Player copiePlayer = new Player(
                    player.getName(),
                    player.getTeam(),
                    new Position(player.getPosition().getRow(),
                                 player.getPosition().getCol())
            );

            // Simulation du coup
            deplacer(copiePlayer, dir, copiePlateau);

            // Minimax
            int valeur = minimax(copiePlateau, copiePlayer, super.getDepth()- 1, false);

            if (valeur > bestValue) {
                bestValue = valeur;
                bestDirection = dir;
            }
        }

        return bestDirection;
    }

    private int minimax(Plateau plateau, Player player, int depth, boolean maximisant) {

        if (depth == 0 || plateau.getCoupsPossibles(player.getPosition()).isEmpty()) {
            return (int) heuristic.evaluate(plateau, player);
        }

        if (maximisant) {
            int bestValue = Integer.MIN_VALUE;
            for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {

                Plateau copiePlateau = Plateau.copierPlateau(plateau);
                Player copiePlayer = new Player(
                        player.getName(),
                        player.getTeam(),
                        new Position(player.getPosition().getRow(),
                                     player.getPosition().getCol())
                );

                deplacer(copiePlayer, dir, copiePlateau);

                int value = minimax(copiePlateau, copiePlayer, super.getDepth() - 1, false);
                bestValue = Math.max(bestValue, value);
            }
            return bestValue;

        } else {
            int worstValue = Integer.MAX_VALUE;
            for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {

                Plateau copiePlateau = Plateau.copierPlateau(plateau);
                Player copiePlayer = new Player(
                        player.getName(),
                        player.getTeam(),
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

    // On réutilise le deplacer() de AbstractStrategie
    private void deplacer(Player player, Direction dir, Plateau plateau) {
        // Ici, on peut utiliser applyMove si on veut suivre le pattern Do/Undo
        MoveBackup backup = applyMove(plateau, player, dir);
        // Rien à undo ici puisque c’est une simulation isolée
        undoMove(plateau, player, backup);
    }

    @Override
    public String getNom() {
        return "Stratégie MINMAX";
    }
}

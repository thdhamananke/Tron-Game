package model;

import java.util.*;

/**
 * Heuristique qui estime le territoire futur contrôlé par chaque joueur
 * en simulant une propagation simultanée (flood-fill multi-source).

 * Chaque case est attribuée au joueur qui y arrive en premier.
 * En cas d'égalité parfaite, la case devient contestée (neutre).
*/
public class AdvancedHeuristic implements Heuristic {

    private static final double TERRITORY_WEIGHT = 1.0;

    @Override
    public String getName() {
        return "Heuristique Simple";
    }

    @Override
    public double evaluate(Plateau plateau, Player player) {
        if (!player.isAlive()) {
            return -1000000.0;
        }

        int rows = plateau.getNbLignes();
        int cols = plateau.getNbColonnes();

        // Structures pour le flood-fill
        int[] distance = new int[rows * cols];
        Team[] ownerTeam = new Team[rows * cols];

        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(ownerTeam, null);

        Queue<Integer> queue = new ArrayDeque<>();

        // Initialisation chaque joueur vivant démarre de sa position
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position currentPos = new Position(row, col);
                Cellule cell = plateau.getCellule(currentPos);
                Player owner = cell.getOwner();
                if (owner != null && owner.isAlive()) {
                    int index = row * cols + col;
                    distance[index] = 0;
                    ownerTeam[index] = owner.getTeam(); 
                    queue.add(index);
                }
            }
        }

        // Propagation multi-source
        while (!queue.isEmpty()) {
            int currentIndex = queue.poll();
            int row = currentIndex / cols;
            int col = currentIndex % cols;
            int curDist = distance[currentIndex];
            Team curtTeam = ownerTeam[currentIndex];

            Position currentPos = new Position(row, col);

            for (Direction dir : Direction.values()) {
                Position nextPos = currentPos.move(dir);

                if (plateau.estDansPlateau(nextPos) && plateau.estLibre(nextPos)) {
                    int nextIndex = nextPos.getRow() * cols + nextPos.getCol();

                    if (distance[nextIndex] == Integer.MAX_VALUE) {
                        distance[nextIndex] = curDist + 1;
                        ownerTeam[nextIndex] = curtTeam;
                        queue.add(nextIndex);
                    } else if (distance[nextIndex] == curDist + 1 && ownerTeam[nextIndex] != curtTeam) {
                        ownerTeam[nextIndex] = null; // zone contestée
                    }
                }
            }
        }

        // Calcul final du score
        int score = 0;
        Team myTeam = player.getTeam();

        for (int i = 0; i < ownerTeam.length; i++) {
            Team team = ownerTeam[i];
            if (team == myTeam) {
                score++;
            } else if (team != null) {
                score--; // cellule contrôlée par une autre équipe
            }
        }

        return TERRITORY_WEIGHT * score;

    }
}
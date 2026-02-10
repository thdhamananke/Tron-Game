package model;

import java.util.*;

/**
 * Heuristique de base qui compte simplement les cases accessibles pour le joueur.
 * Simple, rapide, fonctionne pour 2 joueurs ou plusieurs.
*/
public class FreeSpaceHeuristic implements Heuristic {

    @Override
    public String getName() {
        return "Heuristique Simple";
    }

    @Override
    public double evaluate(Plateau grid, Player player) {
        if (!player.isAlive()) {
            return -1000000.0;
        }
        return countAccessibleCells(grid, player.getPosition());
    }

    private int countAccessibleCells(Plateau grid, Position posi) {
        if (!grid.estDansPlateau(posi)) return 0;
        int count = 1; 

        boolean[][] visited = new boolean[grid.getNbLignes()][grid.getNbColonnes()];

        Queue<Position> queue = new LinkedList<>();
        queue.add(posi);
        visited[posi.getRow()][posi.getCol()] = true;

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            // On regarde les 4 directions
            for (Direction dir : Direction.values()) {
                Position next = current.move(dir);

                if (grid.estDansPlateau(next)
                        && !visited[next.getRow()][next.getCol()]
                        && grid.estLibre(next)) {

                    visited[next.getRow()][next.getCol()] = true;
                    queue.add(next);
                    count++;
                }
            }
        }
        return count;
    }
}

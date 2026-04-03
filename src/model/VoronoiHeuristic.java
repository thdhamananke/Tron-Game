package model;

import java.util.*;

/**
 * Heuristique qui estime le territoire futur contrôlé par chaque joueur
 * en simulant une propagation simultanée (flood-fill multi-source).

 * Chaque case est attribuée au joueur qui y arrive en premier.
 * En cas d'égalité parfaite, la case devient contestée (neutre).
*/
public class VoronoiHeuristic implements Heuristic {

    private static final double TERRITORY_WEIGHT = 1.0;

    @Override
    public String getName() {
        return "Heuristique Avancée(Voronoi)";
    }

    @Override
    public double evaluate(Plateau plateau, Player player) {
        if (!player.isAlive()) {
            return -1000000.0;
        }

        int rows = plateau.getNbLignes();
        int cols = plateau.getNbColonnes();

        int[] distance = new int[rows * cols];
        Team[] ownerTeam = new Team[rows * cols];
        Arrays.fill(distance, Integer.MAX_VALUE);

        Queue<Integer> queue = new ArrayDeque<>();

        // optimisation on récupère les positions des joueurs via le plateau
        // plutôt que de scanner toutes les cases vides.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                // On cherche uniquement les têtes des joueurs (pas les murs)
                // Note : adapter selon comment tu stockes la position actuelle
                Player occupant = plateau.getJoueurAt(p); 
                if (occupant != null) {
                    int index = r * cols + c;
                    distance[index] = 0;
                    ownerTeam[index] = occupant.getTeam();
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
            Team curTeam = ownerTeam[currentIndex];

            // Si curTeam est null, c'est une case contestée, on ne propage plus à partir d'ici
            if (curTeam == null) continue; 

            for (Direction dir : Direction.values()) {
                Position nextPos = new Position(
                    row + (dir == Direction.BAS ? 1 : (dir == Direction.HAUT ? -1 : 0)),
                    col + (dir == Direction.DROITE ? 1 : (dir == Direction.GAUCHE ? -1 : 0))
                );

                if (plateau.estDansPlateau(nextPos)) {
                    int nextIndex = nextPos.getRow() * cols + nextPos.getCol();
                    
                    if (plateau.estLibre(nextPos)) {
                        if (distance[nextIndex] == Integer.MAX_VALUE) {
                            distance[nextIndex] = curDist + 1;
                            ownerTeam[nextIndex] = curTeam;
                            queue.add(nextIndex);
                        } else if (distance[nextIndex] == curDist + 1 && ownerTeam[nextIndex] != curTeam) {
                            // Case contestée
                            ownerTeam[nextIndex] = null; 
                        }
                    }
                }
            }
        }

        // Calcul du score orienté équipe
        int teamScore = 0;
        int opponentScore = 0;
        Team team = player.getTeam();

        for (Team t : ownerTeam) {
            if (t == null) continue;
            if (t == team) teamScore++;
            else opponentScore++;
        }

        return (teamScore - opponentScore) * TERRITORY_WEIGHT;
    }
}
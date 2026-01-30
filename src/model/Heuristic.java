package model;

public interface Heuristic {
    /**
     * Évalue l'état actuel du jeu pour un joueur donné.
     * @param grid  L'état actuel de la grille et des joueurs
     * @param player Le joueur pour lequel on calcule l'utilité
     * @return Une valeur numérique (score) représentant la qualité de la position
    */
    double evaluate(Plateau grid, Player player);
    String getName();
}
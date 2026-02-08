package model;

import java.util.*;
/**
 * Représente une position sur la grille du jeu de Tron defini par une ligne et une col
 * 
 * Cette classe est utilisée pour localiser les joueurs, les murs
 * et effectuer des calculs de distance ou de déplacement.
*/
public class Position {
    private int row;
    private int col;

    /**
     * Construit une nouvelle position.
     * @param row la ligne
     * @param col la colonne
    */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** @return la ligne de la position */
    public int getRow() {
        return row;
    }

    /** @return la colonne de la position */
    public int getCol() {
        return col;
    }

    /**
     * Modifie la ligne de la position.
     * @param row nouvelle ligne
    */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Modifie la colonne de la position.
     * @param col nouvelle colonne
    */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Calcul la distance de Manhattan entre cette position et une autre position.
     * @param other autre position
     * @return distance de Manhattan
    */
    public int distanceManhattan(Position other) {
        return Math.abs(this.row - other.row)
             + Math.abs(this.col - other.col);
    }

    /**
     * Crée une copie indépendante de cette position.
     * @return nouvelle instance Position
    */
    public Position copy() {
        return new Position(row, col);
    }

    /**
     * Compare cette position à un autre objet.
     * Deux positions sont égales si elles ont la même ligne et la même colonne.
     * @param obj objet à comparer
     * @return true si les positions sont identiques
    */
    @Override
    public boolean equals(Object obj)  {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    /**
     * Génère le code de hachage de la position.
     * Obligatoire lorsque equals() est redéfini.
     * @return code de hachage
    */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /** @return représentation textuelle de la position */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /**
     * Retourne une nouvelle position déplacée dans la direction donnée.
     * @param direction direction du déplacement
     * @return la nouvelle position
    */
    public Position move(Direction direction) {
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case HAUT:
                newRow--;
                break;
            case BAS:
                newRow++;
                break;
            case GAUCHE:
                newCol--;
                break;
            case DROITE:
                newCol++;
                break;
        }

        return new Position(newRow, newCol);
    }

}

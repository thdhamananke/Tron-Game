package model;

import java.util.*;

/**
 * Représente le plateau du jeu de Tron.
 * Le plateau est une grille de cellules (Cellule) sur laquelle
 * évoluent les joueurs et leurs murs.
*/
public class Plateau {
    private int nbLignes;
    private int nbColonnes;
    private Cellule[][] grille;

    /**
     * Construit un plateau vide.
     *
     * @param nbLignes nombre de lignes
     * @param nbColonnes nombre de colonnes
    */
    public Plateau(int nbLignes, int nbColonnes) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.grille = new Cellule[nbLignes][nbColonnes];
        initialiser();
    }

    /**
     * Initialise le plateau avec des cellules vides.
    */
    private void initialiser() {
        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {
                grille[ligne][colonne] = new Cellule(new Position(ligne, colonne));
            }
        }
    }

    /**
     * @return nombre de lignes du plateau
     */
    public int getNbLignes() {
        return nbLignes;
    }

    /**
     * @return nombre de colonnes du plateau
     */
    public int getNbColonnes() {
        return nbColonnes;
    }

    /**
     * Vérifie si une position est à l'intérieur du plateau.
     *
     * @param position position à tester
     * @return true si la position est valide
     */
    public boolean estDansPlateau(Position position) {
        return position.getRow() >= 0 && position.getRow() < nbLignes
            && position.getCol() >= 0 && position.getCol() < nbColonnes;
    }

    /**
     * Retourne la cellule à une position donnée.
     *
     * @param position position recherchée
     * @return cellule correspondante
     */
    public Cellule getCellule(Position position) {
        if (!estDansPlateau(position)) {
            throw new IllegalArgumentException(
                "Position hors plateau : " + position);
        }
        return grille[position.getRow()][position.getCol()];
    }

    /**
     * Indique si une cellule est vide.
     *
     * @param position position testée
     * @return true si la cellule est vide
     */
    public boolean estLibre(Position position) {
        return estDansPlateau(position)
            && getCellule(position).isEmpty();
    }

    /**
     * Place un joueur sur une cellule.
     *
     * @param position position cible
     * @param joueur joueur à placer
     */
    public void placerJoueur(Position position, Player joueur) {
        if (!estDansPlateau(position)) return;
        getCellule(position).occupy(joueur);
    }

    /**
     * Marque une cellule comme mur.
     *
     * @param position position du mur
     * @param proprietaire joueur ayant créé le mur
     */
    public void placerMur(Position position, Player proprietaire) {
        if (!estDansPlateau(position)) return;
        getCellule(position).occupy(proprietaire);
    }

    /**
     * Vide une cellule (la rend libre).
     *
     * @param position position à vider
     */
    public void viderCellule(Position position) 
    {
        if (!estDansPlateau(position)) return;
        grille[position.getRow()][position.getCol()] = new Cellule(new Position(position.getRow(), position.getCol()));
    }

    /**
     * Retourne la liste des coups possibles depuis une position donnée.
     * Un coup est valide si la cellule cible est libre et dans le plateau.
     *
     * @param position position actuelle
     * @return liste des directions possibles
     */
    public List<Direction> getCoupsPossibles(Position position) 
    {
        List<Direction> coups = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Position suivante = position.move(direction);
            if (estLibre(suivante)) {
                coups.add(direction);
            }
        }
        return coups;
    }

    /**
     * Crée une copie profonde du plateau.
     * Cette méthode est essentielle pour les algorithmes
     * de recherche (Minimax, MAXN, SOS).
     *
     * @param original plateau à copier
     * @return copie indépendante du plateau
     */
    public static Plateau copierPlateau(Plateau original) {
        Plateau copie =
                new Plateau(original.nbLignes, original.nbColonnes);

        for (int ligne = 0; ligne < original.nbLignes; ligne++) {
            for (int colonne = 0; colonne < original.nbColonnes; colonne++) {

                Cellule celluleOriginale =
                        original.grille[ligne][colonne];

                Cellule nouvelleCellule =
                        new Cellule(new Position(ligne, colonne));

                if (!celluleOriginale.isEmpty()) {
                    nouvelleCellule.occupy(celluleOriginale.getOwner());
                }

                copie.grille[ligne][colonne] = nouvelleCellule;
            }
        }
        return copie;
    }



    public CellState[][] getEtatPourVue() {
        CellState[][] etat = new CellState[nbLignes][nbColonnes];

        for (int l = 0; l < nbLignes; l++) {
            for (int c = 0; c < nbColonnes; c++) {
                Cellule cell = grille[l][c];
                if (cell.isEmpty()) {
                    etat[l][c] = CellState.EMPTY;
                } else if (cell.getState()==CellState.WALL) {
                    etat[l][c] = CellState.WALL;
                } else {
                    etat[l][c] = CellState.PLAYER; // ou PLAYER_ROUGE / PLAYER_BLEU
                }
            }
        }
        return etat;
    }

    public void setSize(int rows, int cols) {
        this.nbLignes = rows;
        this.nbColonnes = cols;
        this.grille = new Cellule[rows][cols];
    }

}

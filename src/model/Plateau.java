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

    public void setCellule(Position pos, Cellule cell) {
        if (!estDansPlateau(pos)) return;
        grille[pos.getRow()][pos.getCol()] = cell;
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
     * @return nombre de cellules encore libres sur le plateau
    */
    public int getNbCasesLibres() {
        int count = 0;

        for (int l = 0; l < nbLignes; l++) {
            for (int c = 0; c < nbColonnes; c++) {
                if (grille[l][c].isEmpty()) {
                    count++;
                }
            }
        }
        return count;
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
    public Plateau copierPlateau() {
        Plateau copie = new Plateau(this.nbLignes, this.nbColonnes);
        
        for (int row = 0; row < nbLignes; row++) {
            for (int col = 0; col < nbColonnes; col++) {
                Cellule celluleOriginale = this.grille[row][col];
                if (!celluleOriginale.isEmpty()) {
                    // On occupe la cellule de la copie avec le même propriétaire
                    copie.grille[row][col].occupy(celluleOriginale.getOwner());
                }
            }
        }
        return copie;
    }


    /**
     * Retourne le joueur présent sur une cellule donnée, s'il y en a un.
     * @param pos La position à vérifier
     * @return Le Player occupant la case, ou null si la case est vide ou contient juste un mur.
    */
    public Player getJoueurAt(Position pos) {
        if (!estDansPlateau(pos)) return null;
        
        Cellule cell = getCellule(pos);
        if (!cell.isEmpty() && cell.getState() == CellState.PLAYER) {
            return cell.getOwner();
        }
        return null;
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
        initialiser();
    }

}

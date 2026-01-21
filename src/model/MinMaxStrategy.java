package model;

import java.util.*;

/**
 * Stratégie d'IA basée sur l'algorithme Minimax avec élagage Alpha-Beta 
 * pour le jeu Tron. Cette stratégie explore récursivement les coups possibles jusqu'à
 * une profondeur donnée afin de choisir le mouvement maximisant
 * l'évaluation heuristique du plateau pour le joueur courant.
*/
public class MinMaxStrategy implements Strategie {

    private final int depth;
    private final Heuristic heuristic;

    /**
     * Construit une stratégie Minimax avec élagage Alpha-Beta.
     * @param heuristic heuristique utilisée pour évaluer les positions
     * @param depth profondeur maximale de recherche
    */
    public MinMaxStrategy(Heuristic heuristic, int depth) {
        this.heuristic = heuristic;
        this.depth = depth;
    }

    /**
     * Calcule le meilleur mouvement à jouer pour le joueur courant
     * en appliquant l'algorithme Minimax avec élagage Alpha-Beta.
     *
     * @param me joueur courant
     * @param plateau état actuel du plateau
     * @return direction optimale à jouer
    */
    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDirection = null;

        Player opponent = findOpponent(me, plateau);

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (Direction dir : plateau.getCoupsPossibles(me.getPosition())) {

            MoveBackup backup = applyMove(plateau, me, dir);
            double value = minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta,false);
            undoMove(plateau, me, backup);

            if (value > bestValue) {
                bestValue = value;
                bestDirection = dir;
            }
            alpha = Math.max(alpha, bestValue);
        }

        return bestDirection;
    }

    /**
     * Implémentation récursive de l'algorithme Minimax avec élagage Alpha-Beta.
     *
     * @param plateau état courant du plateau
     * @param me joueur maximisant (joueur initial)
     * @param opponent adversaire du joueur maximisant
     * @param depth profondeur restante de recherche
     * @param alpha borne inférieure (meilleure valeur garantie pour MAX)
     * @param beta borne supérieure (meilleure valeur garantie pour MIN)
     * @param maximizing indique si le joueur courant maximise ou minimise
     * @return valeur heuristique de l'état exploré
    */
    private double minimaxAlphaBeta(Plateau plateau, Player me, Player opponent, int depth, double alpha, double beta, boolean maximizing) {

        Player currentPlayer = maximizing ? me : opponent;
        List<Direction> coups = plateau.getCoupsPossibles(currentPlayer.getPosition());

        if (depth == 0 || coups.isEmpty() || !currentPlayer.isAlive()) {
            return heuristic.evaluate(plateau, me);
        }

        if (maximizing) {
            double value = Double.NEGATIVE_INFINITY;

            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, currentPlayer, dir);
                value = Math.max(value,
                        minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta, false));
                        undoMove(plateau, currentPlayer, backup);

                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;
            }
            return value;

        } else {
            double value = Double.POSITIVE_INFINITY;
            // Ici, on va simuler l'adversaire (pour un Minimax simple à 2 joueurs)
            for (Direction dir : coups) {
                MoveBackup backup = applyMove(plateau, currentPlayer, dir);
                value = Math.min(value,
                        minimaxAlphaBeta(plateau, me, opponent, depth - 1, alpha, beta, true));
                        undoMove(plateau, currentPlayer, backup);

                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
            return value;
        }
    }

    /**
     * Elle est utilitaire, elle permet de trouver l'adversaire.
     * @param me le joueur courant
     * @param plateau le plateau du jeu
     * @return  l'adversaire avec qui on joue sur le meme plateau
    */
    private Player findOpponent(Player me, Plateau plateau) {
        for (int ligne = 0; ligne < plateau.getNbLignes(); ligne++) {
            for (int colonne = 0; colonne < plateau.getNbColonnes(); colonne++) {
                Player player = plateau.getCellule(new Position(ligne, colonne)).getOwner();
                if (player != null && player != me && player.isAlive()) {
                    return player;
                }
            }
        }
        return null;
    }

    /**
     * Structure utilitaire permettant de sauvegarder les informations
     * nécessaires pour annuler un coup (Do / Undo).
    */
    private static class MoveBackup {
        Position oldPosition;
        boolean wallCreated;
    }

    /**
     * Simule le déplacement d’un joueur sur le plateau.
     * L'ancienne position devient un mur et la nouvelle est occupée par le joueur.
     * 
     * @param plateau   plateau concerné
     * @param player    le joueur à déplacer 
     * @param dir       la direction du déplacement
     * @return          un objet permettant d’annuler le coup
    */
    private MoveBackup applyMove(Plateau plateau, Player player, Direction dir) {
        MoveBackup backup = new MoveBackup();

        Position oldPos = player.getPosition();
        Position newPos = oldPos.move(dir);

        backup.oldPosition = oldPos;
        backup.wallCreated = true;

        plateau.placerMur(oldPos, player);
        player.setPosition(newPos);
        plateau.placerJoueur(newPos, player);

        return backup;
    }

    /**
     * Annule un déplacement précédemment simulé à l’aide de applyMove
     * @param plateau plateau concerné
     * @param player joueur à repositionner
     * @param backup informations sauvegardées du coup précédent
    */
    private void undoMove(Plateau plateau, Player player, MoveBackup backup) {
        plateau.viderCellule(player.getPosition());
        player.setPosition(backup.oldPosition);
        plateau.viderCellule(backup.oldPosition);
        plateau.placerJoueur(backup.oldPosition, player);
    }

    @Override
    public String getNom() {
        return "MINMAX Alpha-Beta";
    }
}

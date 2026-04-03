package model;

import java.util.*;
/**
 * Stratégie d'IA basée sur l'algorithme Minimax avec élagage Alpha-Beta pour le jeu Tron.
*/
public class AlphaBetaStrategie extends AbstractStrategie {

    private long startTime;
    private static final long TIME_LIMIT_MS = 500;

    public AlphaBetaStrategie(Heuristic heuristic, int depth) {
        super(heuristic , depth);
    }

    /**
     * Calcul le mouvement du joueur sur le plateau du jeu
     * @param me le joueur concerné
     * @param plateau le plateau du jeu
     * @return une direction donnée
    */
    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {
        startTime = System.currentTimeMillis();

        // Player opponent = findOpponent(me, plateau);
        List<Direction> coups = plateau.getCoupsPossibles(me.getPosition());
        if (coups.isEmpty()) return Direction.HAUT;
        Collections.shuffle(coups); // pour melanger les directions

        double bestValue = Double.NEGATIVE_INFINITY;
        Direction bestDirection = coups.get(0);

        for (Direction dir : coups) {
            Plateau copieSaine = plateau.copierPlateau();
            
            Position ancienne = me.getPosition();
            Position nouvelle = ancienne.move(dir);
            copieSaine.placerMur(ancienne, me);
            
            Player meVirtuel = new Player(me.getName(), me.getTeam(), nouvelle);
            copieSaine.placerJoueur(nouvelle, meVirtuel);
            
            Player opponent = findOpponent(meVirtuel, copieSaine);

            double value = minimaxAlphaBeta(copieSaine, meVirtuel, opponent, depth - 1, 
                                            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);

            if (value > bestValue) {
                bestValue = value;
                bestDirection = dir;
            }
        }
        return bestDirection;
    }


    /**
     * Implémentation récursive de l'algorithme Minimax avec élagage Alpha-Beta.
     * @param plateau état courant du plateau
     * @param me joueur maximisant (joueur initial)
     * @param opponent adversaire du joueur maximisant
     * @param depth profondeur restante de recherche
     * @param alpha borne inférieure (meilleure valeur garantie pour MAX)
     * @param beta borne supérieure (meilleure valeur garantie pour MIN)
     * @param maximizing indique si le joueur courant maximise ou minimise
     * @return valeur heuristique de l'état exploré
    */
 
    private double minimaxAlphaBeta(Plateau plateau, Player me, Player opponent,
                                    int depth, double alpha, double beta, boolean maximizing) {

        if (System.currentTimeMillis() - startTime > TIME_LIMIT_MS) {
            return heuristic.evaluate(plateau, me);
        }

        if (depth == 0 || (maximizing && !me.isAlive()) || (!maximizing && opponent != null && !opponent.isAlive())) {
            return heuristic.evaluate(plateau, me);
        }

        Player current = maximizing ? me : opponent;
        if (current == null) return heuristic.evaluate(plateau, me);

        List<Direction> coups = plateau.getCoupsPossibles(current.getPosition());
        if (coups.isEmpty()) return heuristic.evaluate(plateau, me);

        double value = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (Direction dir : coups) {
            Plateau simulation = plateau.copierPlateau();
            Position ancienne = current.getPosition();
            Position nouvelle = ancienne.move(dir);
            
            simulation.placerMur(ancienne, current);
            Player virtuel = new Player(current.getName(), current.getTeam(), nouvelle);
            simulation.placerJoueur(nouvelle, virtuel);

            // On met à jour les références pour l'appel suivant
            Player nextMe = maximizing ? virtuel : me;
            Player nextOpponent = maximizing ? opponent : virtuel;

            double eval = minimaxAlphaBeta(simulation, nextMe, nextOpponent, depth - 1, alpha, beta, !maximizing);

            if (maximizing) {
                value = Math.max(value, eval);
                alpha = Math.max(alpha, value);
            } else {
                value = Math.min(value, eval);
                beta = Math.min(beta, value);
            }
            if (beta <= alpha) break;
        }
        return value;
    }

    /**
     * Elle permet de trouver l'adversaire
     * @param me    le joueur courant
     * @param plateau   le plateau de jeu
     * @return  l'adversaire
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


    @Override
    public String getName() {
        return "Stratégie AlphaBeta";
    }
}

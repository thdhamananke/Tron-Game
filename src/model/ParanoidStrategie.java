package model;

import java.util.ArrayList;
import java.util.List;

public class ParanoidStrategie implements Strategie {

    private static final int DEPTH_MAX = 5;
    private final Heuristic heuristic;
    private final List<Player> tousLesJoueurs;

    public ParanoidStrategie(Heuristic heuristic, List<Player> tousLesJoueurs) {
        this.heuristic = heuristic;
        this.tousLesJoueurs = tousLesJoueurs;
    }

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        List<Player> adversaires = getAutresJoueurs(player);
        int bestVal = Integer.MIN_VALUE;
        Direction bestDirection = null;
        List<Direction> coupsPossibles = plateau.getCoupsPossibles(player.getPosition());
        
        if (coupsPossibles.isEmpty()) {
            //par defaut
            return Direction.HAUT;
        }
        
        for (Direction direction : coupsPossibles) {
            // Copie du plateau pour la simulation
            Plateau plateauCopie = Plateau.copierPlateau(plateau);
            
            // Copie du joueur courant
            Position pos = player.getPosition();
            Player copiePlayer = new Player(
                player.getName(),
                player.getTeam(),
                new Position(pos.getRow(), pos.getCol())
            );
            
            // Simulation du déplacement 
            deplacer(copiePlayer, direction, plateauCopie);

            // Évaluation du mouvement avec alphabeta
            int valeur = alphabetaMin(plateauCopie, copiePlayer, DEPTH_MAX - 1, 
                                     bestVal, Integer.MAX_VALUE, adversaires);

            if (valeur > bestVal) {
                bestVal = valeur;
                bestDirection = direction;
            }
        }
        
        return bestDirection != null ? bestDirection : coupsPossibles.get(0);
    }
    
    // MAX pour ce joueur 
    private int alphabetaMax(Plateau plateau, Player joueurMax, int depth, 
                            int alpha, int beta, List<Player> adversaires) {
        

        if (depth == 0 || plateau.getCoupsPossibles(joueurMax.getPosition()).isEmpty()) {
            return heuristic.evaluate(plateau, joueurMax);
        }
        
        int bestValue = alpha;
        List<Direction> coups = plateau.getCoupsPossibles(joueurMax.getPosition());
        
        for (Direction dir : coups) {
            /

            Plateau copiePlateau = Plateau.copierPlateau(plateau);
            Position pos = joueurMax.getPosition();
            Player copieJoueurMax = new Player(
                joueurMax.getName(),
                joueurMax.getTeam(),
                new Position(pos.getRow(), pos.getCol())
            );
            
            // Simulation du coup
            deplacer(copieJoueurMax, dir, copiePlateau);
            

            //MIN
            int value = alphabetaMin(copiePlateau, copieJoueurMax, depth - 1, 
                                    bestValue, beta, adversaires);
            

            if (value > bestValue) {
                bestValue = value;
            }
            
            // Coupure beta
            if (bestValue >= beta) {
                return beta;
            }
        }
        
        return bestValue;
    }
    
    // MIN pour la coalition des adversaires
    private int alphabetaMin(Plateau plateau, Player joueurMax, int depth, 
                            int alpha, int beta, List<Player> adversaires) {
        
        if (depth == 0) {
            return heuristic.evaluate(plateau, joueurMax);
        }
        
        int worstValue = beta;
        
        for (Player adversaire : adversaires) {
            // est ce que le joueur est tujours en vie 
            if (!adversaireEstPresent(plateau, adversaire)) {
                continue;
            }
            
            List<Direction> coupsAdversaire = plateau.getCoupsPossibles(adversaire.getPosition());
            
            if (coupsAdversaire.isEmpty()) {
                continue;
            }
            
            for (Direction dir : coupsAdversaire) {

                Plateau copiePlateau = Plateau.copierPlateau(plateau);
                
                Position posMax = joueurMax.getPosition();
                Player copieJoueurMax = new Player(
                    joueurMax.getName(),
                    joueurMax.getTeam(),
                    new Position(posMax.getRow(), posMax.getCol())
                );
                
              
                Position posAdv = adversaire.getPosition();
                Player copieAdversaire = new Player(
                    adversaire.getName(),
                    adversaire.getTeam(),
                    new Position(posAdv.getRow(), posAdv.getCol())
                );
                
                // Simulation du coup de adv
                deplacer(copieAdversaire, dir, copiePlateau);
                
                // liste des restes des advs 
                List<Player> adversairesRestants = new ArrayList<>();
                for (Player adv : adversaires) {
                    if (!adv.equals(adversaire)) {
                        Position pos = adv.getPosition();
                        adversairesRestants.add(new Player(
                            adv.getName(),
                            adv.getTeam(),
                            new Position(pos.getRow(), pos.getCol())
                        ));
                    }
                }
                
                //MAX
                int value = alphabetaMax(copiePlateau, copieJoueurMax, depth - 1, 
                                       alpha, worstValue, adversairesRestants);
                
                //mettre a jour la pire valeur de ce joueur 
                if (value < worstValue) {
                    worstValue = value;
                }
                
                if (worstValue <= alpha) {
                    return alpha;
                }
            }
        }
        
        return worstValue;
    }
    
    private void deplacer(Player player, Direction dir, Plateau plateau) {
        Position ancienne = player.getPosition();
        Position nouvelle = ancienne.move(dir);
        
        plateau.placerMur(ancienne, player);
        
        player.setPosition(nouvelle);
        plateau.placerJoueur(nouvelle, player);
    }
    
    // Méthode pour obtenir les adversaires
    private List<Player> getAutresJoueurs(Player joueurCourant) {
        List<Player> autresJoueurs = new ArrayList<>();
        for (Player joueur : this.tousLesJoueurs) {
            if (!joueur.equals(joueurCourant)) {
                // Créer une copie de l'adversaire
                Position pos = joueur.getPosition();
                autresJoueurs.add(new Player(
                    joueur.getName(),
                    joueur.getTeam(),
                    new Position(pos.getRow(), pos.getCol())
                ));
            }
        }
        return autresJoueurs;
    }
    
    private boolean adversaireEstPresent(Plateau plateau, Player adversaire) {
        // adv as t-il une position valide dans le plateau ???
        Position pos = adversaire.getPosition();
        
        if (!plateau.estDansPlateau(pos)) {
            return false;
        }
        
        // CEllule occcupé par un autre  joueur de la meme equipe
        Cellule cellule = plateau.getCellule(pos);
        if (cellule.isEmpty()) {
            return false;
        }
        
        Player occupant = cellule.getOwner();
        return occupant != null && 
               occupant.getTeam() != null && 
               occupant.getTeam().equals(adversaire.getTeam());
    }

    @Override
    public String getNom() {
        return "Stratégie paranoid";
    }
}
package model;

import java.util.List;

public class SOSStrategie implements Strategie {
    
    private Heuristic heuristic;
    private Direction derniereDirection = null;
    
    public SOSStrategie(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        Position pos = player.getPosition();
        List<Direction> coups = plateau.getCoupsPossibles(pos);
        
        if (coups.isEmpty()) return null;
        if (coups.size() == 1) {
            derniereDirection = coups.get(0);
            return coups.get(0);
        }
        
        Direction meilleur = coups.get(0);
        double meilleurScore = Double.NEGATIVE_INFINITY;
        
        for (Direction dir : coups) {
            double score = evaluerDirection(dir, player, plateau);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleur = dir;
            }
        }
        
        derniereDirection = meilleur;
        return meilleur;
    }
    
    private double evaluerDirection(Direction dir, Player player, Plateau plateau) {
        Position pos = player.getPosition();
        Position nouvelle = pos.move(dir);
        
        Player copiPlayer = new Player(player.getName(), player.getTeam(), nouvelle);
        
        // Score heuristique de base
        double score = heuristic.evaluate(plateau, copiPlayer);
        
        // Critères SOS
        int distanceBord = distanceBord(nouvelle, plateau);
        score += distanceBord * 0.1;
        
        if (plateau.getCoupsPossibles(nouvelle).size() <= 1) {
            score -= 0.5; // Cul-de-sac
        }
        
        if (estOpposee(dir, derniereDirection)) {
            score -= 0.2; // Éviter retour arrière
        }
        
        return score;
    }
    
    private int distanceBord(Position pos, Plateau plateau) {
        int x = pos.getRow();
        int y = pos.getCol();
        return Math.min(Math.min(x, plateau.getNbLignes() - 1 - x),
                       Math.min(y, plateau.getNbColonnes() - 1 - y));
    }
    
    private boolean estOpposee(Direction dir1, Direction dir2) {
        if (dir1 == null || dir2 == null) return false;
        return (dir1 == Direction.HAUT && dir2 == Direction.BAS) ||
               (dir1 == Direction.BAS && dir2 == Direction.HAUT) ||
               (dir1 == Direction.GAUCHE && dir2 == Direction.DROITE) ||
               (dir1 == Direction.DROITE && dir2 == Direction.GAUCHE);
    }
    
    @Override
    public String getNom() {
        return "SOS Stratégie";
    }
}
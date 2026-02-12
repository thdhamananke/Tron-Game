package model;

import java.util.*;

public class ParanoidStrategie extends AbstractStrategie {
    private final List<Player> joueurs;

    public ParanoidStrategie(Heuristic heuristic, List<Player> joueurs, int depth) {
        super(heuristic, depth);
        this.joueurs = joueurs;
    }

    @Override
    public Direction calculerMouvement(Player me, Plateau plateau) {

        double bestValue = Double.MIN_VALUE;
        Direction bestDir = null;

        double alpha = Double.MIN_VALUE;
        double beta = Double.MAX_VALUE;

        for (Direction dir : plateau.getCoupsPossibles(me.getPosition())) {

            MoveBackup backup = applyMove(plateau, me, dir);
            double value = paranoid(plateau, me, depth-1, alpha, beta , true);
            undoMove(plateau, me, backup);

            if (value > bestValue) {
                bestValue = value;
                bestDir = dir;
            }

            alpha = Math.max(alpha, bestValue);
        }

        return bestDir;
    }
    private double  paranoid(Plateau plateau , Player me , int depth , double alpha , double beta , boolean maximiserJoueur){
       
       // double value =0;
        if(depth == 0 || !(me.isAlive())){
            return heuristic.evaluate(plateau, me);
        }
        if(maximiserJoueur){

             double maxVal = Double.MIN_VALUE;
            for(Direction dir : plateau.getCoupsPossibles(me.getPosition())){
                MoveBackup backup = applyMove(plateau, me, dir);
               double val = paranoid(plateau, me, depth-1, alpha, beta, false);
               undoMove(plateau, me, backup);
               maxVal = Math.max(maxVal , val);
               alpha= Math.max(alpha , maxVal);
               if(beta<=alpha){
                break;
               }
            }
            return maxVal;

        }else{

            for (Player player : joueurs) {
                if(player!=me && player.isAlive()){
                  
            
            double minVal =Double.MAX_VALUE;
            for (Direction dir : plateau.getCoupsPossibles(me.getPosition()))  {
                MoveBackup backup = applyMove(plateau, me, dir);
                double val = paranoid(plateau, me, depth-1, alpha, beta, maximiserJoueur);
                undoMove(plateau, me, backup);
                minVal = Math.min(val , minVal);
                beta= Math.min(beta , minVal);
                if(beta<= alpha ){
                    break;
                }

            }
            return minVal;
        }

        }
            }
        return 0;     
    }

    @Override
    public String getName() {
        return "Stratégie Paranoid";
    }
}

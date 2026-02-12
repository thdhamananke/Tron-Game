package model;

import java.util.List;

// SOS ne fait jamais min
// chaque joueur maximise son utilité perçue
// les feuilles retournent un vecteur
// la matrice sociale est appliquée uniquement aux feuilles
// la comparaison se fait sur l’ID du joueur courant





public class SOSStrategie  extends AbstractStrategie {
    private double[] vecteur;
        private final List<Player> joueurs;


    protected SOSStrategie(Heuristic heuristic, int depth , List<Player> joueurs) {
        super(heuristic, depth);
        this.joueurs=joueurs;
        
        
    }

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {

        double bestScore = Double.MIN_VALUE;
        Direction bestCoup = Direction.NONE;
         int idRacine =joueurs.indexOf(player);
        for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {
            
        
        MoveBackup backup = applyMove(plateau, player, bestCoup);
     
        vecteur = sos( plateau ,  depth-1  , nextPlayer(player));
         undoMove(plateau, player, backup);

         if(vecteur[idRacine ] > bestScore){
            bestScore = vecteur[idRacine];
            bestCoup = dir;
         }
        }
        return bestCoup;


    }

    private double[] sos(Plateau plateau , int depth ,Player player ){
        if(depth == 0  || plateau.getCoupsPossibles(player.getPosition()).isEmpty() ){
            double evaluation =  heuristic.evaluate(plateau, player);
            return utiliteSociale(evaluation);
        }
        double  id = joueurs.indexOf(player);
        double bestScore = Double.MIN_NORMAL;
        double[] bestVecteur = null;
        
        for (Direction  dir : plateau.getCoupsPossibles(player.getPosition())) {
            MoveBackup backup = applyMove(plateau, player, dir);
            vecteur = sos(plateau, depth-1, nextPlayer(player));
            undoMove(plateau, player, backup);

            if(vecteur[(int) id] > bestScore){
                bestScore = vecteur[(int) id];
                bestVecteur= vecteur;
            }
        }
        return bestVecteur;
    }
    private  double[] utiliteSociale(double evaluation){
    double[] u = new double[joueurs.size()];
    for (int i = 0; i < joueurs.size(); i++) {
        u[i] = evaluation;
    }
    return u;

    }

    
    
    private Player nextPlayer(Player player){
        int i = joueurs.indexOf(player);
        return  joueurs.get(i+1) ;
         
     

    }


    @Override
    public String getName() {
        return "SOS Stratégie";
    }
    
}


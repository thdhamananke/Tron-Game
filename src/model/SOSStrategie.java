package model;

import java.util.List;

public class SOSStrategie extends AbstractStrategie {

    private final List<Player> joueurs;

    public SOSStrategie(Heuristic heuristic, int depth, List<Player> joueurs) {
        super(heuristic, depth);
        this.joueurs = joueurs;
    }

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {

        double bestScore = Double.NEGATIVE_INFINITY;
        Direction bestCoup = Direction.NONE;
        int idRacine = joueurs.indexOf(player);

        for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {

          //  MoveBackup backup = applyMove(plateau, player, dir);
            Plateau plateauCopie = plateau.copierPlateau();

            Position oldPos = player.getPosition();
            Position newPos = oldPos.move(dir);

            plateauCopie.placerMur(oldPos, player);
            Player playerCopie = new Player(player.getName(), player.getTeam(), newPos);
            plateauCopie.placerJoueur(newPos, playerCopie);

            // sos pour next joueur
            double[] vecteur = sos(plateauCopie, depth - 1, nextPlayer(playerCopie));

      //      undoMove(plateau, player, backup);

            if (vecteur[idRacine] > bestScore) {
                bestScore = vecteur[idRacine];
                bestCoup = dir;
            }
        }

        return bestCoup;
    }

    public double[] sos(Plateau plateau, int depth, Player player) {
        //  feuille
        if (depth == 0 || plateau.getCoupsPossibles(player.getPosition()).isEmpty()) {
            double evaluation = heuristic.evaluate(plateau, player);
            return utiliteSociale(evaluation);
        }

        int id = joueurs.indexOf(player);
        double bestScore = Double.NEGATIVE_INFINITY;
        double[] bestVecteur = utiliteSociale(Double.NEGATIVE_INFINITY);

        for (Direction dir : plateau.getCoupsPossibles(player.getPosition())) {
       //     MoveBackup backup = applyMove(plateau, player, dir);
           
            Plateau plateauCopie = plateau.copierPlateau();

            Position oldPos = player.getPosition();
            Position newPos = oldPos.move(dir);

            plateauCopie.placerMur(oldPos, player);
            Player playerCopie = new Player(player.getName(), player.getTeam(), newPos);
            plateauCopie.placerJoueur(newPos, playerCopie);
            double[] currentVecteur = sos(plateauCopie, depth - 1, nextPlayer(playerCopie));

         //   undoMove(plateau, player, backup);

            if (currentVecteur[id] > bestScore) {
                bestScore = currentVecteur[id];
                bestVecteur = currentVecteur;
            }
        }

        return bestVecteur;
    }

    public double[] utiliteSociale(double evaluation) {
        double[] u = new double[joueurs.size()];
        for (int i = 0; i < joueurs.size(); i++) {
            u[i] = evaluation;
        }
        return u;
    }

    public Player nextPlayer(Player player) {
        int i = joueurs.indexOf(player);
        return joueurs.get((i + 1) % joueurs.size());
    }

    @Override
    public String getName() {
        return "SOS Stratégie";
    }
}


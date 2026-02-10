package model;

public abstract class AbstractStrategie implements Strategie {

    protected final Heuristic heuristic;
    protected int  depth;

    protected AbstractStrategie(Heuristic heuristic ,int depth ) {
        this.depth=depth;
        this.heuristic = heuristic;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public Heuristic getHeuristic() {
        return heuristic;
    }
  
    protected static class MoveBackup {
        protected Position oldPosition;
        Cellule oldCell;
        Cellule newCell;
    }

    protected MoveBackup applyMove(Plateau plateau, Player player, Direction dir) {
        Position oldPos = player.getPosition();
        Position newPos = oldPos.move(dir);

        //  Si on sort du plateau, on s'arrête tout de suite
        if (!plateau.estDansPlateau(newPos) || !plateau.estLibre(newPos)) {
            return null; 
        }

        MoveBackup backup = new MoveBackup();
        backup.oldPosition = oldPos;

        // Sauvegarde simple : on ne crée pas de nouveaux objets Cellule, trop lourd
        // On mémorise juste l'état pour pouvoir le remettre
        backup.oldCell = new Cellule(plateau.getCellule(oldPos));
        backup.newCell = new Cellule(plateau.getCellule(newPos));

        // Simulation
        plateau.placerMur(oldPos, player);
        player.setPosition(newPos);
        plateau.placerJoueur(newPos, player);

        return backup;
    }

    protected void undoMove(Plateau plateau, Player player, MoveBackup backup) {
        if (backup == null) return;
        
        // On restaure l'état exact des deux cellules impactées
        plateau.setCellule(player.getPosition(), backup.newCell);
        plateau.setCellule(backup.oldPosition, backup.oldCell);
        player.setPosition(backup.oldPosition);
    }

}

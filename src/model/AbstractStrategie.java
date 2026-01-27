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
    public Heuristic getHeuristic() {
        return heuristic;
    }

  
    protected static class MoveBackup {
        protected Position oldPosition;
    }

    protected MoveBackup applyMove(Plateau plateau, Player player, Direction dir) {
        MoveBackup backup = new MoveBackup();
        Position oldPos = player.getPosition();
        Position newPos = oldPos.move(dir);

        backup.oldPosition = oldPos;

        plateau.placerMur(oldPos, player);
        player.setPosition(newPos);
        plateau.placerJoueur(newPos, player);

        return backup;
    }

    protected void undoMove(Plateau plateau, Player player, MoveBackup backup) {
        plateau.viderCellule(player.getPosition());
        player.setPosition(backup.oldPosition);
        plateau.viderCellule(backup.oldPosition);
        plateau.placerJoueur(backup.oldPosition, player);
    }
   
}

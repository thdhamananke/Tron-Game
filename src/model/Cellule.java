package model;
public class Cellule {
    private Position position;
    private Player owner ;

    /**
     * @param position  la position de la cellule
    */
    public Cellule(Position position) {
        this.position = position;
        this.owner = null;
    }

    /** obtenir la position de la cellule */
    public Position getPosition() {
        return position;
    }
    
    public boolean isEmpty() {
        return owner == null;
    }
    
    public void occupy(Player player) {
        this.owner = player;
    }
    
    /** le joueur */
    public Player getOwner() {
        return owner;
    }
    
    /** l'etat de la cellule */
    public CellState getState() {
        return owner == null ? CellState.EMPTY : CellState.WALL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cellule other)) return false;
        return position.equals(other.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}

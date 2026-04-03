package model;

public class Cellule {
    private Position position;
    private CellState state;
    private Player owner;
    private boolean isObstacle = false; 

    public Cellule(Position position) {
        this.position = position;
        this.state = CellState.EMPTY;
        this.owner = null;
    }

    // Constructeur de copie (important pour l'IA)
    public Cellule(Cellule other) {
        this.position = new Position(other.position.getRow(), other.position.getCol());
        this.state = other.state;
        this.owner = other.owner;
        this.isObstacle = other.isObstacle;
    }

    public Player getOwner() {
        return owner;
    }

    
    public boolean isEmpty() {
        // Une cellule est libre seulement si elle n'a pas de proprio ET n'est pas un obstacle
        return owner == null && state == CellState.EMPTY && !isObstacle;
    }

    public boolean isObstacle() { return isObstacle; }
    public void setObstacle(boolean obstacle) { this.isObstacle = obstacle; }

    public void occupy(Player player) {
        this.owner = player;
        this.state = CellState.WALL;
    }

    public CellState getState() {
        if (isObstacle) return CellState.WALL;
        if (owner != null) return CellState.WALL;
        return state;
    }
    
    
    public void setState(CellState state) {
        this.state = state; 
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

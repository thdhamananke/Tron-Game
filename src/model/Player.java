package model;

public class Player {
    private final String name;
    private final Team team;
    private Position position;
    private boolean alive;

    /**
     * Constructeur de player
     * @param name  nom du player
     * @param team  l'equipe dans la quelle il appartient
     * @param posi  la position du player
    */
    public Player(String name, Team team, Position posi) {
        this.name = name;
        this.team = team;
        this.position = posi;
        this.alive = true;
    }

    /** les getters */
    public String getName() {
        return this.name;
    }
    
    public Position getPosition() {
        return position;
    }

    public Team getTeam() {
        return team;
    }

    public Color getColor() {
        return team.getColor();
    }

    public boolean isAlive() {
        return alive;
    }

    /** les setteurs */
    public void setPosition(Position position) {
        this.position = position;
    }

    public void die() {
        this.alive = false;
    }
}

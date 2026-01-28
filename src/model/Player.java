package model;

public class Player {
    private final String name;
    private final Team team;
    private Position position;
    private Strategie strategie;
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

    public java.awt.Color getAwtColor() {
        Color color = getColor(); // ton enum model.Color

        return switch (color) {
            case RED -> java.awt.Color.RED;
            case BLUE -> java.awt.Color.BLUE;
            case GREEN -> java.awt.Color.GREEN;
            case YELLOW -> java.awt.Color.YELLOW;
            case MAGENTA -> java.awt.Color.MAGENTA;
            case CYAN -> java.awt.Color.CYAN;

            // “Bright” personnalisées
            case BRIGHT_RED -> new java.awt.Color(255, 50, 50);
            case BRIGHT_GREEN -> new java.awt.Color(50, 255, 50);
            case BRIGHT_YELLOW -> new java.awt.Color(255, 255, 100);
            case BRIGHT_BLUE -> new java.awt.Color(50, 50, 255);

            default -> java.awt.Color.BLACK; // sécurité
        };
    }



    /** les setteurs */
    public void setPosition(Position position) {
        this.position = position;
    }

    public void die() {
        this.alive = false;
    }
}

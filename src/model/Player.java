package model;

public class Player {
    private final String name;
    private final Team team;
    private Position position;
    private boolean alive;
    private Heuristic heuristic;
    private Strategie strategie;

    /**
     *  volatile : permet de garantit que la valeur lue est toujours la version 
     *  la plus récente écrite par un autre thread
    */
    private volatile Direction dernierCoupCal;


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

    public Strategie getStrategie() {
        return strategie;
    }

    public Heuristic getHeuristic() { 
        return heuristic; 
    }

    /** les setteurs */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void die() {
        this.alive = false;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }


    /**
     * On crée un thread qui va faire le calcul séparement
     * @param plateauCopie le plateau copier
    */
    public void lancerReflexion(Plateau plateauCopie) {
        this.dernierCoupCal = null; 

        new Thread(() -> {
            try {
                if (this.strategie != null && this.alive) {
                    this.dernierCoupCal = this.strategie.calculerMouvement(this, plateauCopie);
                }
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }, "Thread-Joueur-" + name).start();
    }

    public Direction getdernierCoupCal() {
        return dernierCoupCal;
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
}

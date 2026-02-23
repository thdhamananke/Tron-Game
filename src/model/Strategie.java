package model;

public interface Strategie {
    public Direction calculerMouvement(Player player , Plateau plateau);
    public String getName();
    public Heuristic getHeuristic();
}

package model;

import java.util.*;

public interface Strategie {
    public Direction calculerMouvement(Player player , Plateau plateau);
    public String getName();
    public Heuristic getHeuristic();
}

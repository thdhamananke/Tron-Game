package model;
import java.util.Random;

public class RandomStrategie  extends  AbstractStrategie{
    Random random = new Random();
    
    public RandomStrategie(Heuristic heuristic, int depth) {
        super(heuristic, depth);
    }


    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        int val = random.nextInt(4);
        return Direction.values()[val];
       
    }

    @Override
    public String getName() {
        return " Stratégie Aléatoire";
    }
    
}
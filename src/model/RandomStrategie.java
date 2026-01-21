package model;
import java.util.Random;
import java.util.random.*;

public class RandomStrategie  implements  Strategie{
    Random random = new Random();

    @Override
    public Direction calculerMouvement(Player player, Plateau plateau) {
        int val = random.nextInt(4);
        return Direction.values()[val];
       
    }

    @Override
    public String getNom() {
        return " Stratégie Aléatoire";
    }
    
}
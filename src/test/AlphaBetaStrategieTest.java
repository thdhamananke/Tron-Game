package test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import model.VoronoiHeuristic;
import model.AlphaBetaStrategie;
import model.Color;
import model.Heuristic;
import model.*;

public class AlphaBetaStrategieTest {
    
    @Test
    public void testsDirectionNonNullAvecheuristic(){
        Heuristic heuristic = new VoronoiHeuristic();
        AlphaBetaStrategie strategie = new AlphaBetaStrategie(heuristic , 5);

        Plateau  plateau = new Plateau(1,1);
        Team team = new Team("G1" , new ArrayList<>() , Color.BLUE);
        Player joueur = new Player("LAm", team,new Position(0,0));
        plateau.placerJoueur(joueur.getPosition(), joueur);
        Direction dir = strategie.calculerMouvement(joueur, plateau);

        assertTrue("La direction ne doit pas etre null" , dir != null);



    }
}

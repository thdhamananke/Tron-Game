package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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

     @Test
    public void testDirectionParmiCoupsPossibles() {
        Heuristic heuristic = new VoronoiHeuristic();
        AlphaBetaStrategie strategie = new AlphaBetaStrategie(heuristic, 3);

        Plateau plateau = new Plateau(4, 4);
        Team team = new Team("G1", new ArrayList<>(), Color.RED);
        Player joueur = new Player("Sila", team, new Position(2, 2));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);
        List<Direction> coupsPossibles = plateau.getCoupsPossibles(joueur.getPosition());

        assertTrue("La direction doit être parmi les coups possibles",
                coupsPossibles.contains(dir));
    }

    @Test
    public void testToujoursRetourneUneDirection() {
        Heuristic heuristic = new VoronoiHeuristic();
        AlphaBetaStrategie strategie = new AlphaBetaStrategie(heuristic, 2);

        Plateau plateau = new Plateau(5, 5);
        Team team = new Team("G1", new ArrayList<>(), Color.GREEN);
        Player joueur = new Player("Lydia", team, new Position(0, 0));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);

        assertTrue("La stratégie doit toujours retourner une direction valide", dir != null);
    }

    @Test
    public void testPlateauCoin() {
        Heuristic heuristic = new VoronoiHeuristic();
        AlphaBetaStrategie strategie = new AlphaBetaStrategie(heuristic, 2);

        Plateau plateau = new Plateau(3, 3);
        Team team = new Team("G1", new ArrayList<>(), Color.YELLOW);
        Player joueur = new Player("CornerPlayer", team, new Position(0, 0));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);

        List<Direction> coupsPossibles = plateau.getCoupsPossibles(joueur.getPosition());
        assertTrue("La direction doit être un coup possible même dans un coin",
                coupsPossibles.contains(dir));
    }
}

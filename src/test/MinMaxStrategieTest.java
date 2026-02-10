package test;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class MinMaxStrategieTest {

    @Test
    public void testDirectionNonNulleAvecHeuristic() {

        Heuristic heuristic = new VoronoiHeuristic();

        MinMaxStrategie strategie = new MinMaxStrategie(heuristic, 3);
        Plateau plateau = new Plateau(1,1);

        Team team = new Team("Team1", new ArrayList<>(), Color.RED);
        Player joueur = new Player("Lydia", team, new Position(2, 2));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);

        assertTrue("La direction ne doit pas être null", dir != null);
    }

    @Test
    public void testCoupsPossiblesAvecHeuristic() {
        Heuristic heuristic = new VoronoiHeuristic();

        MinMaxStrategie strategie = new MinMaxStrategie(heuristic, 2);
        Plateau plateau = new Plateau(5,5);

        Team team = new Team("Team1", new ArrayList<>(), Color.BLUE);
        Player joueur = new Player("Lamia", team, new Position(0, 0));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);
        List<Direction> possibles = plateau.getCoupsPossibles(joueur.getPosition());

        assertTrue("La direction doit être dans les coups possibles", possibles.contains(dir));
    }

    @Test
    public void testMinMaxRetourneToujoursUneDirection() {
        Heuristic heuristic = new VoronoiHeuristic();

        MinMaxStrategie strategie = new MinMaxStrategie(heuristic, 2);
        Plateau plateau = new Plateau(4,4);

        Team team = new Team("Team1", new ArrayList<>(), Color.GREEN);
        Player joueur = new Player("Sila", team, new Position(1, 1));
        plateau.placerJoueur(joueur.getPosition(), joueur);

        Direction dir = strategie.calculerMouvement(joueur, plateau);

        assertTrue("MinMax doit toujours retourner une direction valide", dir != null);
    }
}

package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.*;

public class ParanoidStrategieTest {

    @Test
    public void testDirectionNonNulle() {
        Heuristic heuristic = new FreeSpaceHeuristic();
        List<Player> joueurs = new ArrayList<>();

        Plateau plateau = new Plateau(3, 3);
        Team team = new Team("Equipe1", new ArrayList<>(), Color.BLUE);

        Player joueur1 = new Player("lam", team, new Position(1, 1));
        joueurs.add(joueur1);
        plateau.placerJoueur(joueur1.getPosition(), joueur1);

        ParanoidStrategie strategie = new ParanoidStrategie(heuristic, 3);

        Direction dir = strategie.calculerMouvement(joueur1, plateau);
        assertTrue("la direction n'est jamais null", dir != null);
    }

    @Test
    public void testDirectionParmiCoupsPossibles() {
        Heuristic heuristic = new VoronoiHeuristic();
        List<Player> joueurs = new ArrayList<>();

        Plateau plateau = new Plateau(4, 4);
        Team team = new Team("Equipe1", new ArrayList<>(), Color.RED);

        Player joueur1 = new Player("meli", team, new Position(2, 2));
        Player joueur2 = new Player("fat", team, new Position(0, 0));
        joueurs.add(joueur1);
        joueurs.add(joueur2);
        plateau.placerJoueur(joueur1.getPosition(), joueur1);
        plateau.placerJoueur(joueur2.getPosition(), joueur2);
        List<Direction> coupsPossibles = plateau.getCoupsPossibles(joueur1.getPosition());
        ParanoidStrategie strategie = new ParanoidStrategie(heuristic, 2);
     
        Direction dir = strategie.calculerMouvement(joueur1, plateau);

        assertTrue("la diretion tjrs dans les coups possibles",coupsPossibles.contains(dir));
    }

    @Test
    public void testPlateauCoin() {
        Heuristic heuristic = new TreeOfChambersHeuristic();
        List<Player> joueurs = new ArrayList<>();

        Plateau plateau = new Plateau(2, 2);
        Team team = new Team("Equipe1", new ArrayList<>(), Color.GREEN);

        Player joueur1 = new Player("moi", team, new Position(0, 0));
        Player joueur2 = new Player("lui", team, new Position(1, 0));
        joueurs.add(joueur1);
        joueurs.add(joueur2);

        plateau.placerJoueur(joueur1.getPosition(), joueur1);
        plateau.placerJoueur(joueur2.getPosition(), joueur2);

        ParanoidStrategie strategie = new ParanoidStrategie(heuristic, 2);

        Direction dir = strategie.calculerMouvement(joueur1, plateau);
        List<Direction> coupsPossibles = plateau.getCoupsPossibles(joueur1.getPosition());

        assertTrue(" si le joueur est dans un coin, la direction doit etre un coup possible",coupsPossibles.contains(dir));
    }
}

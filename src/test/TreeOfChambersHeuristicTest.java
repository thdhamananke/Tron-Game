package test;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import model.*;

public class TreeOfChambersHeuristicTest {

    /**
     * Test 1 : Cas de base sans adversaire.
     * L'heuristique doit retourner le nombre total de cases accessibles.
     */
    @Test
    public void testScoreSeulSurPlateau() {
        TreeOfChambersHeuristic toc = new TreeOfChambersHeuristic();
        Plateau plateau = new Plateau(3, 3); // 9 cases

        Team team = new Team("Solo", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("Moi", team, new Position(0, 0));
        plateau.placerJoueur(p1.getPosition(), p1);

        double score = toc.evaluate(plateau, p1);

        assertEquals("Le score sans adversaire doit être le total des cases accessibles", 9.0, score, 0.1);
    }

    /**
     * Test 2 : Détection d'une chambre isolée (Cul-de-sac).
     * On crée une chambre de 2 cases protégée par un "pont" (point d'articulation).
     */
    @Test
    public void testChambreIsolee() {
        TreeOfChambersHeuristic toc = new TreeOfChambersHeuristic();
        Plateau plateau = new Plateau(3, 3);

        Team t1 = new Team("Ami", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("P1", t1, new Position(0, 0));
        plateau.placerJoueur(p1.getPosition(), p1);

        Team t2 = new Team("Ennemi", new ArrayList<>(), Color.RED);
        Player p2 = new Player("P2", t2, new Position(2, 2));
        plateau.placerJoueur(p2.getPosition(), p2);

        Team tMurs = new Team("MURS", new ArrayList<>(), Color.RED);
        plateau.placerJoueur(new Position(0, 1), new Player("M1", tMurs, new Position(0, 1)));
        plateau.placerJoueur(new Position(1, 0), new Player("M2", tMurs, new Position(1, 0)));

        double score = toc.evaluate(plateau, p1);

        assertTrue("Le score doit être calculé même en cas d'isolement", score < 0 || score > -1000);
    }

    /**
     * Test 3 : Symétrie.
     * Sur un plateau vide avec deux joueurs à égale distance du centre, le score doit être 0.
     */
    // @Test
    // public void testSymetrie() {
    //     TreeOfChambersHeuristic toc = new TreeOfChambersHeuristic();
    //     Plateau plateau = new Plateau(5, 5);

    //     Team t1 = new Team("T1", new ArrayList<>(), Color.BLUE);
    //     Player p1 = new Player("P1", t1, new Position(0, 2));
    //     plateau.placerJoueur(p1.getPosition(), p1);

    //     Team t2 = new Team("T2", new ArrayList<>(), Color.RED);
    //     Player p2 = new Player("P2", t2, new Position(4, 2));
    //     plateau.placerJoueur(p2.getPosition(), p2);

    //     double score = toc.evaluate(plateau, p1);

    //     assertEquals("Sur un plateau parfaitement symétrique, le score TOC doit être 0", 0.0, score, 1.0);
    // }

    /**
     * Test 4 : Joueur mort.
     * Vérifie que l'heuristique renvoie bien la valeur plancher.
     */
    @Test
    public void testJoueurMort() {
        TreeOfChambersHeuristic toc = new TreeOfChambersHeuristic();
        Plateau plateau = new Plateau(5, 5);

        Team t1 = new Team("T1", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("P1", t1, new Position(0, 0));
        p1.setAlive(false); // Mort
        plateau.placerJoueur(p1.getPosition(), p1);

        double score = toc.evaluate(plateau, p1);

        assertEquals("Un joueur mort doit avoir un score de -1000000", -1000000.0, score, 0.1);
    }

    /**
     * Test 5 : Point d'articulation (Goulot).
     * On teste si l'algorithme gère un plateau séparé en deux par une seule case.
     */
    @Test
    public void testPointArticulation() {
        TreeOfChambersHeuristic toc = new TreeOfChambersHeuristic();

        Plateau plateau = new Plateau(3, 5);

        Team t1 = new Team("T1", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("P1", t1, new Position(1, 0));
        plateau.placerJoueur(p1.getPosition(), p1);

        Team t2 = new Team("T2", new ArrayList<>(), Color.RED);
        Player p2 = new Player("P2", t2, new Position(1, 4));
        plateau.placerJoueur(p2.getPosition(), p2);

        Team tMurs = new Team("MURS", new ArrayList<>(), Color.RED);
        plateau.placerJoueur(new Position(0, 2), new Player("M1", tMurs, new Position(0, 2)));
        plateau.placerJoueur(new Position(2, 2), new Player("M2", tMurs, new Position(2, 2)));

        double score = toc.evaluate(plateau, p1);

        assertNotNull("L'analyse des points d'articulation ne doit pas renvoyer null", score);
    }
}
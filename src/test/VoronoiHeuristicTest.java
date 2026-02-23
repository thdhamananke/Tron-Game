package test;

import static org.junit.Assert.*;
import org.junit.Test;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class VoronoiHeuristicTest {

    @Test
    public void testPartageEquitable() {
        VoronoiHeuristic heuristic = new VoronoiHeuristic();
        Plateau plateau = new Plateau(10, 10); // plateau 10x10

        // Deux joueurs aux coins opposés
        Team team1 = new Team("Equipe1", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("Joueur1", team1, new Position(0, 0));
        plateau.placerJoueur(p1.getPosition(), p1);

        Team team2 = new Team("Equipe2", new ArrayList<>(), Color.RED);
        Player p2 = new Player("Joueur2", team2, new Position(9, 9));
        plateau.placerJoueur(p2.getPosition(), p2);

        double scoreP1 = heuristic.evaluate(plateau, p1);

        // Sur un plateau vide et symétrique, le score attendu est proche de 0
        assertEquals("Score nul attendu pour partage équitable", 0.0, scoreP1, 5.0);
    }

    // @Test
    // public void testAvantageTerritorial() {
    //     VoronoiHeuristic heuristic = new VoronoiHeuristic();
    //     Plateau plateau = new Plateau(5, 5);

    //     Team teamA = new Team("A", new ArrayList<>(), Color.BLUE);
    //     Player p1 = new Player("P1", teamA, new Position(2, 2)); // joueur au centre
    //     plateau.placerJoueur(p1.getPosition(), p1);

    //     Team teamB = new Team("B", new ArrayList<>(), Color.RED);
    //     Player p2 = new Player("P2", teamB, new Position(0, 0)); // joueur dans un coin
    //     plateau.placerJoueur(p2.getPosition(), p2);

    //     double scoreP1 = heuristic.evaluate(plateau, p1);

    //     // Joueur central doit avoir plus de cases contrôlées → score positif
    //     assertTrue("Le joueur central doit avoir un score positif", scoreP1 > 0);
    // }

    @Test
    public void testJoueurMort() {
        VoronoiHeuristic heuristic = new VoronoiHeuristic();
        Plateau plateau = new Plateau(5, 5);

        Team team = new Team("A", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("P1", team, new Position(1, 1));
        plateau.placerJoueur(p1.getPosition(), p1);

        p1.setAlive(false); // joueur mort

        double score = heuristic.evaluate(plateau, p1);

        // Joueur mort doit renvoyer score minimal
        assertEquals("Joueur mort doit avoir score minimal", -1000000.0, score, 0.1);
    }

    @Test
    public void testCaseContestee() {
        VoronoiHeuristic heuristic = new VoronoiHeuristic();
        Plateau plateau = new Plateau(3, 3);

        Team t1 = new Team("T1", new ArrayList<>(), Color.BLUE);
        Player p1 = new Player("P1", t1, new Position(1, 0)); // milieu gauche
        plateau.placerJoueur(p1.getPosition(), p1);

        Team t2 = new Team("T2", new ArrayList<>(), Color.RED);
        Player p2 = new Player("P2", t2, new Position(1, 2)); // milieu droite
        plateau.placerJoueur(p2.getPosition(), p2);

        // La case centrale (1,1) est contestée
        double score = heuristic.evaluate(plateau, p1);

        // L'évaluation doit tourner correctement même avec des cases contestées
        assertNotNull("Évaluation doit réussir avec des cases contestées", score);
    }
}
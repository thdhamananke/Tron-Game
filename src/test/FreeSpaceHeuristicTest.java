package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import model.*;
import java.util.ArrayList;

public class FreeSpaceHeuristicTest {

    private Plateau plateau;
    private Player joueur;
    private FreeSpaceHeuristic heuristique;

    @Before
    public void setUp() {
        plateau = new Plateau(5, 5);
        Team equipe = new Team("Alpha", new ArrayList<>(), null);
        joueur = new Player("Testeur", equipe, new Position(2, 2));
        heuristique = new FreeSpaceHeuristic();
        
        plateau.placerJoueur(joueur.getPosition(), joueur);
    }

    @Test
    public void testPlateauVide() {
        double score = heuristique.evaluate(plateau, joueur);
        assertEquals("Un plateau vide 5x5 devrait donner 25 cases accessibles", 25.0, score, 0.0);
    }

    @Test
    public void testJoueurBloque() {
        plateau.placerMur(new Position(1, 2), null); // Haut
        plateau.placerMur(new Position(3, 2), null); // Bas
        plateau.placerMur(new Position(2, 1), null); // Gauche
        plateau.placerMur(new Position(2, 3), null); // Droite

        double score = heuristique.evaluate(plateau, joueur);
        assertEquals("Un joueur enfermé devrait avoir un score de 1", 1.0, score, 0.0);
    }

    @Test
    public void testJoueurMort() {
        joueur.setAlive(false);
        double score = heuristique.evaluate(plateau, joueur);
        assertTrue("Un joueur mort doit avoir un score très négatif", score <= -1000000.0);
    }

    @Test
    public void testPartitionPlateau() {
        for (int c = 0; c < 5; c++) {
            plateau.placerMur(new Position(3, c), null);
        }

        double score = heuristique.evaluate(plateau, joueur); 
        assertEquals("Le joueur devrait n'avoir accès qu'à sa zone (15 cases)", 15.0, score, 0.0);
    }
}
package test;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import model.*;

public class ModeleJeuIntegrationTest {

    @Test
    public void testScenarioComplet() {
        // Création des équipes et joueurs
        Team teamA = new Team("A", new ArrayList<>(), Color.BLUE);
        Team teamB = new Team("B", new ArrayList<>(), Color.RED);

        Player p1 = new Player("P1", teamA, new Position(0, 0));
        Player p2 = new Player("P2", teamB, new Position(2, 2));

        List<Player> joueurs = List.of(p1, p2);

        // Création du modèle de jeu 3x3
        ModeleJeu jeu = new ModeleJeu(3, 3, joueurs);
        jeu.demarrer();

        // Définition des directions plus lisibles
        Direction HAUT = Direction.HAUT;
        Direction BAS = Direction.BAS;
        Direction GAUCHE = Direction.GAUCHE;
        Direction DROITE = Direction.DROITE;

        // Tour 1
        // P1 descend P2 monte
        jeu.tourSuivant(List.of(BAS, HAUT));

        assertEquals(new Position(1,0), p1.getPosition());
        assertTrue(p1.isAlive());
        assertEquals(new Position(1,2), p2.getPosition());
        assertTrue(p2.isAlive());

        // Tour 2
        // P1 remonte  P2 va a gauche
        jeu.tourSuivant(List.of(HAUT, GAUCHE));

        assertEquals(new Position(0,0), p1.getPosition());
        assertTrue(p1.isAlive());
        assertEquals(new Position(1,1), p2.getPosition());
        assertTrue(p2.isAlive());

        // Tour 3
        // P1 va à droite, P2 monte → collision 
        jeu.tourSuivant(List.of(DROITE, HAUT));
        assertEquals(new Position(0,1), p1.getPosition());
        assertTrue(p1.isAlive());
        assertFalse(p2.isAlive());
        assertTrue(jeu.estTermine());
        assertEquals(teamA, jeu.getEquipeGagnante());
    }
}
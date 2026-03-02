package test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import model.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeleJeuTest {

    private ModeleJeu modele;
    private Player p1, p2;
    private Team teamA, teamB;

    @Before
    public void setUp() {
        teamA = new Team("Equipe1", new ArrayList<>(), Color.RED);
        teamB = new Team("Equipe2", new ArrayList<>(), Color.BLUE);
        
        p1 = new Player("P1", teamA, new Position(1, 1));
        p2 = new Player("P2", teamB, new Position(3, 3));
        
        List<Player> joueurs = new ArrayList<>(Arrays.asList(p1, p2));
        modele = new ModeleJeu(5, 5, joueurs);
    }

    @Test
    public void testDemarrerPlaceLesJoueurs() {
        modele.demarrer();
        
        assertTrue("Le jeu doit être en cours", modele.isJeuEnCours());
        
        assertFalse("La case (1,1) ne doit pas être libre", 
                     modele.getPlateau().estLibre(new Position(1, 1)));
        
        assertEquals(new Position(1, 1), p1.getPosition());
    }

    @Test
    public void testEliminationHorsPlateau() {
        modele.demarrer();
        
        // Tour 1 : P1 (1,1) -> (0,1) | P2 (3,3) -> (4,3)
        modele.tourSuivant(Arrays.asList(Direction.HAUT, Direction.BAS)); 
        
        // Tour 2 : P1 (0,1) -> Sortie (-1,1) | P2 (4,3) -> (4,4)
        modele.tourSuivant(Arrays.asList(Direction.HAUT, Direction.DROITE));

        assertFalse("P1 devrait être mort", p1.isAlive());
        assertTrue("Le jeu doit être terminé", modele.estTermine());
        
        // Vérification du gagnant (Team de P2)
        assertNotNull("Il doit y avoir un gagnant", modele.getEquipeGagnante());
        assertEquals("L'équipe gagnante doit être celle de P2", 
                     teamB.getName(), modele.getEquipeGagnante().getName());
    }

    @Test
    public void testMouvementValide() {
        modele.demarrer();
        
        modele.tourSuivant(Arrays.asList(Direction.BAS, Direction.HAUT));
        
        assertEquals("P1 doit être en (2,1)", new Position(2, 1), p1.getPosition());
        assertFalse("L'ancienne case (1,1) ne doit plus être libre", 
                     modele.getPlateau().estLibre(new Position(1, 1)));
    }
}
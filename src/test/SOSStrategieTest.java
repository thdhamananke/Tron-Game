package test;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class SOSStrategieTest {

    @Test
    public void testCalculerMouvementRetourneDirectionValide() {
        Heuristic heuristic = new VoronoiHeuristic();

        List<Player> joueurs = new ArrayList<>();
        Player joueur1 = new Player("Lamia", new Team("Team1", new ArrayList<>(), Color.RED), new Position(0, 0));
        Player joueur2 = new Player("Sila", new Team("Team2", new ArrayList<>(), Color.BLUE), new Position(4, 4));

        joueurs.add(joueur1);
        joueurs.add(joueur2);

        Plateau plateau = new Plateau(5, 5);
        plateau.placerJoueur(joueur1.getPosition(), joueur1);
        plateau.placerJoueur(joueur2.getPosition(), joueur2);

        SOSStrategie strategie = new SOSStrategie(heuristic, 2);

        Direction dir = strategie.calculerMouvement(joueur1, plateau);
        List<Direction> coupsPossibles = plateau.getCoupsPossibles(joueur1.getPosition());

        assertTrue("La direction doit être non null", dir != null);
        assertTrue("La direction doit être dans les coups possibles", coupsPossibles.contains(dir));
    }

    @Test
    public void testSOSMaximiseValeurJoueurCourant() throws Exception {
        Heuristic heuristic = new VoronoiHeuristic();

        List<Player> joueurs = new ArrayList<>();
        Player joueur1 = new Player("Lamia", new Team("Team1", new ArrayList<>(), Color.RED), new Position(0, 0));
        Player joueur2 = new Player("Sila", new Team("Team2", new ArrayList<>(), Color.BLUE), new Position(4, 4));
        Player joueur3 = new Player("Ines", new Team("Team3", new ArrayList<>(), Color.GREEN), new Position(2, 2));

        joueurs.add(joueur1);
        joueurs.add(joueur2);
        joueurs.add(joueur3);

        Plateau plateau = new Plateau(5, 5);
        plateau.placerJoueur(joueur1.getPosition(), joueur1);
        plateau.placerJoueur(joueur2.getPosition(), joueur2);
        plateau.placerJoueur(joueur3.getPosition(), joueur3);

        SOSStrategie strategie = new SOSStrategie(heuristic, 2);


        


        // Vérifie pour chaque joueur que sa valeur dans le vecteur est max
        for (Player p : joueurs) {
            double[] vecteur = strategie.sos(plateau, 2, joueur1);
            int id = joueurs.indexOf(p);
            double valeurJoueur = vecteur[id];

            for (int i = 0; i < vecteur.length; i++) {
                if (i != id) {
                    assertTrue("Le joueur courant doit maximiser sa valeur",
                            valeurJoueur >= vecteur[i]);
                }
            }
        }
    }

    @Test
    public void testCalculerMouvementPourTousLesJoueurs() {
        Heuristic heuristic = new VoronoiHeuristic();

        List<Player> joueurs = new ArrayList<>();
        Player joueur1 = new Player("Lamia", new Team("Team1", new ArrayList<>(), Color.RED), new Position(0, 0));
        Player joueur2 = new Player("Sila", new Team("Team2", new ArrayList<>(), Color.BLUE), new Position(4, 4));
        Player joueur3 = new Player("Ines", new Team("Team3", new ArrayList<>(), Color.GREEN), new Position(2, 2));

        joueurs.add(joueur1);
        joueurs.add(joueur2);
        joueurs.add(joueur3);

        Plateau plateau = new Plateau(5, 5);
        plateau.placerJoueur(joueur1.getPosition(), joueur1);
        plateau.placerJoueur(joueur2.getPosition(), joueur2);
        plateau.placerJoueur(joueur3.getPosition(), joueur3);

        Strategie strategie = new SOSStrategie(heuristic, 2);

        for (Player p : joueurs) {
            Direction dir = strategie.calculerMouvement(p, plateau);
            List<Direction> possibles = plateau.getCoupsPossibles(p.getPosition());
            assertTrue("La direction calculée doit être non null", dir != null);
            assertTrue("La direction doit être dans les coups possibles", possibles.contains(dir));
        }
    }
}

package model;

import java.util.*;
import observer.AbstractModeleEcoutable;

/**
 * Modèle (MVC) du jeu de Tron.
 * Gère l'état du plateau, les joueurs et les règles.
 */
public class ModeleJeu extends AbstractModeleEcoutable {

    private Plateau plateau;
    private final List<Player> joueurs;
    private int tour;
    private boolean jeuEnCours;

    private Set<Position> obstacles = new HashSet<>();

    /* ================= CONSTRUCTEUR ================= */

    public ModeleJeu(int nbLignes, int nbColonnes, List<Player> joueurs) {

        if (joueurs == null || joueurs.isEmpty()) {
            throw new IllegalArgumentException(
                "La liste des joueurs ne peut pas être vide."
            );
        }

        this.plateau = new Plateau(nbLignes, nbColonnes);
        this.joueurs = new ArrayList<>(joueurs);
        this.tour = 0;
        this.jeuEnCours = false;
    }

    public void restart(){
        this.plateau = new Plateau(plateau.getNbLignes(), plateau.getNbColonnes());
        obstacles = new HashSet<>();
        this.tour = 0;
        this.jeuEnCours = false;
    }

    /* ================= OBSTACLES ================= */

    public void ajouterObstacle(Position p) {
        obstacles.add(p);
        this.plateau.ajouterObstacle(p);
        notifier();
    }

    public void retirerObstacle(Position p) {
        obstacles.remove(p);

        this.plateau.retirerObstacle(p);
        notifier();
    }

    public void clearObstacles() {
        obstacles.clear();
        notifier();
    }

    public Set<Position> getObstacles() {
        return obstacles;
    }

    /* ================= DEMARRAGE ================= */

    public void demarrer() {

        this.plateau = new Plateau(
                plateau.getNbLignes(),
                plateau.getNbColonnes()
        );

        this.tour = 0;
        this.jeuEnCours = true;

        for (Player j : joueurs) {

            if (j.getPosition() == null)
                throw new IllegalStateException(
                        "Un joueur n'a pas de position initiale."
                );

            if (!plateau.estDansPlateau(j.getPosition()))
                throw new IllegalStateException(
                        "Position initiale hors plateau: " + j.getPosition()
                );

            if (!plateau.estLibre(j.getPosition()))
                throw new IllegalStateException(
                        "Deux joueurs partagent la même position initiale."
                );

            plateau.placerJoueur(j.getPosition(), j);
        }
        for(Position obs : obstacles){
            plateau.ajouterObstacle(obs);
        }

        notifier();
    }

    /* ================= TOUR ================= */

    public void tourSuivant(List<Direction> coups) {

        if (!jeuEnCours) return;

        if (coups == null || coups.size() != joueurs.size()) {
            throw new IllegalArgumentException(
                "La liste des coups doit correspondre au nombre de joueurs."
            );
        }

        for (int i = 0; i < joueurs.size(); i++) {

            Player joueur = joueurs.get(i);

            if (!joueur.isAlive()) continue;

            Direction dir = coups.get(i);
            appliquerDeplacement(joueur, dir);
        }

        tour++;

        if (estTermine()) {
            jeuEnCours = false;
        }

        notifier(); // 🔥 Notifie automatiquement la vue
    }

    /* ================= DEPLACEMENT ================= */

    private void appliquerDeplacement(Player joueur,Direction direction) {

        Position actuelle = joueur.getPosition();
        Position suivante = actuelle.move(direction);

        if (!plateau.estDansPlateau(suivante)
                || !plateau.estLibre(suivante)) {

            joueur.die();
            return;
        }

        plateau.placerMur(actuelle, joueur);

        joueur.setPosition(suivante);

        plateau.placerJoueur(suivante, joueur);
    }

    /* ================= ETAT ================= */

    public boolean estTermine() {

        List<Player> vivants = getJoueursVivants();

        if (vivants.size() <= 1) return true;

        Set<Team> equipesVivantes = new HashSet<>();

        for (Player p : vivants) {
            equipesVivantes.add(p.getTeam());
        }

        return equipesVivantes.size() <= 1;
    }

    public List<Player> getJoueursVivants() {

        List<Player> res = new ArrayList<>();

        for (Player p : joueurs) {
            if (p.isAlive()) res.add(p);
        }

        return res;
    }

    public Player getJoueurAt(Position pos) {

        for (Player p : joueurs) {
            if (p.getPosition().equals(pos)) {
                return p;
            }
        }
        return null;
    }

    
    /* ================= GETTERS ================= */
    public Plateau getPlateau() { return plateau; }
    public List<Player> getJoueurs() { return new ArrayList<>(joueurs); }
    public int getTour() { return tour; }
    public boolean isJeuEnCours() { return jeuEnCours; }

    public Team getEquipeGagnante() {

        if (!estTermine()) return null;

        List<Player> vivants = getJoueursVivants();

        if (vivants.isEmpty()) return null;

        return vivants.get(0).getTeam();
    }

    public void setPlateauSize(int rows, int cols) {
        this.plateau.setSize(rows, cols);
        notifier();
    }
}

package model;

import observer.*;

import java.util.*;

/**
 * Modèle (MVC) du jeu de Tron.
 * Contient l'état du jeu (plateau, joueurs, tour) et applique les règles.
 */
public class ModeleJeu extends AbstractModeleEcoutable
{
    private Plateau plateau;
    private final List<Player> joueurs;
    private int tour;
    private boolean jeuEnCours;

    /**
     * Construit un modèle de jeu avec une liste de joueurs déjà créée.
     *
     * @param nbLignes nombre de lignes du plateau
     * @param nbColonnes nombre de colonnes du plateau
     * @param joueurs liste des joueurs (avec position initiale non null)
     */
    public ModeleJeu(int nbLignes , int nbColonnes , List<Player> joueurs) {
        if (joueurs == null || joueurs.isEmpty()) {
            throw new IllegalArgumentException("La liste des joueurs ne peut pas être vide.");
        }
        this.plateau = new Plateau(nbLignes, nbColonnes);
        this.joueurs = new ArrayList<>(joueurs);
        this.tour = 0;
        this.jeuEnCours = false;
    }

    /**
     * Démarre une nouvelle partie : réinitialise le plateau et place les joueurs.
     */
    public void demarrer() 
    {
        this.plateau = new Plateau(plateau.getNbLignes(), plateau.getNbColonnes());
        this.tour = 0;
        this.jeuEnCours = true;
        for (Player j : joueurs) 
        {
            if (j.getPosition() == null) 
            {
                throw new IllegalStateException("Un joueur n'a pas de position initiale.");
            }
            if (!plateau.estDansPlateau(j.getPosition())) 
            {
                throw new IllegalStateException("Position initiale hors plateau: " + j.getPosition());
            }
            if (!plateau.estLibre(j.getPosition())) 
            {
                throw new IllegalStateException("Deux joueurs partagent la même position initiale: " + j.getPosition());
            }
            plateau.placerJoueur(j.getPosition(), j);

            // Remet vivant (si tu veux relancer après une partie)
            // (Ta classe Player n'a pas de setAlive, donc on suppose que tu recrées les Player sinon.)
        }
    }

    /**
     * Joue un tour complet : chaque joueur vivant effectue un déplacement.
     *
     * @param coups liste des directions choisies dans le même ordre que getJoueurs()
    */
    public void tourSuivant(List<Direction> coups) {
        if (!jeuEnCours) return;

        if (coups == null || coups.size() != joueurs.size()) {
            throw new IllegalArgumentException("La liste des coups doit correspondre au nombre de joueurs.");
        }

        // Tour séquentiel : on applique joueur par joueur
        for (int i = 0; i < joueurs.size(); i++) {
            // ici ca doit etre la listes des players vivants
            Player joueur = joueurs.get(i);
            if (!joueur.isAlive()) continue;

            Direction dir = coups.get(i);
            appliquerDeplacement(joueur, dir);
        }

        tour++;

        if (estTermine()) {
            jeuEnCours = false;
        }
        notifier();
    }

    /**
     * Applique le déplacement d'un joueur :
     * la case actuelle devient un mur
     * le joueur tente d'aller sur la nouvelle case
     * collision => mort
     * @param joueur joueur vivant
     * @param direction direction choisie
    */
    private void appliquerDeplacement(Player joueur, Direction direction) 
    {
        Position actuelle = joueur.getPosition();
        Position suivante = actuelle.move(direction);

        if (!plateau.estDansPlateau(suivante)) 
        {
            joueur.die();
            return;
        }

        if (!plateau.estLibre(suivante)) 
        {
            joueur.die();
            return;
        }
        plateau.placerMur(actuelle, joueur);
        joueur.setPosition(suivante);
        plateau.placerJoueur(suivante, joueur);
    }

    /**
     * Indique si la partie est terminée. En appliquant les règles, si une 
     * seule équipe a encore des joueurs vivants, ou un seul joueur vivant.
     * @return true si terminé et false sinon
    */
    public boolean estTermine() {
        List<Player> vivants = getJoueursVivants();
        if (vivants.size() <= 1) return true;

        Set<Team> equipesVivantes = new HashSet<>();
        for (Player p : vivants) {
            equipesVivantes.add(p.getTeam());
        }
        return equipesVivantes.size() <= 1;
    }

    /** @return plateau courant */
    public Plateau getPlateau() {
        return plateau;
    }

    public List<Player> getJoueurs() {
        return new ArrayList<>(joueurs);
    }

    public List<Player> getJoueursVivants() 
    {
        List<Player> res = new ArrayList<>();
        for (Player p : joueurs) {
            if (p.isAlive()) res.add(p);
        }
        return res;
    }

    public int getTour() 
    {
        return tour;
    }

    public boolean isJeuEnCours() 
    {
        return jeuEnCours;
    }

    /**
     * Retourne l'équipe gagnante si la partie est terminée et qu'il y a un gagnant.
     * @return équipe gagnante ou null si match nul / non terminé
    */
    public Team getEquipeGagnante() {
        if (!estTermine()) return null;

        List<Player> vivants = getJoueursVivants();
        if (vivants.isEmpty()) return null; // match nul : tout le monde est mort
        return vivants.get(0).getTeam();
    }

    // Dans ModeleJeu ou Plateau
    public Player getJoueurAt(Position pos) {
        for (Player p : joueurs) {
            if (p.getPosition().equals(pos)) {
                return p;
            }
        }
        return null;
    }


    public void setPlateauSize(int rows, int cols) {
        this.plateau.setSize(rows, cols);

    }


    /**
     * permet de gerer la réflexion parallèle et applique le tour.
     * Cette méthode fait le lien entre les Threads des joueurs et la logique du jeu.
    */
    public void executerTourAutomatique(int timeoutMs) {
        if (!jeuEnCours || estTermine()) return;

        // Lancer la réflexion en parallèle
        for (Player player : joueurs) {
            if (player.isAlive()) {
                player.lancerReflexion(this.plateau.copierPlateau());
            }
        }

        // Attendre la fin du temps alloué
        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Récupérer les décisions
        List<Direction> coupsDuTour = new ArrayList<>();
        for (Player player : joueurs) {
            if (player.isAlive()) {
                Direction dir = player.getdernierCoupCal();
                
                if (dir == null) {
                    // L'IA n'a pas répondu à temps donc éliminée
                    System.out.println("TIMEOUT : " + player.getName() + " n'a pas répondu. Direction HAUT par defaut");
                    coupsDuTour.add(Direction.HAUT);
                    // player.die(); 
                } else {
                    coupsDuTour.add(dir);
                }
            } else {
                coupsDuTour.add(Direction.HAUT);
            }
        }

        // Appliquer les mouvements physiquement
        this.tourSuivant(coupsDuTour);
    }

}

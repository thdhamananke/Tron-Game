package model;

import observer.*;
import java.util.*;

/**
 * Gère l'exécution asynchrone des tours de jeu.
 * Cette classe sépare la gestion des Threads de la logique métier de ModeleJeu.
 */
public class ModeleJeuThread extends AbstractModeleEcoutable {
    
    private final ModeleJeu modele;
    private final int timeoutMs;

    public ModeleJeuThread(ModeleJeu modele, int timeoutMs) {
        this.modele = modele;
        this.timeoutMs = timeoutMs;
    }

    /**
     * Exécute un tour complet en lançant les calculs d'IA en parallèle.
    */
    public void avancerTour() {
        if (!modele.isJeuEnCours() || modele.estTermine()) return;

        List<Player> joueurs = modele.getJoueurs();
        Plateau plateauActuel = modele.getPlateau();

        // Lancer la réflexion de chaque joueur vivant dans son propre Thread
        for (Player player : joueurs) {
            if (player.isAlive()) {
                player.lancerReflexion(plateauActuel.copierPlateau());
            }
        }

        // Attendre que les IA calculent (le temps du timeout)
        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Récupérer les décisions
        List<Direction> coupsDuTour = new ArrayList<>();
        for (Player player : joueurs) {
            if (player.isAlive()) {
                Direction dir = player.getDernierCoupCal();
                
                if (dir == null) {
                    // Si l'IA n'a pas répondu, on met une direction par défaut (ou on la tue)
                    System.out.println("TIMEOUT : " + player.getName() + " n'a pas répondu. Direction HAUT par defaut");
                    coupsDuTour.add(Direction.HAUT); 
                } else {
                    coupsDuTour.add(dir);
                }
            } else {
                coupsDuTour.add(Direction.HAUT);
            }
        }

        //  Appliquer les mouvements dans le modèle
        modele.tourSuivant(coupsDuTour);
        notifier();
    }

    // Délégation des méthodes utiles
    public ModeleJeu getModele() {
        return modele; 
    }

    public boolean estTermine() { 
        return modele.estTermine(); 
    }
    
    public Plateau getPlateau() { 
        return modele.getPlateau(); 
    }
}
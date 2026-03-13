package controller;

import model.*;
import view.GUI;

import javax.swing.*;
import java.util.List;

/**
 * Contrôleur principal - VERSION COMPLÈTE AVEC LOGS
 */
public class GameController {

    private ModeleJeu game;
    private ModeleJeuThread modeleThread;
    private GUI vue;
    private GameHistory history;

    private volatile boolean running = false;
    private volatile boolean paused = false;

    private int delay = 700;

    public GameController(ModeleJeu game, GUI vue) {
        this.game = game;
        this.vue = vue;
        this.history = new GameHistory();

        this.modeleThread = new ModeleJeuThread(game, delay);

        if (vue != null) {
            game.ajoutEcouteur(vue);
        }
        
        System.out.println("✓ Controller créé");
    }

    /* ================= DEMARRAGE ================= */

    public void demarrerJeu() {
        if (running) {
            System.out.println("⚠️ Le jeu est déjà en cours !");
            return;
        }

        running = true;
        paused = false;

        new Thread(() -> {
            try {
                game.demarrer();
                miseAJourVue();
                
                System.out.println("\n🎮 Jeu démarré avec " + game.getJoueurs().size() + " joueurs\n");
                afficherConfigJoueurs();

                int tourActuel = 0;
                while (!modeleThread.estTermine() && running) {
                    if (!paused) {
                        tourActuel++;
                        
                        // 🔥 LOG: Directions
                        System.out.println("\nTour " + tourActuel + ":");
                        logDirections();
                        
                        modeleThread.avancerTour();
                        miseAJourVue();
                    }
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                running = false;
                enregistrerPartie();
                miseAJourVue();
                System.out.println("\n🏁 Partie terminée après " + game.getTour() + " tours\n");
            }
        }, "GameLoop").start();
    }
    
    /**
     * 🔥 Affiche la config des joueurs au démarrage
     */
    private void afficherConfigJoueurs() {
        System.out.println("📋 Configuration des joueurs :");
        for (Player p : game.getJoueurs()) {
            int depth = (p.getStrategie() instanceof AbstractStrategie) ?
                       ((AbstractStrategie)p.getStrategie()).getDepth() : 0;
            
            System.out.println("  " + p.getTeam().getColor().getEmoji() + " " + 
                             p.getName() + ": " +
                             p.getStrategie().getName() + " + " +
                             p.getHeuristic().getName() + 
                             " (prof=" + depth + ") [Pos: " + p.getPosition() + "]");
        }
        System.out.println();
    }
    
    /**
     * 🔥 Affiche les directions choisies par chaque joueur
     */
    private void logDirections() {
        for (Player p : game.getJoueurs()) {
            if (p.isAlive()) {
                Direction dir = p.getDernierCoupCal();
                String dirStr = (dir != null) ? dir.toString() : "CALCUL EN COURS...";
                System.out.println("  " + p.getTeam().getColor().getEmoji() + " " + 
                                 p.getName() + " → " + dirStr);
            } else {
                System.out.println("  " + p.getTeam().getColor().getEmoji() + " " + 
                                 p.getName() + " ✗ MORT");
            }
        }
    }

    private void miseAJourVue() {
        if (vue != null) {
            SwingUtilities.invokeLater(vue::mettreAjourAffichage);
        }
    }

    /* ================= CONTROLES ================= */

    public void togglePause() {
        paused = !paused;
        System.out.println(paused ? "⏸ Pause" : "▶️ Reprise");
    }

    public void restart() {
        System.out.println("\n🔄 Redémarrage...");
        stopGame();

        // Attendre que le thread se termine
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        // Recréer le modèle avec les mêmes joueurs
        List<Player> joueurs = game.getJoueurs();
        
        // Réinitialiser les joueurs
        for (Player p : joueurs) {
            p.setAlive(true);
        }
        
        game = new ModeleJeu(game.getPlateau().getNbLignes(),
                             game.getPlateau().getNbColonnes(),
                             joueurs);
        modeleThread = new ModeleJeuThread(game, delay);
        
        if (vue != null) {
            game.ajoutEcouteur(vue);
            SwingUtilities.invokeLater(vue::mettreAjourAffichage);
        }
        
        System.out.println("✓ Jeu réinitialisé\n");
    }

    public void stopGame() {
        running = false;
        paused = false;
        System.out.println("⏹ Jeu arrêté");
    }

    public void changeGridSize(int rows, int cols) {
        if (running) stopGame();
        
        List<Player> joueurs = game.getJoueurs();
        game = new ModeleJeu(rows, cols, joueurs);
        modeleThread = new ModeleJeuThread(game, delay);
        
        if (vue != null) {
            game.ajoutEcouteur(vue);
        }
        
        System.out.println("✓ Taille du plateau changée: " + rows + "x" + cols);
    }

    /* ================= CONFIGURATION STRATEGIES ================= */

    /**
     * 🔥 NOUVELLE MÉTHODE: Permet de changer la stratégie d'un joueur
     */
    public void setPlayerStrategy(int playerIndex, String stratName, String heurName, int depth) {
        List<Player> joueurs = game.getJoueurs();
        
        if (playerIndex < 0 || playerIndex >= joueurs.size()) {
            System.err.println("❌ Index joueur invalide: " + playerIndex);
            return;
        }
        
        Player player = joueurs.get(playerIndex);
        Heuristic heur = creerHeuristique(heurName);
        Strategie strat = creerStrategie(stratName, heur, depth, joueurs);
        
        player.setStrategie(strat);
        player.setHeuristic(heur);
        
        System.out.println("✓ Stratégie mise à jour pour " + player.getName() + 
                         ": " + stratName + " + " + heurName + " (prof=" + depth + ")");
    }

    private Heuristic creerHeuristique(String nom) {
        return switch (nom) {
            case "FreeSpace" -> new FreeSpaceHeuristic();
            case "Voronoi" -> new VoronoiHeuristic();
            case "TreeOfChambers" -> new TreeOfChambersHeuristic();
            default -> new FreeSpaceHeuristic();
        };
    }

    private Strategie creerStrategie(String nom, Heuristic heur, int depth, List<Player> joueurs) {
        return switch (nom) {
            case "Random" -> new RandomStrategie(heur, depth);
            case "MinMax" -> new MinMaxStrategie(heur, depth);
            case "AlphaBeta" -> new AlphaBetaStrategie(heur, depth);
            case "MaxN" -> new MaxNStrategie(heur, depth);
            case "Paranoid" -> new ParanoidStrategie(heur, depth);
            case "SOS" -> new SOSStrategie(heur, depth, joueurs);
            default -> new AlphaBetaStrategie(heur, depth);
        };
    }

    /* ================= HISTORIQUE ================= */

    private void enregistrerPartie() {
        if (game != null && game.estTermine()) {
            StringBuilder stratRougeDesc = new StringBuilder();
            StringBuilder stratBleuDesc = new StringBuilder();
            List<Player> joueurs = game.getJoueurs();
            
            for (Player p : joueurs) {
                int depth = (p.getStrategie() instanceof AbstractStrategie) ?
                           ((AbstractStrategie)p.getStrategie()).getDepth() : 0;
                
                String desc = p.getStrategie().getName() + "+" + 
                             p.getHeuristic().getName() + "(" + depth + ") ";
                
                if (p.getTeam().getColor() == model.Color.RED || 
                    p.getTeam().getColor() == model.Color.BRIGHT_RED) {
                    stratRougeDesc.append(desc);
                } else {
                    stratBleuDesc.append(desc);
                }
            }

            history.ajouterPartie(
                    new GameRecord(
                            game.getTour(),
                            game.getEquipeGagnante(),
                            stratRougeDesc.toString(),
                            stratBleuDesc.toString(),
                            game.getPlateau().getNbLignes(),
                            game.getPlateau().getNbColonnes()
                    )
            );
            System.out.println("💾 Partie enregistrée dans l'historique");
        }
    }

    /* ================= GETTERS ================= */

    public ModeleJeu getGame() { return game; }
    public GameHistory getHistory() { return history; }
    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }
    public int getDelay() { return delay; }

    public void setDelay(int d) {
        this.delay = d;
        if (modeleThread != null) modeleThread.setTimeout(d);
    }

    public int getTour() {
        return game != null ? game.getTour() : 0;
    }

    public String getGameState() {
        if (game == null) return "Non initialisé";
        if (game.estTermine()) return "Terminé";
        if (getTour() == 0) return "Prêt";
        if (paused) return "En pause";
        return "En cours";
    }

    public String getWinner() {
        if (game == null || !game.estTermine()) return "-";
        if (game.getEquipeGagnante() == null) return "Match nul";
        return game.getEquipeGagnante().getName();
    }

    public void setVue(GUI vue) {
        this.vue = vue;
        if (vue != null && game != null) {
            game.ajoutEcouteur(vue);
        }
    }
}
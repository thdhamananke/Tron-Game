package controller;

import model.*;
import view.GUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur principal du jeu
 * Gère la logique, le thread, l'historique et l'état
 */
public class GameController {

    private ModeleJeu game;
    private ModeleJeuThread modeleThread;
    private GUI vue;
    private GameHistory history;

    private String strategieRouge = "AlphaBeta";
    private String strategieBleu = "AlphaBeta";

    private volatile boolean running = false;
    private volatile boolean paused = false;

    private int delay = 500;
    private int rows = 30;
    private int cols = 30;

    /* ================= CONSTRUCTEUR ================= */

    public GameController(ModeleJeu game, GUI vue) {
        this.game = game;
        this.vue = vue;
        this.history = new GameHistory();
    }

    /* ================= INITIALISATION ================= */

    public void initialiserGame() {

        if (vue != null) {
            rows = vue.getRows();
            cols = vue.getColumns();
        }

        Player rouge = new Player(
                "Bot Rouge",
                new Team("Rouge", new ArrayList<>(), model.Color.RED),
                new Position(2, 2)
        );

        Player bleu = new Player(
                "Bot Bleu",
                new Team("Bleu", new ArrayList<>(), model.Color.BLUE),
                new Position(rows - 3, cols - 3)
        );

        rouge.getTeam().getMembers().add(rouge);
        bleu.getTeam().getMembers().add(bleu);

        ArrayList<Player> joueurs = new ArrayList<>();
        joueurs.add(rouge);
        joueurs.add(bleu);

        rouge.setStrategie(creerStrategie(strategieRouge, joueurs));
        bleu.setStrategie(creerStrategie(strategieBleu, joueurs));

        game = new ModeleJeu(rows, cols, joueurs);
        modeleThread = new ModeleJeuThread(game, delay);

        if (vue != null) {
            game.ajoutEcouteur(vue);
            vue.mettreAjourAffichage();
        }
    }

    /* ================= DEMARRAGE ================= */

    public void demarrerJeu() {

        if (running) return;

        running = true;
        paused = false;

        new Thread(() -> {

            game.demarrer();

            while (!modeleThread.estTermine() && running) {

                if (!paused) {
                    modeleThread.avancerTour();
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            running = false;
            enregistrerPartie();

            if (vue != null) {
                vue.mettreAjourAffichage();
            }

        }).start();
    }

    /* ================= CONTROLES ================= */

    public void togglePause() {
        paused = !paused;
    }

    public void restart() {
        stopGame();
        initialiserGame();
    }

    public void stopGame() {
        running = false;
        paused = false;
    }

    public void changeGridSize(int r, int c) {
        if (running) stopGame();
        this.rows = r;
        this.cols = c;
        initialiserGame();
    }

    /* ================= STRATEGIES ================= */

    private Strategie creerStrategie(String nom, List<Player> players) {

        return switch (nom) {

            case "Minimax" ->
                    new MinMaxStrategie(new FreeSpaceHeuristic(), 5);

            case "AlphaBeta" ->
                    new AlphaBetaStrategie(new FreeSpaceHeuristic(), 5);

            case "MaxN" ->
                    new MaxNStrategie(new FreeSpaceHeuristic(), 5);

            case "Paranoid" ->
                    new ParanoidStrategie(new FreeSpaceHeuristic(), 5);

            default ->
                    new RandomStrategie(new FreeSpaceHeuristic(), 5);
        };
    }

    public void appliquerStrategieComplete(
            String stratRouge, String heurRouge, int depthRouge,
            String stratBleu, String heurBleu, int depthBleu) {

        Heuristic hR = creerHeuristique(heurRouge);
        Heuristic hB = creerHeuristique(heurBleu);

        Player rouge = game.getJoueurs().get(0);
        Player bleu = game.getJoueurs().get(1);

        rouge.setStrategie(creerStrategie(stratRouge, hR, depthRouge));
        bleu.setStrategie(creerStrategie(stratBleu, hB, depthBleu));
    }

    private Heuristic creerHeuristique(String nom) {
        return switch (nom) {
            case "Advanced" -> new VoronoiHeuristic(); // sécurité
            default -> new FreeSpaceHeuristic();
        };
    }

    private Strategie creerStrategie(
            String nom, Heuristic heur, int depth) {

        return switch (nom) {
            case "AlphaBeta" -> new AlphaBetaStrategie(heur, depth);
            case "MinMax" -> new MinMaxStrategie(heur, depth);
            default -> new RandomStrategie(heur, depth);
        };
    }

    /* ================= HISTORIQUE ================= */

    public void enregistrerPartie() {

        if (game != null && game.estTermine()) {

            history.ajouterPartie(
                    new GameRecord(
                            game.getTour(),
                            game.getEquipeGagnante(),
                            strategieRouge,
                            strategieBleu,
                            rows,
                            cols
                    )
            );
        }
    }

    /* ================= GETTERS ================= */

    public ModeleJeu getGame() {
        return game;
    }

    public GameHistory getHistory() {
        return history;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean b) {
        running = b;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int d) {
        delay = d;
    }

    public int getTour() {
        return game != null ? game.getTour() : 0;
    }

    public String getGameState() {

        if (game == null) return "Non initialisé";
        if (game.estTermine()) return "Terminé";
        if (getTour() == 0) return "Prêt";
        return "En cours";
    }

    public String getWinner() {

        if (game == null || !game.estTermine()) return "-";
        if (game.getEquipeGagnante() == null) return "Match nul";
        return game.getEquipeGagnante().getName();
    }

    public void setVue(GUI vue) {
        this.vue = vue;
    }
}
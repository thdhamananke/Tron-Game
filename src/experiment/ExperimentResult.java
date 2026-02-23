package experiment;

import model.Player;
import model.Team;
import java.util.*;

public class ExperimentResult {

    private int games = 0;
    private long totalTime = 0;
    private int totalTurns = 0;
    private final Map<Team, Integer> wins = new HashMap<>();
    private List<Player> players;
    private List<GameResult> history = new ArrayList<>();

    /**
     * Cette méthode est synchronized car elle est appelée par 
     * tous les threads du pool à chaque fin de partie.
    */
    public synchronized void record(GameResult game) {

        if (this.players == null) {
            this.players = game.getJoueurs();
        }
        history.add(game);
        this.games++;
        this.totalTime += game.getTimeMs();
        this.totalTurns += game.getTurns();

        if (game.getWinner() != null) {
            int currentWins = wins.getOrDefault(game.getWinner(), 0);
            wins.put(game.getWinner(), currentWins + 1);
        }
    }

    // On synchronise aussi le setter
    public synchronized void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<GameResult> getHistory() {
        return history;
    }

    public List<Player> getPlayers() {
        return players;
    }

    // Statistiques globales
    public int getNbGames() {
        return games;
    }

    public long getTotalTimeMs() {
        return totalTime;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public double getAverageTimeMs() {
        return games == 0 ? 0 : totalTime / (double) games;
    }

    public double getAverageTurns() {
        return games == 0 ? 0 : totalTurns / (double) games;
    }

    // Statistiques par équipe
    public int getWins(Team team) {
        return wins.getOrDefault(team, 0);
    }

    public double getWinRate(Team team) {
        if (games == 0) return 0;
        return (getWins(team) / (double) games) * 100;
    }

    public Map<Team, Integer> getWinsPerTeam() {
        return Collections.unmodifiableMap(wins);
    }

    // calcul de la confience.
    public double getConfidence(Team team, double confidenceLevel) {
        if (games == 0) return 0;

        double p = getWins(team) / (double) games;
        return confidenceLevel * Math.sqrt((p * (1 - p)) / games) * 100;
    }

    // Ajoute cette classe interne
    public static class ResumeSession {
        public final int nbParties;
        public final Map<Team, Double> winRates;
        public final double confidence;

        public ResumeSession(int nb, Map<Team, Double> rates, double conf) {
            this.nbParties = nb;
            this.winRates = rates;
            this.confidence = conf;
        }
    }

    // Dans la classe ExperimentResult, ajoute la liste et la méthode :
    private final List<ResumeSession> resumesSession = new ArrayList<>();

    public void recordSession(int nb, Map<Team, Double> rates, double conf) {
        this.resumesSession.add(new ResumeSession(nb, rates, conf));
    }

    public List<ResumeSession> getResumesSession() {
        return resumesSession;
    }
}

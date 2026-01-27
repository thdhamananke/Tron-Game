package experiment;

import java.util.*;

import model.*;

/**
 * Stocke les résultats d'une série de parties pour une configuration donnée.
 * Tout est en millisecondes.
*/
public class ExperimentResult {

    private int numero;  
    private final Map<Team, Integer> winsPerTeam;
    private long totalGameTimeMs; 
    private int totalTurns;

    public ExperimentResult() {
        this.winsPerTeam = new HashMap<>();
    }

    /**
     * Enregistre le résultat d'une partie.
     *
     * @param winningTeam équipe gagnante ou null si match nul
     * @param gameTimeMs durée de la partie en millisecondes
     * @param turns nombre de tours joués
    */
    public void recordGame(Team winningTeam, long gameTimeMs, int turns) {
        numero++;
        totalGameTimeMs += gameTimeMs;
        totalTurns += turns;

        if (winningTeam != null) {
            winsPerTeam.put(
                winningTeam,
                winsPerTeam.getOrDefault(winningTeam, 0) + 1
            );
        }
    }

    /** @return nombre total de parties jouées */
    public int getNumero() {
        return numero;
    }

    /** Retourne le taux de victoire d'une équipe. */
    public double getWinRate(Team team) {
        int wins = winsPerTeam.getOrDefault(team, 0);
        return numero == 0 ? 0.0 : (double) wins / numero;
    }

    /** @return durée moyenne d'une partie (ms) */
    public double getAverageTimeMs() {
        return numero == 0 ? 0.0 : (double) totalGameTimeMs / numero;
    }

    /** @return nombre moyen de tours par partie */
    public double getAverageTurns() {
        return numero == 0 ? 0.0 : (double) totalTurns / numero;
    }

    /** @return nombre de victoires par équipe */
    public Map<Team, Integer> getWinsPerTeam() {
        return Collections.unmodifiableMap(winsPerTeam);
    }
}

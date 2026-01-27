package experiment;

import model.*;

/**
 * Résultat d'une partie,donc c'est cette classe qui permet de
 * faire le transfert pour les expérimentations.
*/
public class GameResult {

    private final Team winningTeam;
    private final long gameTimeMs;
    private final int turns;

    /**
     * Construit le resultat d'une partie du jeu.
     * 
     * @param winningTeam   l'equipe gagnete
     * @param gameTimeMs  le temps du jeu en milliseconde
     * @param turns les tours effectués
    */
    public GameResult(Team winningTeam, long gameTimeMs, int turns) {
        this.winningTeam = winningTeam;
        this.gameTimeMs = gameTimeMs;
        this.turns = turns;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public long getGameTimeMs() {
        return gameTimeMs;
    }

    public int getTurns() {
        return turns;
    }
}

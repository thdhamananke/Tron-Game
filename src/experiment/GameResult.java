package experiment;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class GameResult {

    private final Team winner;
    private final long timeMs;
    private final int turns;
    private final List<Player> joueurs;

    public GameResult(Team winner, long timeMs, int turns, List<Player> joueurs) {
        this.winner = winner;
        this.timeMs = timeMs;
        this.turns = turns;
        this.joueurs = new ArrayList<>(joueurs);
    }

    public Team getWinner() { return winner; }
    public long getTimeMs() { return timeMs; }
    public int getTurns() { return turns; }
    public List<Player> getJoueurs() { return joueurs; }
}

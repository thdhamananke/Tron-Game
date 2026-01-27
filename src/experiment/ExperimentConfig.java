package experiment;

import java.util.List;

import model.Team;

public class ExperimentConfig {
    private final int gridSize;
    private final int teamSize;
    private final int depth;
    private final int nbGames;
    private final List<Team> teams; 


    public ExperimentConfig(int gridSize, int teamSize, int depth, int nbGames, List<Team> teams) {
        this.gridSize = gridSize;
        this.teamSize = teamSize;
        this.depth = depth;
        this.nbGames = nbGames;
        this.teams = teams;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getDepth() {
        return depth;
    }

    public int getNbGames() {
        return nbGames;
    }

    public List<Team> getTeams() {
        return teams;
    }
    
}

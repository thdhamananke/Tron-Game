package experiment;

import java.util.List;

import model.Strategie;

public class ExperimentConfig {
    public final int nbLignes;
    public final int nbColonnes;
    public final int nbEquipes;
    public final int joueurs;
    public final int depth;
    public final int nbGames;

    private List<Strategie> strategies;

    public ExperimentConfig(int nbLignes, int nbColonnes, int nbEquipes, int joueurs, 
                            int depth, int nbGames, List<Strategie> strategies) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.nbEquipes = nbEquipes;
        this.joueurs = joueurs;
        this.depth = depth;
        this.nbGames = nbGames;
        this.strategies = strategies;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    public int getNbEquipes() {
        return nbEquipes;
    }

    public int getJoueurs() {
        return joueurs;
    }

    public int getDepth() {
        return depth;
    }

    public int getNbGames() { 
        return nbGames; 
    }

    public List<Strategie> getStrategies() {
        return strategies;
    }

    
}

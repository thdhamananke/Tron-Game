package controller;

import model.*;
import model.Color;
import view.GUI;
import view.VueJeu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameController{

    private ModeleJeu game;
    private GUI vue;
    private String strategieRouge = "Random";
    private String strategieBleu  = "Random";
    public int delay = 100;

    public GameController(ModeleJeu Game , GUI Vue){
            this.game = Game;
            this.vue = Vue;

    }

    public void intiliserGame() {
        Player player1 = new Player(
                "bot01",
                new Team("One", null, Color.RED),
                new Position(0, 0)
        );

        Player player2 = new Player(
                "bot02",
                new Team("Two", null, Color.BLUE),
                new Position(14, 14)
        );

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        setGame(vue.getRows(), vue.getColumns(), players);
        vue.mettreAjourAffichage();
    }


    public void lunchgame () {
        //int number = 2;
        // number = Vue.getplayersnumber();
        // for(int i = 0 ; i < number ; i++){
        //    Game.set
        //}
        //Est ce que c mieux de crier les players dans le main u c mieux de les criéer ici dans cette méthode

        game.demarrer();
        Strategie strategie;
        while (game.isJeuEnCours()) {
            List<Direction> coups = new ArrayList<>();
            int i = 0;
            for (Player player : game.getJoueursVivants()) {

                String nomStrategie =
                        player.getTeam().getName().equals("One")
                                ? strategieRouge
                                : strategieBleu;

                strategie = creerStrategie(nomStrategie, game.getJoueurs());

                coups.add(strategie.calculerMouvement(player, game.getPlateau()));
                i++;
            }
            System.out.println(i);
            game.tourSuivant(coups);
            vue.mettreAjourAffichage();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if(game.getEquipeGagnante() == null){
            System.out.println("match null");
        }else{
            System.out.println("le team qui a ganger :"+game.getEquipeGagnante().getName());
        }
        vue.mettreAjourAffichage();
    }

    public void changeGridSize(int rows, int cols) {
        List<Player> joueurs = game.getJoueurs();

        ModeleJeu nouveauJeu = new ModeleJeu(rows, cols, new ArrayList<>(joueurs));
        this.game = nouveauJeu;

        vue.mettreAjourAffichage();
    }


    public void setVue(GUI vue) {
        this.vue = vue;
    }

    public ModeleJeu getGame(){
        return this.game;
    }


    private Strategie creerStrategie(String nom , List<Player> players) {
        return switch (nom) {
            case "Minimax"    -> new MinMaxStrategie(new FreeSpaceHeuristic(),5);
            case "AlphaBeta"  -> new AlphaBetaStrategie(new FreeSpaceHeuristic(),5);
            case "MaxN"       -> new MaxNStrategie(new FreeSpaceHeuristic(), 5);
            case "Paranoid"   -> new ParanoidStrategie(new FreeSpaceHeuristic(),players,5);
            //case "SOS"        -> new SOSStrategie(new FreeSpaceHeuristic(), 5);
            default           -> new RandomStrategie(new FreeSpaceHeuristic(),5);
        };
    }


    public void restart(){
        intiliserGame();
    }

    public void setGame(int rows , int cols , ArrayList<Player> list) {
        ModeleJeu newgame = new ModeleJeu(rows , cols , list);
        this.game = newgame;
    }

    public void appliquerStrategie(String rouge, String bleu) {
        this.strategieRouge = rouge;
        this.strategieBleu  = bleu;
    }

}
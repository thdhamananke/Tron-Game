package controller;

import model.*;
import view.VueJeu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameController{

    private ModeleJeu game;
    private VueJeu vue;
    public int delay;

    public GameController(ModeleJeu Game ,VueJeu Vue){
            this.game = Game;
            this.vue = Vue;

    }

    public void lunchgame (){
       //int number = 2;
        // number = Vue.getplayersnumber();
       // for(int i = 0 ; i < number ; i++){
         //    Game.set
        //}
        //Est ce que c mieux de crier les players dans le main u c mieux de les criéer ici dans cette méthode  
        game.demarrer();
        Strategie strategie;
        while(game.isJeuEnCours()){
            List<Direction> coups = new ArrayList<>();
            for(Player player :game.getJoueursVivants()){
                strategie = new RandomStrategie();
                coups.add(strategie.calculerMouvement(player,game.getPlateau()));

            }
            game.tourSuivant(coups);
        }
        if(game.getEquipeGagnante() == null){
            System.out.println("match null");
        }else{
            System.out.println("le team qui a ganger :"+game.getEquipeGagnante().getName());
        }
    }
    
}
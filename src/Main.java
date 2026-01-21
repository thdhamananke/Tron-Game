import view.GUI;
import view.VueJeu;

import javax.swing.*;

import controller.GameController;

import java.util.*;
import model.Color;
import model.ModeleJeu;
import model.Position;
import model.Team;
import model.Player;

public class Main {

    public static void main(String[] args)
    {
       // VueJeu gui = new GUI();
        
        Player player1 = new Player("bot01",new Team("One", null, Color.RED),new Position(0, 0)  );
        Player player2 = new Player("bot02",new Team("Two", null, Color.BLUE),new Position(14, 14)  );
        // public Team(String name, List<Player> members, Color color)
        List<Player> listedeplayers = new ArrayList<Player>();
        listedeplayers.add(player1);
        listedeplayers.add(player2);

        ModeleJeu  game =  new ModeleJeu(15, 15, listedeplayers);
        VueJeu view =  new GUI();
        GameController controller = new GameController(game, view);
        controller.lunchgame();
      //  SwingUtilities.invokeLater(() -> {
        //    new GUI();
        //});


    }

}

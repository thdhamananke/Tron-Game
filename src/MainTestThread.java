// import model.*;
// import controller.GameThread;

// import java.util.Arrays;
// import java.util.List;

// public class MainTestThread {
//     public static void main(String[] args) {
//         Player player1 = new Player("bot01", new Team("One", null, Color.RED), new Position(5, 5));
//         Player player2 = new Player("bot02", new Team("Two", null, Color.BLUE), new Position(14, 14));

//         // Assigner des stratégies
//         player1.setStrategie(new MinMaxStrategie(new AdvancedHeuristic(), 5));
//         player2.setStrategie(new AlphaBetaStrategie(new AdvancedHeuristic(), 5));

//         List<Player> joueurs = Arrays.asList(player1, player2);

//         ModeleJeu jeu = new ModeleJeu(30, 30, joueurs);
//      //   GameThread gameThread = new GameThread(jeu);
//         gameThread.start();
//     }
// }

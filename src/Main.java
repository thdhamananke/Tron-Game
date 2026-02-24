import view.GUI;
import controller.GameController;
import model.*;

import javax.swing.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

                System.out.println("=== Démarrage du Jeu Tron ===");

                /* ===================== JOUEURS ===================== */

                Player player1 = new Player(
                        "Bot Rouge",
                        new Team("Rouge", new ArrayList<>(), Color.RED),
                        new Position(2, 2)
                );

                Player player2 = new Player(
                        "Bot Bleu",
                        new Team("Bleu", new ArrayList<>(), Color.BLUE),
                        new Position(27, 27)
                );

                player1.getTeam().getMembers().add(player1);
                player2.getTeam().getMembers().add(player2);

                player1.setStrategie(
                        new AlphaBetaStrategie(
                                new FreeSpaceHeuristic(),
                                5
                        )
                );

                player2.setStrategie(
                        new AlphaBetaStrategie(
                                new FreeSpaceHeuristic(),
                                5
                        )
                );

                List<Player> joueurs = new ArrayList<>();
                joueurs.add(player1);
                joueurs.add(player2);

                /* ===================== MODELE ===================== */

                ModeleJeu modele = new ModeleJeu(30, 30, joueurs);
               
                /* ===================== CONTROLLER ===================== */

                     GameController controller = new GameController(modele, null);

                /* ===================== VUE ===================== */

                GUI gui = new GUI(controller);

                controller.setVue(gui);

                System.out.println("✓ Interface initialisée");
                System.out.println("✓ ModeleJeuThread activé");
                System.out.println("🎮 Prêt à jouer !");

            } catch (Exception e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Erreur au démarrage:\n" + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        });
    }
}
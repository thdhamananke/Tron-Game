import view.*;
import controller.GameController;
import model.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                System.out.println("╔════════════════════════════════════════════╗");
                System.out.println("║    🎮 JEU DE TRON - MODE GRAPHIQUE 🎮     ║");
                System.out.println("╚════════════════════════════════════════════╝\n");

                // Afficher le dialogue de configuration
                ConfigurationDialog dialog = new ConfigurationDialog(null);
                dialog.setVisible(true);

                if (!dialog.isConfirmed()) {
                    System.out.println("Configuration annulée. Au revoir !");
                    System.exit(0);
                }

                List<Player> joueurs = dialog.getJoueurs();
                int rows = dialog.getNbLignes();
                int cols = dialog.getNbColonnes();

                // Créer le modèle
                ModeleJeu modele = new ModeleJeu(rows, cols, joueurs);

                // Créer le contrôleur (sans vue pour l'instant)
                GameController controller = new GameController(modele, null);

                // Créer la vue
                GUI gui = new GUI(controller);
                controller.setVue(gui);

                System.out.println("✓ Partie configurée avec " + joueurs.size() + " joueurs.");
                System.out.println("✓ Plateau " + rows + "x" + cols);
                System.out.println("🎮 Prêt à jouer !");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur au démarrage :\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
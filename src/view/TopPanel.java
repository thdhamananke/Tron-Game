package view;

import controller.GameController;
import model.Player;
import model.Team;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel supérieur affichant les informations de la partie.
 */
public class TopPanel extends JPanel {

    private JLabel tourLabel;
    private JLabel statutLabel;
    private JLabel gagnantLabel;
    private JLabel configLabel;

    public TopPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 248, 255));

        JPanel mainInfo = new JPanel(new GridLayout(2, 2, 15, 10));
        mainInfo.setBackground(new Color(240, 248, 255));

        tourLabel = new JLabel("0", SwingConstants.CENTER);
        statutLabel = new JLabel("Prêt", SwingConstants.CENTER);
        gagnantLabel = new JLabel("-", SwingConstants.CENTER);
        configLabel = new JLabel("", SwingConstants.CENTER);

        mainInfo.add(createInfoBox("🕐 TOUR", tourLabel, new Color(135, 206, 250)));
        mainInfo.add(createInfoBox("📊 STATUT", statutLabel, new Color(144, 238, 144)));
        mainInfo.add(createInfoBox("🏆 GAGNANT", gagnantLabel, new Color(255, 215, 0)));
        mainInfo.add(createInfoBox("⚙️ CONFIG", configLabel, new Color(221, 160, 221)));

        add(mainInfo, BorderLayout.CENTER);
    }

    private JPanel createInfoBox(String title, JLabel label, Color accentColor) {
        JPanel box = new JPanel(new BorderLayout(5, 5));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setForeground(accentColor.darker());

        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(label, BorderLayout.CENTER);

        return box;
    }

    /**
     * Met à jour l'affichage avec les informations du jeu.
     */
    public void update(int tour, String statut, String gagnant, GameController controller) {
        tourLabel.setText(String.valueOf(tour));
        statutLabel.setText(statut);
        gagnantLabel.setText(gagnant);

        if (controller != null && controller.getGame() != null) {
            List<Player> joueurs = controller.getGame().getJoueurs();
            StringBuilder sb = new StringBuilder();
            for (Player p : joueurs) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(p.getTeam().getName().charAt(0))
                  .append(":")
                  .append(p.getStrategie().getName())
                  .append("+")
                  .append(p.getHeuristic().getName());
            }
            configLabel.setText(sb.toString());
        } else {
            configLabel.setText("");
        }

        // Coloration du statut
        switch (statut) {
            case "Prêt" -> statutLabel.setForeground(new Color(34, 139, 34));
            case "En cours" -> statutLabel.setForeground(new Color(0, 100, 200));
            case "En pause" -> statutLabel.setForeground(new Color(255, 140, 0));
            case "Terminé" -> statutLabel.setForeground(new Color(178, 34, 34));
            default -> statutLabel.setForeground(Color.BLACK);
        }
    }
}
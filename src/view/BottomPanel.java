package view;

import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel inférieur avec légende des joueurs - CORRIGÉ
 */
public class BottomPanel extends JPanel {

    private JPanel legendPanel;

    public BottomPanel() {
        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Légende:", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        add(title, BorderLayout.WEST);

        legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setBackground(new java.awt.Color(245, 245, 245));
        add(legendPanel, BorderLayout.CENTER);
    }

    /**
     * 🔥 Met à jour la légende avec la liste des joueurs
     */
    public void updateLegend(List<Player> joueurs) {
        legendPanel.removeAll();

        if (joueurs == null || joueurs.isEmpty()) {
            legendPanel.revalidate();
            legendPanel.repaint();
            return;
        }

        for (Player player : joueurs) {
            JPanel entry = createLegendEntry(player);
            legendPanel.add(entry);
        }

        legendPanel.revalidate();
        legendPanel.repaint();
    }

    /**
     * Crée une entrée de légende pour un joueur
     */
    private JPanel createLegendEntry(Player player) {
        JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        entry.setBackground(new java.awt.Color(245, 245, 245));

        // Carré coloré
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(player.getColor().toAWT());
        colorBox.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK, 1));

        // Nom
        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        // Statut
        String statut = player.isAlive() ? "✓" : "✗";
        JLabel statusLabel = new JLabel(statut);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusLabel.setForeground(player.isAlive() ? 
            new java.awt.Color(34, 139, 34) : 
            new java.awt.Color(178, 34, 34)
        );

        entry.add(colorBox);
        entry.add(nameLabel);
        entry.add(statusLabel);

        return entry;
    }
}
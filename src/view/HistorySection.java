package view;

import controller.GameController;
import controller.GameHistory;

import javax.swing.*;
import java.awt.*;

/**
 * Section Historique - Affiche les stats des parties (comme console)
 */
public class HistorySection extends JPanel {

    private final GameController controller;
    private JTextArea historyText;

    public HistorySection(GameController controller) {
        
        this.controller = controller;

        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                "📊 Historique",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        // Zone de texte pour les stats
        historyText = new JTextArea(6, 20);
        historyText.setEditable(false);
        historyText.setFont(new Font("Monospaced", Font.PLAIN, 10));
        historyText.setBackground(new Color(250, 250, 250));
        
        JScrollPane scroll = new JScrollPane(historyText);
        add(scroll, BorderLayout.CENTER);

        // Boutons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonsPanel.setBackground(Color.WHITE);

        JButton viewButton = new JButton("📋 Voir Stats");
        viewButton.setFont(new Font("Arial", Font.PLAIN, 10));
        viewButton.addActionListener(e -> afficherStats());

        JButton clearButton = new JButton("🗑️ Effacer");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 10));
        clearButton.addActionListener(e -> effacerHistorique());

        buttonsPanel.add(viewButton);
        buttonsPanel.add(clearButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        // Afficher les stats au démarrage
        updateDisplay();
    }

    /**
     * Met à jour l'affichage des stats
     */
    private void updateDisplay() {
        GameHistory history = controller.getHistory();
        String stats = history.getStatistiques();
        historyText.setText(stats);
    }

    /**
     * Affiche les statistiques dans une fenêtre
     */
    private void afficherStats() {
        updateDisplay();
        
        GameHistory history = controller.getHistory();
        
        if (history.getNombreParties() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Aucune partie jouée pour le moment.",
                "Historique",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        sb.append("     HISTORIQUE DES PARTIES\n");
        sb.append("═══════════════════════════════════\n\n");
        sb.append(history.getStatistiques());
        sb.append("\n\n═══════════════════════════════════\n");
        sb.append("DERNIÈRES PARTIES:\n");
        sb.append("═══════════════════════════════════\n\n");
        
        int count = 0;
        for (var record : history.getParties()) {
            sb.append(record.toString()).append("\n");
            count++;
            if (count >= 5) break; // Limiter à 5 dernières parties
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(60);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "📊 Statistiques Complètes",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Efface l'historique
     */
    private void effacerHistorique() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment effacer tout l'historique ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            controller.getHistory().effacer();
            updateDisplay();
            JOptionPane.showMessageDialog(
                this,
                "Historique effacé !",
                "Confirmation",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
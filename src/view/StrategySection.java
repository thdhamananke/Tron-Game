package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;

public class StrategySection extends JPanel {
    private final GameController controller;
    private final GUI gui;

    private JComboBox<String> stratRougeBox;
    private JComboBox<String> heurRougeBox;
    private JSpinner depthRougeSpinner;
    private JComboBox<String> stratBleuBox;
    private JComboBox<String> heurBleuBox;
    private JSpinner depthBleuSpinner;

    public StrategySection(GameController controller, GUI gui) {
        this.controller = controller;
        this.gui = gui;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "⚙️ Paramètres IA",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)
        ));
        setBackground(new Color(245, 248, 255));

        add(createPlayerPanel(true));
        add(Box.createVerticalStrut(15));
        add(createPlayerPanel(false));

        JButton applyButton = new JButton("✓ Appliquer");
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyButton.setBackground(new Color(34, 139, 34));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setFont(new Font("Arial", Font.BOLD, 12));
        applyButton.addActionListener(e -> appliquer());
        add(Box.createVerticalStrut(10));
        add(applyButton);
    }

    private JPanel createPlayerPanel(boolean rouge) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255));

        String emoji = rouge ? "🔴" : "🔵";
        Color borderColor = rouge ? new Color(220, 20, 60) : new Color(30, 144, 255);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                emoji + " Bot " + (rouge ? "Rouge" : "Bleu"),
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        String[] strategies = {"Random", "MinMax", "AlphaBeta", "MaxN", "Paranoid", "SOS"};
        String[] heuristiques = {"FreeSpace", "Voronoi", "TreeOfChambers"};

        JComboBox<String> stratBox = new JComboBox<>(strategies);
        stratBox.setSelectedItem("AlphaBeta");
        JComboBox<String> heurBox = new JComboBox<>(heuristiques);
        heurBox.setSelectedItem("FreeSpace");
        JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        panel.add(Box.createVerticalStrut(5));
        panel.add(createLabeledComponent("Stratégie:", stratBox));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLabeledComponent("Heuristique:", heurBox));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLabeledComponent("Profondeur:", depthSpinner));
        panel.add(Box.createVerticalStrut(5));

        if (rouge) {
            stratRougeBox = stratBox;
            heurRougeBox = heurBox;
            depthRougeSpinner = depthSpinner;
        } else {
            stratBleuBox = stratBox;
            heurBleuBox = heurBox;
            depthBleuSpinner = depthSpinner;
        }
        return panel;
    }

    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setPreferredSize(new Dimension(90, 20));
        panel.add(label);
        panel.add(component);
        return panel;
    }

    private void appliquer() {
        String stratRouge = (String) stratRougeBox.getSelectedItem();
        String heurRouge = (String) heurRougeBox.getSelectedItem();
        int depthRouge = (int) depthRougeSpinner.getValue();

        String stratBleu = (String) stratBleuBox.getSelectedItem();
        String heurBleu = (String) heurBleuBox.getSelectedItem();
        int depthBleu = (int) depthBleuSpinner.getValue();

        // Mettre à jour les deux premiers joueurs (supposés rouge et bleu)
        controller.setPlayerStrategy(0, stratRouge, heurRouge, depthRouge);
        controller.setPlayerStrategy(1, stratBleu, heurBleu, depthBleu);

        JOptionPane.showMessageDialog(this,
                "Stratégies mises à jour.\nRedémarrez la partie si elle est en cours.",
                "Configuration",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
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
        setBorder(BorderFactory.createTitledBorder("Paramètres IA"));

        add(createPlayerPanel(true));
        add(Box.createVerticalStrut(15));
        add(createPlayerPanel(false));

        JButton applyButton = new JButton("Appliquer");
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyButton.addActionListener(e -> appliquer());

        add(Box.createVerticalStrut(10));
        add(applyButton);
    }

    private JPanel createPlayerPanel(boolean rouge) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                rouge ? "Bot Rouge" : "Bot Bleu"));

        JComboBox<String> stratBox =
                new JComboBox<>(new String[]{
                        "Random",
                        "MinMax",
                        "AlphaBeta"
                });

        JComboBox<String> heurBox =
                new JComboBox<>(new String[]{
                        "FreeSpace",
                        "Advanced"
                });

        JSpinner depthSpinner =
                new JSpinner(new SpinnerNumberModel(4, 1, 10, 1));

        panel.add(new JLabel("Stratégie"));
        panel.add(stratBox);
        panel.add(new JLabel("Heuristique"));
        panel.add(heurBox);
        panel.add(new JLabel("Profondeur"));
        panel.add(depthSpinner);

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

    private void appliquer() {

        controller.appliquerStrategieComplete(
                (String) stratRougeBox.getSelectedItem(),
                (String) heurRougeBox.getSelectedItem(),
                (int) depthRougeSpinner.getValue(),
                (String) stratBleuBox.getSelectedItem(),
                (String) heurBleuBox.getSelectedItem(),
                (int) depthBleuSpinner.getValue()
        );

       
    }
}

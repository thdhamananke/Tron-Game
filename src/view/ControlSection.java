package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class ControlSection extends JPanel {

    private final GameController controller;
    private JButton startButton;

    public ControlSection(GameController controller) {

        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Boutons de contrôles"));

        startButton = createButton("▶ Démarrer", new java.awt.Color(0,200,0));
        JButton pauseButton = createButton("⏸ Pause", new java.awt.Color(255,200,0));
        JButton restartButton = createButton("⟲ Redémarrer", new java.awt.Color(220,0,0));
        JButton annulerButton = createButton("x Annuler", new java.awt.Color(180,180,180));

        startButton.addActionListener(e -> {
            controller.demarrerJeu();
            startButton.setEnabled(false);
        });
    
        annulerButton.addActionListener(e -> {
            controller.fermerApplication(); 
        });

        pauseButton.addActionListener(e -> controller.togglePause());
        restartButton.addActionListener(e -> restart());

        add(startButton);
        add(Box.createVerticalStrut(5)); 
        add(pauseButton);
        add(Box.createVerticalStrut(5));
        add(restartButton);
        add(Box.createVerticalStrut(5)); 
        add(annulerButton);
    }

    private JButton createButton(String text, java.awt.Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setOpaque(true);
        
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        
        return btn;
    }

    public void restart() {
        controller.restart();
        startButton.setEnabled(true);
    }

    public void enableStart() {
        startButton.setEnabled(true);
    }
}

package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

/**
 * Panel latéral complet.
 */
public class SidePanel extends JPanel {

    private final GameController controller;
    private final GUI gui;
    private final GameBoardPanel gameBoard;

    public SidePanel(GameController controller, GUI gui, GameBoardPanel gameBoard) {
        this.controller = controller;
        this.gui = gui;
        this.gameBoard = gameBoard;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTitle());
        add(Box.createVerticalStrut(10));
        
        // ControlSection newcControlSection = ;
        add(new ControlSection(controller));
        add(Box.createVerticalStrut(10));
        // newcControlSection.setLocation(0,0);

        // StrategySection
        add(new StrategySection(controller, gui));
        add(Box.createVerticalStrut(10));

        // ConfigurationSection
        add(new ConfigSection(controller, gameBoard));
        add(Box.createVerticalStrut(10));

        // ObstacleSection
        add(new ObstacleSection(gameBoard));
        add(Box.createVerticalStrut(10));

        // SpeedSection
        add(new SpeedSection(controller));
        add(Box.createVerticalStrut(10));

        // HistorySection
        add(new HistorySection(controller));
        add(Box.createVerticalGlue());
    }

    private JPanel createTitle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel title = new JLabel("⚙️ CONTRÔLES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        panel.add(title, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return panel;
    }
}